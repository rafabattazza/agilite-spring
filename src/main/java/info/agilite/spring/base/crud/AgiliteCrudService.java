package info.agilite.spring.base.crud;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import info.agilite.spring.base.database.HibernateWrapper;
import info.agilite.spring.base.metadata.EntitiesMetadata;
import info.agilite.spring.base.metadata.OneToManyMetadata;
import info.agilite.spring.base.metadata.PropertyMetadata;
import info.agilite.utils.StringUtils;
import info.agilite.utils.Utils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level=AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AgiliteCrudService {
	final HibernateWrapper hibernate;

	@Value("${info.agilite.spring.entity.base-package}")
	private String entityBasePackage;

	//TODO validar o delete do Ax01 no CD01020
	public void saveEntity(Object entity) {
		hibernate.saveOrUpdateCascade(entity);
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
		result.append(createListJoin(crudRequest, alias));

		String where = createWhere(classe, crudRequest, alias);
		if(!StringUtils.isNullOrEmpty(where)) {
			result.append(where);
		}
		
		return result.toString();
	}
	
	private String createWhere(String classe, CrudListRequest request, String alias) {
		String simpleWhere = createSimpleFilter(request, alias);
		String defaultWhere = request.getDefaultFilter();
		String inactiveWhere = createArchiveWhere(classe, request, alias);
		String completeWhere = createCompleteWhere(request, alias);
		
		return StringUtils.concat(" WHERE ", 
				StringUtils.firstNotEmpty(simpleWhere, " 1 = 1 "), 
				" AND ",
				StringUtils.firstNotEmpty(defaultWhere, " 1 = 1 "),
				" AND ",
				StringUtils.firstNotEmpty(inactiveWhere, " 1 = 1 "),
				" AND ",
				StringUtils.firstNotEmpty(completeWhere, " 1 = 1 ")
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
						.map(field -> StringUtils.concat(" COALESCE(CAST(", alias, ".", field, " AS string), '') "))
						.collect(Collectors.joining(", "));

				where.append(fields).append(")) LIKE :simple_fielter_value ");
				return where.toString();
			}
		}
		return null;
	}
	
	private String createCompleteWhere(CrudListRequest request, String alias) {
		if(request.getCompleteFilters() != null && !Utils.isEmpty(request.getCompleteFilters().getValues())) {
			List<CrudListCompleteFilterValue> filterFields = request.getCompleteFilters().getValues()
					.stream()
					.collect(Collectors.toList());

			if(filterFields.size() > 0) {
				return filterFields.stream()
						.map(field -> StringUtils.concat(" LOWER(COALESCE(CAST(", alias, ".", field.getName(), " AS string), '')) LIKE :", field.getName().replace(".", "_")))
						.collect(Collectors.joining(" AND "));
			}
		}
		return null;
	}
	
	private String createArchiveWhere(String classe, CrudListRequest request, String alias) {
		PropertyMetadata propertyExcluido = EntitiesMetadata.INSTANCE.getPropertyByName(StringUtils.concat(classe, "arquivado"));
		if(propertyExcluido != null) {
			if(request.getCompleteFilters() != null && request.getCompleteFilters().isShowArchivedOnly()) {
				return StringUtils.concat(propertyExcluido.getNome(), " = 1");
			}else {
				return StringUtils.concat(" (", propertyExcluido.getNome(), " IS NULL) ");
			}
		}
		return null;
	}

	private Long findCount(CrudListRequest crudRequest, String baseSelect) {
		Query<Long> queryCount = hibernate.query(StringUtils.concat(" SELECT count(*) AS qtd", baseSelect), Long.class);
		setFilterParameters(crudRequest, queryCount);
		Long qtdRegistros = queryCount.uniqueResult();
		return qtdRegistros;
	}
	
	private void setFilterParameters(CrudListRequest request, Query query) {
		if(!StringUtils.isNullOrEmpty(request.getSimpleFilterValue())){
			query.setParameter("simple_fielter_value", "%" + request.getSimpleFilterValue().toLowerCase() + "%");
		}
		
		if(request.getCompleteFilters() != null && !Utils.isEmpty(request.getCompleteFilters().getValues())){
			request.getCompleteFilters().getValues().forEach(value -> query.setParameter(value.getName(), "%" + value.getValue() + "%"));
		}
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
		Query query = hibernate.query(select.toString());
		setFilterParameters(crudRequest, query);
		
		if(crudRequest.getPage() == null || crudRequest.getPage() == 0)crudRequest.setPage(1);
		if(crudRequest.getRowsPerPage() == null)crudRequest.setRowsPerPage(50);
		
		query.setMaxResults(crudRequest.getRowsPerPage());
		query.setFirstResult(crudRequest.getRowsPerPage() * (crudRequest.getPage()-1));
		
		List<Object[]> values = query.list();
		return Utils.convertJpaListTupleToNestedMap("id$|$" + crudRequest.getColumns().stream().collect(Collectors.joining("$|$")), "$|$", values);
	}
	
	private String getFields(CrudListRequest request, String alias) {
		return StringUtils.concat(alias, ".",  alias, "id as id, ", request.getColumns().stream().map(column -> {
			if(column.startsWith("NATIVE$")) {
				return column.replace("NATIVE$", "");
			}else{
				return alias + "." + column;
			}
		}).collect(Collectors.joining(",")));
	}
	
	private String createListJoin(CrudListRequest crudRequest, String alias) {
		Set<String> fks = crudRequest.getColumns().stream()
			.filter(col -> col.indexOf(".") > 0)
			.filter(col -> !col.startsWith("NATIVE$"))
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
		String alias = entityName.toLowerCase();
		@SuppressWarnings("rawtypes")
		Query query = hibernate.query(StringUtils.concat(
				createFromWithFetch(entityName, alias), 
				" WHERE ",
				alias, 
				".id = :id")
			);
		query.setParameter("id", idEntity);
		
		return query.uniqueResult();
	}

	public void archive(String entityName, List<Long> ids) {
		hibernate.query("UPDATE " + entityName + " SET " + entityName + "arquivado = :arquivado WHERE " + entityName +"id IN (:ids)")
		  .setParameter("arquivado", 1)
		  .setParameter("ids", ids)
		  .executeUpdate();
	}
	
	public void unarchive(String entityName, List<Long> ids) {
		hibernate.query("UPDATE " + entityName + " SET " + entityName + "arquivado = null WHERE " + entityName +"id IN (:ids)")
		  .setParameter("ids", ids)
		  .executeUpdate();
	}
	
	public void delete(String entityName, Long id) {
		try {
			hibernate.delete(getEntityClass(entityName), id);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Erro ao excluir entidade", e);
		}
	}
	
	private String createFromWithFetch(String entityName, String alias){
		String result = StringUtils.concat(" FROM ",  entityName, " ", alias);
		
		List<PropertyMetadata> properties = EntitiesMetadata.INSTANCE.getPropertiesByTable(entityName);
		List<PropertyMetadata> fks = properties.stream().filter(entity -> entity.isFk()).collect(Collectors.toList());
		if(!Utils.isEmpty(fks)){
			for(PropertyMetadata fk : fks){
				result = StringUtils.concat(result, (fk.isRequired() ? " INNER " : " LEFT "), " JOIN FETCH ", alias, ".", fk.getNome());
			};
		}
		List<OneToManyMetadata> ones = EntitiesMetadata.INSTANCE.getOneToManysByTable(entityName);
		if(!Utils.isEmpty(ones)){
			for(OneToManyMetadata one : ones){
				result = StringUtils.concat(result, " LEFT JOIN FETCH ", alias, ".", one.getNome(),  " as ", one.getNome());
			}
		}
		
		return result;
	}

}