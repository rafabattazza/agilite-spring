package info.agilite.spring.base.crud;

import java.io.IOException;
import java.util.Objects;

import javax.management.IntrospectionException;
import javax.transaction.Transactional;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import info.agilite.spring.base.RestMapping;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestMapping("/app/crud")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class AgiliteCrudController {
	AgiliteCrudService service;
	Jackson2ObjectMapperBuilder jsonBuilder;
	
	@PostMapping("/save/{entity}")
	@Transactional
	public void save(@PathVariable("entity") String entityName, @RequestBody String entity) throws ClassNotFoundException, JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = jsonBuilder.build();
		Object parsedEntity = mapper.readValue(entity.getBytes(), service.getEntityClass(entityName));
		
		service.saveEntity(parsedEntity);
	}
	
	@PostMapping("/list/{entity}")
	public CrudListResponse list(@PathVariable("entity") String entityName, @RequestBody CrudListRequest request) {
		return service.list(entityName, request);
	}

	
	@PostMapping("/edit/{entity}/{id}")
	public Object edit(@PathVariable("entity") String entityName, @PathVariable("id") Long idEntity) throws ClassNotFoundException, IntrospectionException {
		Objects.requireNonNull(idEntity, "Entity id can't be null");
		
		return service.findEntityById(entityName, idEntity);
	}
	

//	
//	@DeleteMapping("/{entity}/{id}")
//	@Transactional
//	public void delete(@PathVariable("entity") String entityName, @PathVariable("id") Long id) {
//		service.delete(entityName, id);
//	}
//	
//	private CrudListRequest configRequestDefault(CrudListRequest request) {
//		if(Utils.isEmpty(request.getOrders())) {
//			request.setOrders(Arrays.asList(new CrudOrderBy(request.getFields().get(0).getKey(), false)));
//		}
//		
//		return request;
//	}
}
