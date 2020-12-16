package info.agilite.spring.base.crud;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.management.IntrospectionException;
import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import info.agilite.spring.base.AgiliteAbstractEntity;
import info.agilite.spring.base.JacksonConfig;
import info.agilite.spring.base.RestMapping;
import info.agilite.spring.base.metadata.EntitiesMetadata;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestMapping("/app/crud")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class AgiliteCrudController {
	AgiliteCrudService service;
	JacksonConfig jackson;
	
	@PostMapping("/save/{entity}")
	@Transactional
	public Object salvar(@PathVariable("entity") String entityName, @RequestBody String entity) throws ClassNotFoundException, JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = jackson.createObjectMapper();
		AgiliteAbstractEntity parsedEntity = (AgiliteAbstractEntity)mapper.readValue(entity.getBytes(), EntitiesMetadata.INSTANCE.getEntityClass(entityName));
		
		service.saveEntity(parsedEntity);
		return parsedEntity;
	}
	
	@PostMapping("/list/{entity}")
	public CrudListResponse listar(@PathVariable("entity") String entityName, @RequestBody CrudListRequest request) {
		return service.list(entityName, request);
	}

	
	@PostMapping("/edit/{entity}/{id}")
	public Object editar(@PathVariable("entity") String entityName, @PathVariable("id") Long idEntity, @RequestBody(required = false) List<String> viewPropertiesToFetchJoin) throws ClassNotFoundException, IntrospectionException {
		if(idEntity == null) {
			return service.novo(entityName);
		}else {
			Object result = service.editar(entityName, idEntity, viewPropertiesToFetchJoin);
			if(result == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);
			}
			
			return result;
		}
		
	}
	
	@PostMapping("/copy/{entity}/{id}")
	public Object copiar(@PathVariable("entity") String entityName, @PathVariable("id") Long idEntity, @RequestBody(required = false) List<String> viewPropertiesToFetchJoin) throws ClassNotFoundException, IntrospectionException {
		Objects.requireNonNull(idEntity, "Entity id can't be null");
		
		Object result = service.copiar(entityName, idEntity, viewPropertiesToFetchJoin);
		if(result == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		return result;
	}
	
	
	@PostMapping("/delete/{entity}")
	@Transactional
	public void deletar(@PathVariable("entity") String entityName, @RequestBody List<Long> ids) {
		service.delete(entityName, ids);
	}
	
	@PostMapping("/archive/{entity}")
	@Transactional
	public void arquivar(@PathVariable("entity") String entityName, @RequestBody List<Long> ids) {
		service.arquivar(entityName, ids);
	}
	
	
	@PostMapping("/unarchive/{entity}")
	@Transactional
	public void desArquivar(@PathVariable("entity") String entityName, @RequestBody List<Long> ids) {
		service.desArquivar(entityName, ids);
	}
	
}
