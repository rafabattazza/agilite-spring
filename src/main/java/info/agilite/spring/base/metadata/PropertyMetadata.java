package info.agilite.spring.base.metadata;

import java.lang.reflect.Type;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE)
@RequiredArgsConstructor(access=AccessLevel.PUBLIC)
@Getter
@Setter
public class PropertyMetadata {
	final String nome;
	final String table;
	final Type type;
	final String size;
	final boolean required;
	final boolean fk;
	
	EntityMetadata entityMetadata;
}
