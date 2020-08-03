package info.agilite.spring.base.fk;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Service;

import com.google.common.base.Splitter;

import info.agilite.utils.StringUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@RequiredArgsConstructor(access=AccessLevel.PACKAGE)
public class FkService {
	private static final int PAGE_SIZE = 50;
	EntityManager em;
	
	public List<FkResult> findFkRows(FkRequest request){
		TypedQuery<FkResult> q = em.createQuery(" SELECT new info.agilite.spring.base.fk.FkResult(" +
				 	   " id, " + getFields(request) +
				 	   " ) " +
					   " FROM " + request.getEntityName() +
					   createWhere(request) +
					   " ORDER BY " + getFields(request), FkResult.class)
		  .setMaxResults(PAGE_SIZE);
		
		if(!StringUtils.isNullOrEmpty(request.getQuery())) {
			q.setParameter("querySelect", "%" + request.getQuery().toLowerCase() + "%");
		}
		
		return q.getResultList();
	}
	
	public FkResult findFkById(Long id, FkRequest request){
		return em.createQuery(
					   " SELECT new info.agilite.spring.base.fk.FkResult(" +
				 	   " id, " + getFields(request) +
				 	   " ) " +
					   " FROM " + request.getEntityName() +
					   " WHERE id = :id", FkResult.class)
				.setParameter("id", id)
				.getSingleResult();
	}
	
	private String getFields(FkRequest request) {
		return Splitter.on(",")
			    .trimResults()
			    .splitToList(request.getFields())
			    .stream()
			    .collect(Collectors.joining("|| '$|$' ||"));
			    
		
	}
	
	private String createWhere(FkRequest request) {
		if(StringUtils.isNullOrEmpty(request.getQuery()))return "";
		return " WHERE LOWER(" + getFields(request) + ") LIKE LOWER(:querySelect) ";
	}
}
