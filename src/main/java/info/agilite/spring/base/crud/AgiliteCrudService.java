package info.agilite.spring.base.crud;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import info.agilite.utils.StringUtils;
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
	
	
	
//	public CrudResult list(String classe, CrudListRequest crudRequest) {
//		String alias = classe.toLowerCase();
//		
//		String sqlBase = createSQLBase(classe, crudRequest, alias);
//		Long count = findCount(crudRequest, sqlBase);
//		
//		List<Map<String, Object>> data = findData(crudRequest, sqlBase, alias);
//		
//		String order = null;
//		if(!Utils.isEmpty(crudRequest.getOrders())) {
//			order = crudRequest.getOrders().get(0).getKey();
//		}
//		
//		return new CrudResult(count, data, order);
//	}
//
//	private String createSQLBase(String classe, CrudListRequest crudRequest, String alias) {
//		StringBuilder result = new StringBuilder();
//		
//		result.append(" FROM ").append(classe).append(" ").append(alias);
//		String joins = createJoins(crudRequest, alias);
//		if(!StringUtils.isNullOrEmpty(joins)) {
//			result.append(joins);
//		}
//		
//		String where = createWhere(crudRequest, alias);
//		if(!StringUtils.isNullOrEmpty(where)) {
//			result.append(where);
//		}
//		
//		return result.toString();
//	}
//
//	private Long findCount(CrudListRequest crudRequest, String baseSelect) {
//		TypedQuery<Long> queryCount = em.createQuery(StringUtils.concat(" SELECT count(*)", baseSelect), Long.class);
//		setFilterParameters(crudRequest, queryCount);
//		Long qtdRegistros = queryCount.getSingleResult();
//		return qtdRegistros;
//	}
//	
//	
//	@SuppressWarnings("unchecked")
//	private List<Map<String, Object>> findData(CrudListRequest crudRequest, String baseSelect, String alias){
//		StringBuilder fields = new StringBuilder().append(crudRequest.getFieldsToSQL(alias));
//		
//		StringBuilder select = new StringBuilder(" SELECT ");
//		select.append(fields);
//		select.append(baseSelect);
//		
//		if(!Utils.isEmpty(crudRequest.getOrders())) {
//			select.append(" ORDER BY ").append(alias).append(".").append(crudRequest.getOrders().stream().map(CrudOrderBy::getSqlOrder).collect(Collectors.joining(", ")));
//		}
//		Query query = em.createQuery(select.toString());
//		setFilterParameters(crudRequest, query);
//		
//		if(crudRequest.getPageNumber() == null)crudRequest.setPageNumber(0);
//		if(crudRequest.getPageSize() == null)crudRequest.setPageSize(200);
//		query.setMaxResults(crudRequest.getPageSize());
//		query.setFirstResult(crudRequest.getPageNumber() * crudRequest.getPageSize());
//		
//		List<Object[]> values = query.getResultList();
//		return Utils.convertJpaListTupleToNestedMap(crudRequest.getFieldsAsString(), values);
//	}
//
//	private void setFilterParameters(CrudListRequest request, Query query) {
//		if(Utils.isEmpty(request.getParameters()))return;
//		for(CrudFilterParameter param : request.getParameters()) {
//			query.setParameter(param.getName(), param.getValue());
//		}
//	}
//	
//	private String createJoins(CrudListRequest crudRequest, String alias) {
//		if(Utils.isEmpty(crudRequest.getJoins()))return "";
//		String joins = crudRequest.getJoins()
//				.stream()
//				.map(join -> StringUtils.concat(
//						join.isLeft() ? " LEFT " : " INNER ", 
//						" JOIN ", 
//						alias, ".", join.getProperty(), 
//						join.getAlias() != null ? " AS " + join.getAlias() : "",
//						" "))
//				.collect(Collectors.joining(" "));
//		return joins;
//	}
//	
//	private String createWhere(CrudListRequest request, String alias) {
//		String simpleWhere = createSimpleFilter(request, alias);
//		String where = createFilter(request, alias);
//		
//		if(StringUtils.isNullOrEmpty(simpleWhere) && StringUtils.isNullOrEmpty(where)) {
//			return "";
//		}
//		
//		if(!StringUtils.isNullOrEmpty(simpleWhere) && !StringUtils.isNullOrEmpty(where)) {
//			return StringUtils.concat(" WHERE ", simpleWhere, " AND ", where);
//		}
//		
//		
//		return StringUtils.concat(" WHERE ", simpleWhere, where);
//	}
//
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
//	private String createSimpleFilter(CrudListRequest request, String alias) {
//		if(!StringUtils.isNullOrEmpty(request.getQuery())) {
//			List<String> filterFields = request.getFields()
//					.stream()
//					.filter(field -> field.isFilterable())
//					.map(field -> field.getKey())
//					.collect(Collectors.toList());
//			
//			if(filterFields.size() > 0) {
//				StringBuilder where = new StringBuilder(" (");
//				
//				String fields = filterFields.stream()
//					.map(field -> StringUtils.concat(" LOWER(CONCAT(", alias, ".", field, ", '')) like :simple_fielter_value "))
//					.collect(Collectors.joining(" OR "));
//				
//				where.append(fields).append(") ");
//				request.addToParameter(new CrudFilterParameter("simple_fielter_value", "%" + request.getQuery().toLowerCase() + "%"));
//				
//				return where.toString();
//			}
//		}
//		return "";
//	}
//	public Map<String, Object> findEntityById(String entityName, Long idEntity) {
//		String fields = createFieldsToEdit(entityName);
//		
//		Query query = em.createQuery("SELECT " + fields + " FROM " + entityName + " WHERE id = :id");
//		query.setParameter("id", idEntity);
//		
//		Object[] result = (Object[])query.getSingleResult();
//		return Utils.convertJpaTupleToNestedMap(fields, result);
//	}
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