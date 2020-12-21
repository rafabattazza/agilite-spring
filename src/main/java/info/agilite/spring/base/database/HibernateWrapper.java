package info.agilite.spring.base.database;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.hibernate.Session;
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
	
	public void persist(Object entity){
		session().saveOrUpdate(entity);
	}
	
	public void persistForceRemoveChildren(Object entity){
		this.persistForceRemoveChildren(entity, (child) -> true);
	}
	
	public void persistForceRemoveChildren(Object entity, Predicate<String> deleteChildren){
		Session s = session();
		
		if(((AgiliteAbstractEntity)entity).getIdValue() != null){
			cascadeDeleteChildren(entity, s, deleteChildren);
		}
		
		s.saveOrUpdate(entity);
	}

	public <T> T load(Class<T> clazz, Long id){
		return session().load(clazz, id);
	}
	
	public <T> T get(Class<T> clazz, Long id){
		return session().get(clazz, id);
	}

	public void delete(Object entity){
		session().delete(entity);
	}
	
	public void delete(Class<?> clazz, Long id){
		session().delete(clazz.getName(), load(clazz, id));
	}
	
	public Query<?> query(String query){
		return session().createQuery(query);
	}
	
	public <T> Query<T> query(String query, Class<T> clazz){
		return session().createQuery(query, clazz);
	}
	
	public Query<?> nativeQuery(String nativeQuery){
		return session().createNativeQuery(nativeQuery);
	}
	
	public <T> List<T> findAll(Class<T> clazz){
		return session()
				.createQuery("FROM ".concat(clazz.getSimpleName()).concat(" ORDER BY ").concat(clazz.getSimpleName()).concat("id"), clazz)
				.list();
	}
	
	public <T> List<T> findAll(Class<T> clazz, String ... fieldsOrderBy){
		return session()
				.createQuery("FROM ".concat(clazz.getSimpleName()).concat(" ORDER BY ").concat(Arrays.stream(fieldsOrderBy).collect(Collectors.joining(","))), clazz)
				.list();
	}
	
	public <T> List<T> findList(Class<T> clazz, String query, Map<String, Object> paramsEquals){
		Query<T> q = session().createQuery(query, clazz);
		
		for(String key : paramsEquals.keySet()){
			q.setParameter(key, paramsEquals.get(key));
		}	
		
		return q.list();
	}
	
	public <T> List<T> findAll(Class<T> clazz, Map<String, Object> paramsEquals, String ... fieldsOrderBy){
		if(fieldsOrderBy == null){
			fieldsOrderBy = new String[]{clazz.getSimpleName().toLowerCase().concat("id")};
		}
		
		String where = createWhereByParams(paramsEquals);
		
		Query<T> query = session()
				.createQuery(
						"FROM "
						.concat(clazz.getSimpleName())
						.concat(where)
						.concat(" ORDER BY ")
						.concat(Arrays.stream(fieldsOrderBy).collect(Collectors.joining(","))), clazz);
		
		for(String key : paramsEquals.keySet()){
			query.setParameter(key, paramsEquals.get(key));
		}
		
		return query.list();
	}

	public <T> T findUniqueField(Class<T> classResult, Class<? extends AgiliteAbstractEntity> classEntity, String field, Map<String, Object> paramsEquals){
		String where = createWhereByParams(paramsEquals);
		
		Query<T> q = session().createQuery(StringUtils.concat(
				" SELECT ", field, " FROM ", classEntity.getSimpleName(), 
				where), classResult);
		
		for(String key : paramsEquals.keySet()){
			q.setParameter(key, paramsEquals.get(key));
		}
		
		return q.uniqueResult();
	}
	
	public <T> List<T> findListField(Class<T> classResult, Class<? extends AgiliteAbstractEntity> classEntity, String field, Map<String, Object> paramsEquals){
		String where = createWhereByParams(paramsEquals);
		
		Query<T> q = session().createQuery(StringUtils.concat(
				" SELECT ", field, " FROM ", classEntity.getSimpleName(), 
				where), classResult);
		
		for(String key : paramsEquals.keySet()){
			q.setParameter(key, paramsEquals.get(key));
		}
		
		return q.list();
	}
	
	public Long findId(Class<? extends AgiliteAbstractEntity> classEntity, Map<String, Object> paramsEquals){
		String where = createWhereByParams(paramsEquals);
		if(StringUtils.isNullOrEmpty(where))throw new RuntimeException("Não é permitido a busca de ID sem informar parâmetros");
		
		Query<Long> q = session().createQuery(StringUtils.concat(
				" SELECT ", classEntity.getSimpleName().toLowerCase(), "id",
				" FROM ", classEntity.getSimpleName(), 
				where), Long.class);
		
		for(String key : paramsEquals.keySet()){
			q.setParameter(key, paramsEquals.get(key));
		}
		
		return q.uniqueResult();
	}

	
	public Session session() {
		return em.unwrap(Session.class);
	}
	
	
	@SuppressWarnings("unchecked")
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

	private String createWhereByParams(Map<String, Object> paramsEquals) {
		if(paramsEquals == null || paramsEquals.size() == 0)return " ";
		
		boolean hasParamNull = paramsEquals.values().stream().filter(val -> val == null).findFirst().isPresent();
		if(hasParamNull){
			throw new RuntimeException("Parâmetro com valor nulo não é permitido no find");
		}
		
		String where = paramsEquals.keySet().stream()
				.map(val -> {
					boolean isString = EntitiesMetadata.INSTANCE.getPropertyByName(val).getType() == String.class;
					if(isString){
						return "LOWER(".concat(val).concat(") = LOWER(:").concat(val).concat(")");
					}else{
						return val.concat(" = :").concat(val);
					}
				})
				.collect(Collectors.joining(" AND "));
		return " WHERE " + where;
	}
}
