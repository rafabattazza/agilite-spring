package info.agilite.spring.base.crud;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import info.agilite.spring.base.AgiliteAbstractEntity;
import info.agilite.spring.base.RestMapping;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@RestMapping("/app/crud")
public class AgiliteCrudController {
	EntityManager em;
	
	@PostMapping("/save")
	@Transactional
	public void save(@RequestBody AgiliteAbstractEntity entity){
		em.unwrap(Session.class).persist(entity);
	}
}
