package info.agilite.spring.base.crud;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import info.agilite.utils.StringUtils;
import info.agilite.utils.Utils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level=AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AgiliteCrudService {
	final EntityManager em;

	@Value("${info.agilite.spring.entity.base-package}")
	private String entityBasePackage;

	//TODO validar o delete do Ax01 no CD01020
	public void saveEntity(Object entity) {
		Session session = em.unwrap(Session.class);
	    session.saveOrUpdate(entity);
	}
	
	public Class<?> getEntityClass(String entityName) throws ClassNotFoundException {
		Class<?> entityClass = Class.forName(entityBasePackage + "." + StringUtils.capitalize(entityName));
		return entityClass;
	}
	
	public CrudListResponse list(String classe, CrudListRequest crudRequest) {
		String alias = classe.toLowerCase();
		
		String sqlBase = createSQLBaseToList(classe, crudRequest, alias);
		Long count = findCount(crudRequest, sqlBase);
		
		List<Map<String, Object>> data = findData(crudRequest, sqlBase, alias);
		if(data.size() == 0 && crudRequest.getPage() > 0) {
			crudRequest.setPage(0);
			if(count > 0) {
				//refazer com página 0
				this.list(classe, crudRequest);
			}
		}
		
		return new CrudListResponse(data, new CrudListPagination(
				crudRequest.getSortBy(), 
				crudRequest.isDesc(), 
				crudRequest.getPage(), 
				crudRequest.getRowsPerPage(), 
				count.intValue()
			)
		);
	}

	private String createSQLBaseToList(String classe, CrudListRequest crudRequest, String alias) {
		StringBuilder result = new StringBuilder();
		
		result.append(" FROM ").append(classe).append(" ").append(alias);
		result.append(createJoins(crudRequest, alias));

		String where = createWhere(crudRequest, alias);
		if(!StringUtils.isNullOrEmpty(where)) {
			result.append(where);
		}
		
		return result.toString();
	}
	
	private String createWhere(CrudListRequest request, String alias) {
		String simpleWhere = createSimpleFilter(request, alias);
		String defaultWhere = request.getDefaultFilter();
		
		return StringUtils.concat(" WHERE ", 
				StringUtils.firstNotEmpty(simpleWhere, " 1 = 1 "), 
				" AND ",
				StringUtils.firstNotEmpty(defaultWhere, " 1 = 1 ")
			);
	}

	private String createSimpleFilter(CrudListRequest request, String alias) {
		if(!StringUtils.isNullOrEmpty(request.getSimpleFilterValue())) {
			List<String> filterFields = request.getColumnsSimpleFilter()
					.stream()
					.collect(Collectors.toList());

			if(filterFields.size() > 0) {
				StringBuilder where = new StringBuilder(" LOWER(CONCAT(");

				String fields = filterFields.stream()
						.map(field -> StringUtils.concat(" CAST(COALESCE(", alias, ".", field, ", '') AS string)"))
						.collect(Collectors.joining(", "));

				where.append(fields).append(")) LIKE :simple_fielter_value ");
				return where.toString();
			}
		}
		return "";
	}

	private Long findCount(CrudListRequest crudRequest, String baseSelect) {
		Query<Long> queryCount = em.unwrap(Session.class).createQuery(StringUtils.concat(" SELECT count(*) AS qtd", baseSelect), Long.class);
		setFilterParameters(crudRequest, queryCount);
		Long qtdRegistros = queryCount.getSingleResult();
		return qtdRegistros;
	}
	
	private void setFilterParameters(CrudListRequest request, Query query) {
		if(StringUtils.isNullOrEmpty(request.getSimpleFilterValue()))return;
		query.setParameter("simple_fielter_value", "%" + request.getSimpleFilterValue().toLowerCase() + "%");
	}
	
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> findData(CrudListRequest crudRequest, String baseSelect, String alias){
		String fields = getFields(crudRequest, alias);
		
		StringBuilder select = new StringBuilder(" SELECT ");
		select.append(fields);
		select.append(baseSelect);
		
		if(!Utils.isEmpty(crudRequest.getSortBy())) {
			select.append(" ORDER BY ").append(alias).append(".").append(crudRequest.getSortBy());
			if(crudRequest.isDesc()) {
				select.append(" DESC ");
			}
		}
		Query query = em.unwrap(Session.class).createQuery(select.toString());
		setFilterParameters(crudRequest, query);
		
		if(crudRequest.getPage() == null || crudRequest.getPage() == 0)crudRequest.setPage(1);
		if(crudRequest.getRowsPerPage() == null)crudRequest.setRowsPerPage(50);
		
		query.setMaxResults(crudRequest.getRowsPerPage());
		query.setFirstResult(crudRequest.getRowsPerPage() * (crudRequest.getPage()-1));
		
		List<Object[]> values = query.getResultList();
		return Utils.convertJpaListTupleToNestedMap("id, " + crudRequest.getColumns().stream().collect(Collectors.joining(",")), values);
	}
	
	private String getFields(CrudListRequest request, String alias) {
		return StringUtils.concat(alias, ".",  alias, "id as id, ", request.getColumns().stream().map(column -> (alias + "." + column)).collect(Collectors.joining(",")));
	}
	
	private String createJoins(CrudListRequest crudRequest, String alias) {
		Set<String> fks = crudRequest.getColumns().stream()
			.filter(col -> col.indexOf(".") > 0)
			.map(col -> col.substring(0, col.indexOf(".")))
			.collect(Collectors.toSet());
		if(Utils.isEmpty(fks))return "";
		
		String joins = fks
				.stream()
				.map(join -> StringUtils.concat(
						" LEFT JOIN ", 
						alias, ".", join, 
						" AS " + join,
						" "))
				.collect(Collectors.joining(" "));
		return joins;
	}
	
	public Object findEntityById(String entityName, Long idEntity) {
		Query query = em.unwrap(Session.class).createQuery(" FROM " + entityName + " WHERE id = :id");
		query.setParameter("id", idEntity);
		
		return query.uniqueResult();
	}

	
//	private String createFilter(CrudListRequest request, String alias) {
//		if(Utils.isEmpty(request.getFilters()))return "";
//		
//		StringBuilder where = new StringBuilder();
//		int indexParametro = 0;
//		for(CrudFilter filter : request.getFilters()) {
//			if(where.length() > 0) {
//				where.append(" AND ");
//			}
//		
//			where.append(filter.getSqlField(alias)).append(" ");
//			where.append(filter.getOper().getSqlOperation()).append(" ");
//			where.append(filter.getSqlParams(indexParametro)).append(" ");
//			
//			Object[] convertedParams = filter.convertValues();
//			for(Object paramValue : convertedParams) {
//				request.addToParameter(new CrudFilterParameter(CrudFilter.PARAM_PREFIX + indexParametro, paramValue));
//				indexParametro++;
//			}
//		}
//
//		return where.toString();
//	}
//
//	
//	private String createFieldsToEdit(String entityName)  {
//		EntityMetadata entityMetadata = metadata.getEntityMetadata(entityName);
//		
//		return entityMetadata.getProperties()
//			.stream()
//			.map(prop -> {
//				if (prop.isFk()) {
//					return prop.getNome() + ".id";
//				}else {
//					return prop.getNome();
//				}
//			})
//			.collect(Collectors.joining(", "));
//	}
//	
//	
//	public void delete(String entityName, Long id) {
//		em.createQuery("DELETE FROM " + entityName + " WHERE id = :id")
//		  .setParameter("id", id)
//		  .executeUpdate();
//	}
}