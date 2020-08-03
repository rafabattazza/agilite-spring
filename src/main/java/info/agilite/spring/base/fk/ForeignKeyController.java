package info.agilite.spring.base.fk;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import info.agilite.spring.base.RestMapping;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestMapping("/api/fk")
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@AllArgsConstructor(access=AccessLevel.PACKAGE)
public class ForeignKeyController {
	FkService service;
	
	@PostMapping("{entity}")
	public List<FkResult> findValues(@PathVariable("entity") String entity,@RequestBody FkRequest request) {
		request.setEntityName(entity);
		
		return service.findFkRows(request);
	}
	
	@PostMapping("{entity}/{id}")
	public FkResult findById(@PathVariable("entity") String entity, @PathVariable("id") Long id, @RequestBody FkRequest request) {
		request.setEntityName(entity);
		
		return service.findFkById(id, request);
	}
}
