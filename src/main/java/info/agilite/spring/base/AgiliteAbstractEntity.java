package info.agilite.spring.base;

import java.io.Serializable;
import java.util.List;

import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.annotation.JsonIgnore;

import info.agilite.spring.hibernate.types.JsonBinaryType;

@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@MappedSuperclass
public abstract class AgiliteAbstractEntity implements Serializable{
	private static final long serialVersionUID = 1L;

	@JsonIgnore
	public abstract Long getIdValue();
	@JsonIgnore
	public List<Long> getFilesIds(){
		return null;
	}

}
