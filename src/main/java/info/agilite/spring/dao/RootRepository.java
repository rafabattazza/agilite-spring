package info.agilite.spring.dao;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import info.agilite.spring.base.AgiliteAbstractEntity;
import info.agilite.spring.base.database.HibernateWrapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class RootRepository {
	public final HibernateWrapper hibernate;

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
		if(!TransactionSynchronizationManager.isActualTransactionActive()) {
			throw new RuntimeException("Não é permitido salvar registros sem uma transação aberta");
		}
		hibernate.persist(entity);
	}
	
	public void persistAndFlush(AgiliteAbstractEntity entity) {
		if(!TransactionSynchronizationManager.isActualTransactionActive()) {
			throw new RuntimeException("Não é permitido salvar registros sem uma transação aberta");
		}

		hibernate.persist(entity);
		hibernate.flush();
	}

	public void persistForceRemoveChildren(AgiliteAbstractEntity entity) {
		if(!TransactionSynchronizationManager.isActualTransactionActive()) {
			throw new RuntimeException("Não é permitido salvar registros sem uma transação aberta");
		}

		hibernate.persistForceRemoveChildren(entity);
	}

	public void persistForceRemoveChildren(AgiliteAbstractEntity entity, Predicate<String> deleteChildren) {
		if(!TransactionSynchronizationManager.isActualTransactionActive()) {
			throw new RuntimeException("Não é permitido salvar registros sem uma transação aberta");
		}

		hibernate.persistForceRemoveChildren(entity, deleteChildren);
	}
	
	public void persistForceRemoveChildrenAndFlush(AgiliteAbstractEntity entity) {
		if(!TransactionSynchronizationManager.isActualTransactionActive()) {
			throw new RuntimeException("Não é permitido salvar registros sem uma transação aberta");
		}

		hibernate.persistForceRemoveChildren(entity);
		hibernate.flush();
	}

	public void persistForceRemoveChildrenAndFlush(AgiliteAbstractEntity entity, Predicate<String> deleteChildren) {
		if(!TransactionSynchronizationManager.isActualTransactionActive()) {
			throw new RuntimeException("Não é permitido salvar registros sem uma transação aberta");
		}

		hibernate.persistForceRemoveChildren(entity, deleteChildren);
		hibernate.flush();
	}

	public void delete(AgiliteAbstractEntity entity) {
		if(!TransactionSynchronizationManager.isActualTransactionActive()) {
			throw new RuntimeException("Não é permitido deletar registros sem uma transação aberta");
		}

		hibernate.delete(entity);
	}

	public void delete(Class<? extends AgiliteAbstractEntity> clazz, Long id) {
		if(!TransactionSynchronizationManager.isActualTransactionActive()) {
			throw new RuntimeException("Não é permitido deletar registros sem uma transação aberta");
		}

		hibernate.delete(clazz, id);
	}

	public void delete(Class<? extends AgiliteAbstractEntity> clazz, List<Long> ids) {
		if(!TransactionSynchronizationManager.isActualTransactionActive()) {
			throw new RuntimeException("Não é permitido deletar registros sem uma transação aberta");
		}

		hibernate.delete(clazz, ids);
	}

	public <T> List<T> findAll(Class<T> clazz) {
		return hibernate.findAll(clazz);
	}

	public <T> List<T> findAll(Class<T> clazz, List<Long> ids) {
		return hibernate.findAll(clazz, ids);
	}

	public <T> List<T> findAll(Class<T> clazz, String orderBy) {
		return hibernate.findAll(clazz, orderBy);
	}

	public <T> T findUniqueByQuery(Class<T> clazz, String query, Map<Object, Object> params) {
		return hibernate.findUniqueByQuery(clazz, query, params);
	}

	public <T> List<T> findListByQuery(Class<T> clazz, String query, Map<Object, Object> params) {
		return hibernate.findListByQuery(clazz, query, params);
	}

	public <T> List<T> findListByEquals(Class<T> clazz, Map<Object, Object> params) {
		return hibernate.findListByEquals(clazz, params);
	}

	public <T> List<T> findListByEqualsOrderBy(Class<T> clazz, String orderBy, Map<Object, Object> params) {
		return hibernate.findListByEqualsOrderBy(clazz, orderBy, params);
	}

	public <T> T findUniqueByEquals(Class<T> clazz, Map<Object, Object> params) {
		return hibernate.findUniqueByEquals(clazz, params);
	}
	
	public <T> T findUniqueFieldByEquals(Class<T> returnType, Class<? extends AgiliteAbstractEntity> entityClass, String field, Map<Object, Object> paramsEquals) {
		return hibernate.findUniqueFieldByEquals(returnType, entityClass, field, paramsEquals);
	}

	public Query<?> nativeQuery(String nativeQuery) {
		return hibernate.nativeQuery(nativeQuery);
	}

	public Query<?> nativeQuery(String nativeQuery, Map<Object, Object> params) {
		return hibernate.nativeQuery(nativeQuery, params);
	}

	public <T> Query<T> nativeQuery(Class<T> clazz, String nativeQuery) {
		return hibernate.nativeQuery(clazz, nativeQuery);
	}

	public <T> Query<T> nativeQuery(Class<T> clazz, String nativeQuery, Map<Object, Object> params) {
		return hibernate.nativeQuery(clazz, nativeQuery, params);
	}

	public Query<?> query(String query) {
		return hibernate.query(query);
	}

	public Query<?> query(String query, Map<Object, Object> params) {
		return hibernate.query(query, params);
	}

	public <T> Query<T> query(String query, Class<T> clazz) {
		return hibernate.query(query, clazz);
	}

	public <T> Query<T> query(String query, Class<T> clazz, Map<Object, Object> params) {
		return hibernate.query(query, clazz, params);
	}

	public <T> Query<T> createEqualsQuery(Class<T> clazz, Map<Object, Object> paramsEquals) {
		return hibernate.createEqualsQuery(clazz, paramsEquals);
	}

	public <T> Query<T> createEqualsQueryOrderBy(Class<T> clazz, String orderBy, Map<Object, Object> paramsEquals) {
		return hibernate.createEqualsQueryOrderBy(clazz, orderBy, paramsEquals);
	}

	public Session session() {
		return hibernate.session();
	}

	public void setQueryParams(Query<?> q, Map<Object, Object> parametros) {
		hibernate.setQueryParams(q, parametros);
	}
	
	public void changeSchema(String schema) {
		nativeQuery("SET search_path="+schema).executeUpdate();
	}
	
	
}
