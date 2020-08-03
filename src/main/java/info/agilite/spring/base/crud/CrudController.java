package info.agilite.spring.base.crud;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import info.agilite.spring.base.RestMapping;
import info.agilite.utils.Utils;

@RestMapping("/api/crud")
public class CrudController {
	protected final CrudService service;
	private Jackson2ObjectMapperBuilder jsonBuilder;
	
	@Autowired
	public CrudController(CrudService service, Jackson2ObjectMapperBuilder jsonBuilder) {
		this.service = service;
		this.jsonBuilder = jsonBuilder;
	}

	@PostMapping("/list/{entity}")
	public CrudResult list(@PathVariable("entity") String entityName, @RequestBody CrudListRequest request) {
		configRequestDefault(request);
		return service.list(entityName, request);
	}

	@PostMapping("/save/{entity}")
	@Transactional
	public void save(@PathVariable("entity") String entityName, @RequestBody String entity) throws ClassNotFoundException, JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = jsonBuilder.build();
		Object parsedEntity = mapper.readValue(entity.getBytes(), service.getEntityClass(entityName));
		
		service.saveEntity(parsedEntity);
	}

	@PostMapping("/edit/{entity}/{id}")
	public Map<String, Object> edit(@PathVariable("entity") String entityName, @PathVariable("id") Long idEntity) throws ClassNotFoundException, IntrospectionException {
		Objects.requireNonNull(idEntity, "Entity id can't be null");
		
		Map<String, Object> record = service.findEntityById(entityName, idEntity);
		
		return record;		
	}
	
	@DeleteMapping("/{entity}/{id}")
	@Transactional
	public void delete(@PathVariable("entity") String entityName, @PathVariable("id") Long id) {
		service.delete(entityName, id);
	}
	
	private CrudListRequest configRequestDefault(CrudListRequest request) {
		if(Utils.isEmpty(request.getOrders())) {
			request.setOrders(Arrays.asList(new CrudOrderBy(request.getFields().get(0).getKey(), false)));
		}
		
		return request;
	}
}
