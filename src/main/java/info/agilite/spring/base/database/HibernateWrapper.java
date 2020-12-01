package info.agilite.spring.base.database;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
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
		em.unwrap(Session.class).saveOrUpdate(entity);
	}
	
	public void persistForceRemoveChildren(Object entity){
		this.persistForceRemoveChildren(entity, (child) -> true);
	}
	
	public void persistForceRemoveChildren(Object entity, Predicate<String> deleteChildren){
		Session s = em.unwrap(Session.class);
		
		if(((AgiliteAbstractEntity)entity).getIdValue() != null){
			cascadeDeleteChildren(entity, s, deleteChildren);
		}
		
		s.saveOrUpdate(entity);
	}

	public <T> T load(Class<T> clazz, Long id){
		return em.unwrap(Session.class).load(clazz, id);
	}
	
	public <T> T get(Class<T> clazz, Long id){
		return em.unwrap(Session.class).get(clazz, id);
	}
	
	public void delete(Object entity){
		em.unwrap(Session.class).delete(entity);
	}
	
	public void delete(Class<?> clazz, Long id){
		em.unwrap(Session.class).delete(clazz.getSimpleName(), id);
	}
	
	public Query query(String query){
		return em.unwrap(Session.class).createQuery(query);
	}
	
	public <T> Query<T> query(String query, Class<T> clazz){
		return em.unwrap(Session.class).createQuery(query, clazz);
	}
	
	public Query nativeQuery(String nativeQuery){
		return em.unwrap(Session.class).createNativeQuery(nativeQuery);
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
}
