package info.agilite.spring.base.database;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;

import info.agilite.spring.base.AgiliteAbstractEntity;
import info.agilite.spring.base.metadata.EntitiesMetadata;
import info.agilite.spring.base.metadata.OneToManyMetadata;
import info.agilite.utils.StringUtils;
import info.agilite.utils.Utils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class HibernateWrapper {
	EntityManager em;
	
	public <T extends AgiliteAbstractEntity> T load(Class<T> clazz, Long id){
		return session().load(clazz, id);
	}
	public <T extends AgiliteAbstractEntity> T get(Class<T> clazz, Long id){
		return session().get(clazz, id);
	}
	public void persist(AgiliteAbstractEntity entity){
		//FIXME empresa
		session().saveOrUpdate(entity);
	}
	public void flush() {
		session().flush();
	}
	public void persistForceRemoveChildren(AgiliteAbstractEntity entity){
		//FIXME empresa
		this.persistForceRemoveChildren(entity, (child) -> true);
	}
	public void persistForceRemoveChildren(AgiliteAbstractEntity entity, Predicate<String> deleteChildren){
		//FIXME empresa
		Session s = session();
		
		if(((AgiliteAbstractEntity)entity).getIdValue() != null){
			cascadeDeleteChildren(entity, s, deleteChildren);
		}
		
		s.saveOrUpdate(entity);
	}
	
	public void delete(AgiliteAbstractEntity entity){
		session().delete(entity);
	}
	public void delete(Class<? extends AgiliteAbstractEntity> clazz, Long id){
		session().delete(clazz.getName(), load(clazz, id));
	}
	public void delete(Class<? extends AgiliteAbstractEntity> clazz, List<Long> ids){
		String className = clazz.getSimpleName();
		session().createQuery("DELETE FROM " + className + " WHERE " + className.toLowerCase() + "id IN :ids")
			.setParameter("ids", ids)
			.executeUpdate();
	}
	
	public <T> List<T> findAll(Class<T> clazz){
		//FIXME empresa
		return session()
				.createQuery("FROM ".concat(clazz.getSimpleName()).concat(" ORDER BY ").concat(clazz.getSimpleName()).concat("id"), clazz)
				.list();
	}
	public <T> List<T> findAll(Class<T> clazz, List<Long> ids){
		//FIXME empresa
		String columnId = clazz.getSimpleName().concat("id");
		return session()
				.createQuery("FROM ".concat(clazz.getSimpleName())
						.concat(" WHERE ").concat(columnId).concat(" IN (:ids)")
						.concat(" ORDER BY ").concat(columnId), clazz)
				.setParameter("ids", ids)
				.list();
	}
	public <T> List<T> findAll(Class<T> clazz, String orderBy){
		//FIXME empresa
		return session()
				.createQuery("FROM ".concat(clazz.getSimpleName()).concat(" ORDER BY ").concat(orderBy), clazz)
				.list();
	}
	
	
	public <T> T findUniqueByQuery(Class<T> clazz, String query, Map<Object, Object> params){
		Query<T> q = session().createQuery(query, clazz);
		
		setQueryParams(q, params);
		
		return q.uniqueResult();
	}
	public <T> List<T> findListByQuery(Class<T> clazz, String query, Map<Object, Object> params){
		Query<T> q = session().createQuery(query, clazz);
		
		setQueryParams(q, params);
		
		return q.list();
	}
	
	public <T> T findUniqueByEquals(Class<T> clazz, Map<Object, Object> paramsEquals){
		Query<T> q = createEqualsQuery(clazz, paramsEquals);
		
		setQueryParams(q, paramsEquals);
		
		return q.uniqueResult();
	}
	
	public <T> List<T> findListByEquals(Class<T> clazz, Map<Object, Object> params){
		//FIXME empresa
		Query<T> query = createEqualsQueryOrderBy(clazz, null, params);
		
		return query.list();
	}
	public <T> List<T> findListByEqualsOrderBy(Class<T> clazz, String orderBy, Map<Object, Object> params){
		//FIXME empresa
		Query<T> query = createEqualsQueryOrderBy(clazz, orderBy, params);
		
		return query.list();
	}
	
	public <T> T findUniqueFieldByEquals(Class<T> returnType, Class<? extends AgiliteAbstractEntity> entityClass, String field, Map<Object, Object> paramsEquals){
		String where = createWhereByParams(paramsEquals);
		Query<T> query = session()
				.createQuery(
						"SELECT ".concat(field)
						.concat("FROM ").concat(entityClass.getSimpleName())
						.concat(where), returnType);
		
		setQueryParams(query, paramsEquals);
		return query.uniqueResult();
	}
	
	public Query<?> nativeQuery(String nativeQuery){
		return session().createNativeQuery(nativeQuery);
	}
	public Query<?> nativeQuery(String nativeQuery, Map<Object, Object> params){
		NativeQuery<?> q = session().createNativeQuery(nativeQuery);
		setQueryParams(q, params);
		return q;
	}
	public Query<?> nativeQuery(Class<?> clazz, String nativeQuery){
		return session().createNativeQuery(nativeQuery, clazz);
	}
	public Query<?> nativeQuery(Class<?> clazz, String nativeQuery, Map<Object, Object> params){
		NativeQuery<?> q = session().createNativeQuery(nativeQuery, clazz);
		setQueryParams(q, params);
		return q;
	}
	
	public Query<?> query(String query){
		return session().createQuery(query);
	}
	public Query<?> query(String query,  Map<Object, Object> params){
		Query<?> q = session().createQuery(query);
		setQueryParams(q, params);
		return q;
	}
	public <T> Query<T> query(String query, Class<T> clazz){
		return session().createQuery(query, clazz);
	}
	public <T> Query<T> query(String query, Class<T> clazz, Map<Object, Object> params){
		Query<T> q = session().createQuery(query, clazz);
		setQueryParams(q, params);
		return q;
	}
	
	
	public <T> Query<T> createEqualsQuery(Class<T> clazz, Map<Object, Object> paramsEquals){
		//FIXME empresa
		return createEqualsQueryOrderBy(clazz, null, paramsEquals);
	}
	public <T> Query<T> createEqualsQueryOrderBy(Class<T> clazz, String orderBy,  Map<Object, Object> paramsEquals){
		//FIXME empresa
		if(orderBy == null){
			orderBy = clazz.getSimpleName().toLowerCase().concat("id");
		}
		
		String where = createWhereByParams(paramsEquals);
		
		Query<T> query = session()
				.createQuery(
						"FROM "
						.concat(clazz.getSimpleName())
						.concat(where)
						.concat(" ORDER BY ")
						.concat(orderBy), clazz);
		
		setQueryParams(query, paramsEquals);
		return query;
	}
	
	public Session session() {
		return em.unwrap(Session.class);
	}
	
	private String createWhereByParams(Map<Object, Object> paramsEquals) {
		if(paramsEquals == null || paramsEquals.size() == 0)return " ";
		
		boolean hasParamNull = paramsEquals.values().stream().filter(val -> val == null).findFirst().isPresent();
		if(hasParamNull){
			throw new RuntimeException("Parâmetro com valor nulo não é permitido no find");
		}
		
		String where = paramsEquals.keySet().stream()
				.map(val -> val.toString())
				.map(val -> {
					boolean isString = EntitiesMetadata.INSTANCE.getPropertyByName(val).getType() == String.class;
					if(isString){
						return "LOWER(".concat(val).concat(") = LOWER(:").concat(val).concat(")");
					}else{
						return val.concat(" = :").concat(normalizeParamName(val));
					}
				})
				.collect(Collectors.joining(" AND "));
		return " WHERE " + where;
	}
	
	private String normalizeParamName(String val) {
		return val.replace(".", "_");
	}
	
	private void cascadeDeleteChildren(Object entity, Session s, Predicate<String> deleteChildren) {
		List<OneToManyMetadata> ones = EntitiesMetadata.INSTANCE.getOneToManysByTable(entity.getClass().getSimpleName());
		if(!Utils.isEmpty(ones)){
			ones.forEach(one -> {
				if(deleteChildren.test(one.getNome())){
					if(one.getMethodGet() == null){
						try {
							one.setMethodGet(entity.getClass().getMethod("get" + StringUtils.capitalize(one.getNome()), (Class[])null));
						} catch (NoSuchMethodException | SecurityException e) {
							throw new RuntimeException("Método GET não localizado na classe pai 'get" + StringUtils.capitalize(one.getNome()) + "'", e);
						}
					}
					try {
						Object result = one.getMethodGet().invoke(entity, (Object[])null);
						if(result != null){
							List<Long> idsFilhosAtuais = ((Collection<AgiliteAbstractEntity>)result).stream()
									.map(AgiliteAbstractEntity::getIdValue)
									.filter(id -> id != null)
									.collect(Collectors.toList());
							
							if(idsFilhosAtuais.size() == 0)idsFilhosAtuais.add(-1L);
							
							s.createQuery(StringUtils.concat(" DELETE FROM ", one.getClasse(), " WHERE ", one.getClasse(), "id NOT IN (:idsFilhosAtuais) AND ", one.getJoinColumn(), ".", entity.getClass().getSimpleName().toLowerCase(), "id = :parentId"))
								.setParameter("idsFilhosAtuais", idsFilhosAtuais)
								.setParameter("parentId", ((AgiliteAbstractEntity)entity).getIdValue())
								.executeUpdate();
						}
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new RuntimeException("Erro ao executar método get da classe pai 'get" + StringUtils.capitalize(one.getNome()) + "'", e);
					}
				}
			});
		}
	}
	public void setQueryParams(Query<?> q, Map<Object, Object> parametros) {
		for(Object key : parametros.keySet()){
			q.setParameter(normalizeParamName(key.toString()), parametros.get(key));
		}
	}
}
