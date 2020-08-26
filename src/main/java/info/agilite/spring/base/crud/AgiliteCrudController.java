package info.agilite.spring.base.crud;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.management.IntrospectionException;
import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

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
	public Object save(@PathVariable("entity") String entityName, @RequestBody String entity) throws ClassNotFoundException, JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = jsonBuilder.build();
		Object parsedEntity = mapper.readValue(entity.getBytes(), service.getEntityClass(entityName));
		
		return service.saveEntity(parsedEntity);
	}
	
	@PostMapping("/list/{entity}")
	public CrudListResponse list(@PathVariable("entity") String entityName, @RequestBody CrudListRequest request) {
		return service.list(entityName, request);
	}

	
	@PostMapping("/edit/{entity}/{id}")
	public Object edit(@PathVariable("entity") String entityName, @PathVariable("id") Long idEntity) throws ClassNotFoundException, IntrospectionException {
		Objects.requireNonNull(idEntity, "Entity id can't be null");
		
		Object result = service.findEntityById(entityName, idEntity);
		if(result == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		return result;
	}
	
	@PostMapping("/archive/{entity}")
	@Transactional
	public void archive(@PathVariable("entity") String entityName, @RequestBody List<Long> ids) {
		service.archive(entityName, ids);
	}
	
	@PostMapping("/delete/{entity}")
	@Transactional
	public void delete(@PathVariable("entity") String entityName, @RequestBody Long id) {
		service.delete(entityName, id);
	}
	
	@PostMapping("/unarchive/{entity}")
	@Transactional
	public void unarchive(@PathVariable("entity") String entityName, @RequestBody List<Long> ids) {
		service.unarchive(entityName, ids);
	}
	
}
