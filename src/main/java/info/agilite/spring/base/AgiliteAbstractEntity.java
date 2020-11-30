package info.agilite.spring.base;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.TypeDef;

import info.agilite.spring.hibernate.types.JsonBinaryType;

@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@MappedSuperclass
public abstract class AgiliteAbstractEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	public abstract Long getIdValue();

}
