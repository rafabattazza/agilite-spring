package info.agilite.spring.base.autocomplete;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import info.agilite.spring.base.RestMapping;
import info.agilite.spring.base.metadata.EntitiesMetadata;
import info.agilite.spring.base.metadata.PropertyMetadata;
import info.agilite.utils.StringUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@RestMapping("/app/autocomplete")
public class AgiliteAutocompleteController {
	EntityManager em;
	
	@SuppressWarnings("unchecked")
	@PostMapping
	public List<Object[]> autoComplete(@RequestBody AutoCompleteRequest request){
		String whereQuery = "";
		if(!StringUtils.isNullOrEmpty(request.getQuery())) {
			whereQuery  = StringUtils.concat(" AND LOWER(", createColumnToQuery(request), ") LIKE :query");
		}
		
		Query<Object[]> q = em.unwrap(Session.class)
			.createQuery(StringUtils.concat(
					" SELECT ", request.getTable(), "id, ", request.getColumns().stream().collect(Collectors.joining(", ")),
					" FROM ", StringUtils.capitalize(request.getTable()),
					" WHERE 1 = 1 ", whereQuery,
					(StringUtils.isNullOrEmpty(request.getDefaultFilter()) ? "" : " AND " + request.getDefaultFilter()),
					" ORDER BY ", request.getColumns().stream().collect(Collectors.joining(", "))
					));
		
		if(!StringUtils.isNullOrEmpty(request.getQuery())) {
			q.setParameter("query", "%" + request.getQuery().toLowerCase() + "%");
		}
		
		return q.getResultList();
	}
	
	private String createColumnToQuery(AutoCompleteRequest request) {
		List<String> result = new ArrayList<>();
		
		request.getColumns().stream().forEach(column -> {
			PropertyMetadata property = EntitiesMetadata.INSTANCE.getPropertyByName(column);
			if(property.getType().getTypeName().equals(String.class.getName())) {
				result.add(property.getNome());
			}else {
				result.add("CAST(" + property.getNome() + " AS string)");
			}
		});
		
		return result.stream().collect(Collectors.joining(" || ")); 
	}
	
}
