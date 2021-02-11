package info.agilite.spring.dao;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.hibernate.Session;
import org.hibernate.query.Query;

import info.agilite.spring.base.AgiliteAbstractEntity;
import info.agilite.spring.base.database.HibernateWrapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class RootRepository {
	protected final HibernateWrapper hibernate;

	public <T extends AgiliteAbstractEntity> T load(Class<T> clazz, Long id) {
		return hibernate.load(clazz, id);
	}

	public <T extends AgiliteAbstractEntity> T get(Class<T> clazz, Long id) {
		return hibernate.get(clazz, id);
	}

	public void flush() {
		hibernate.flush();
	}
	
	public void persist(AgiliteAbstractEntity entity) {
		hibernate.persist(entity);
	}
	
	public void persistAndFlush(AgiliteAbstractEntity entity) {
		hibernate.persist(entity);
		hibernate.flush();
	}

	public void persistForceRemoveChildren(AgiliteAbstractEntity entity) {
		hibernate.persistForceRemoveChildren(entity);
	}

	public void persistForceRemoveChildren(AgiliteAbstractEntity entity, Predicate<String> deleteChildren) {
		hibernate.persistForceRemoveChildren(entity, deleteChildren);
	}
	
	public void persistForceRemoveChildrenAndFlush(AgiliteAbstractEntity entity) {
		hibernate.persistForceRemoveChildren(entity);
		hibernate.flush();
	}

	public void persistForceRemoveChildrenAndFlush(AgiliteAbstractEntity entity, Predicate<String> deleteChildren) {
		hibernate.persistForceRemoveChildren(entity, deleteChildren);
		hibernate.flush();
	}

	public void delete(AgiliteAbstractEntity entity) {
		hibernate.delete(entity);
	}

	public void delete(Class<? extends AgiliteAbstractEntity> clazz, Long id) {
		hibernate.delete(clazz, id);
	}

	public void delete(Class<? extends AgiliteAbstractEntity> clazz, List<Long> ids) {
		hibernate.delete(clazz, ids);
	}

	public <T> List<T> findAll(Class<T> clazz) {
		return hibernate.findAll(clazz);
	}

	public <T> List<T> findAll(Class<T> clazz, List<Long> ids) {
		return hibernate.findAll(clazz, ids);
	}

	protected <T> List<T> findAll(Class<T> clazz, String orderBy) {
		return hibernate.findAll(clazz, orderBy);
	}

	protected <T> T findUniqueByQuery(Class<T> clazz, String query, Map<Object, Object> params) {
		return hibernate.findUniqueByQuery(clazz, query, params);
	}

	protected <T> List<T> findListByQuery(Class<T> clazz, String query, Map<Object, Object> params) {
		return hibernate.findListByQuery(clazz, query, params);
	}

	protected <T> List<T> findListByEquals(Class<T> clazz, Map<Object, Object> params) {
		return hibernate.findListByEquals(clazz, params);
	}

	public <T> List<T> findListByEqualsOrderBy(Class<T> clazz, String orderBy, Map<Object, Object> params) {
		return hibernate.findListByEqualsOrderBy(clazz, orderBy, params);
	}

	protected <T> T findUniqueFieldByEquals(Class<T> returnType, Class<? extends AgiliteAbstractEntity> entityClass, String field, Map<Object, Object> paramsEquals) {
		return hibernate.findUniqueFieldByEquals(returnType, entityClass, field, paramsEquals);
	}

	protected Query<?> nativeQuery(String nativeQuery) {
		return hibernate.nativeQuery(nativeQuery);
	}

	protected Query<?> nativeQuery(String nativeQuery, Map<Object, Object> params) {
		return hibernate.nativeQuery(nativeQuery, params);
	}

	protected Query<?> nativeQuery(Class<?> clazz, String nativeQuery) {
		return hibernate.nativeQuery(clazz, nativeQuery);
	}

	protected Query<?> nativeQuery(Class<?> clazz, String nativeQuery, Map<Object, Object> params) {
		return hibernate.nativeQuery(clazz, nativeQuery, params);
	}

	protected Query<?> query(String query) {
		return hibernate.query(query);
	}

	protected Query<?> query(String query, Map<Object, Object> params) {
		return hibernate.query(query, params);
	}

	protected <T> Query<T> query(String query, Class<T> clazz) {
		return hibernate.query(query, clazz);
	}

	protected <T> Query<T> query(String query, Class<T> clazz, Map<Object, Object> params) {
		return hibernate.query(query, clazz, params);
	}

	protected <T> Query<T> createEqualsQuery(Class<T> clazz, Map<Object, Object> paramsEquals) {
		return hibernate.createEqualsQuery(clazz, paramsEquals);
	}

	protected <T> Query<T> createEqualsQueryOrderBy(Class<T> clazz, String orderBy, Map<Object, Object> paramsEquals) {
		return hibernate.createEqualsQueryOrderBy(clazz, orderBy, paramsEquals);
	}

	protected Session session() {
		return hibernate.session();
	}

	protected void setQueryParams(Query<?> q, Map<Object, Object> parametros) {
		hibernate.setQueryParams(q, parametros);
	}
}
