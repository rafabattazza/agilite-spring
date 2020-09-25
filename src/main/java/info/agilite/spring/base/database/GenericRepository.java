package info.agilite.spring.base.database;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import info.agilite.utils.Utils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class GenericRepository {
	EntityManager em;
	
	/**
	 * Executa uma select de unico registro com os valores informados
	 * @param <T> Tipo do retorno
	 * @param clazz Classe do retorno
	 * @param values Valores informados em pares de nome - valor, exemplo: "cd20nome", "rafael", "cd20tipo", 1
	 * @return
	 */
	public <T> T getByEquals(Class<T> clazz, Object ... values) {
		Session s = em.unwrap(Session.class);
		CriteriaQuery<T> criteriaQuery = createCriteria(clazz, s, values);
		
		return s.createQuery(criteriaQuery).uniqueResult();
	}
	
	public <T> List<T> findByEquals(Class<T> clazz, Object ... values) {
		Session s = em.unwrap(Session.class);
		CriteriaQuery<T> criteriaQuery = createCriteria(clazz, s, values);
		
		return s.createQuery(criteriaQuery).list();
	}

	private <T> CriteriaQuery<T> createCriteria(Class<T> clazz, Session s, Object... values) {
		CriteriaBuilder criteriaBuilder = s.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
		Root<T> root = criteriaQuery.from(clazz);
		
		Map<String, Object> map = Utils.mapWithStringKeys(values);
		List<Predicate> restrictions = map.entrySet().stream().map(entry -> {
			if(entry.getValue() == null) {
				return criteriaBuilder.isNull(root.get(entry.getKey()));
			}else if(entry.getValue() instanceof String) {
				return criteriaBuilder.equal(criteriaBuilder.lower(root.get(entry.getKey())), ((String)entry.getValue()).toLowerCase());
			}else {
				return criteriaBuilder.equal(root.get(entry.getKey()), entry.getValue());
			}
		}).collect(Collectors.toList());
		Predicate and = criteriaBuilder.and(restrictions.toArray(new Predicate[0]));
		criteriaQuery.where(and);
		return criteriaQuery;
	}
	
	
	public <T> T getFetch(Class<T> clazz, Long id, Join ... joins) {
		Session s = em.unwrap(Session.class);
		CriteriaBuilder criteriaBuilder = s.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);

		Root<T> root = criteriaQuery.from(clazz);
		Arrays.asList(joins).forEach(join -> root.fetch(join.getName(), join.isLeft() ? JoinType.LEFT : JoinType.INNER));
		
		criteriaQuery.where(criteriaBuilder.equal(root.get(clazz.getSimpleName().toLowerCase()+"id"), id));
		
		return s.createQuery(criteriaQuery).uniqueResult();
	}

}
