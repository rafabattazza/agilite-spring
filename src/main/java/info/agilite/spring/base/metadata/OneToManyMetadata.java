package info.agilite.spring.base.metadata;

import java.lang.reflect.Method;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE)
@RequiredArgsConstructor(access=AccessLevel.PUBLIC)
@Getter
@Setter
public class OneToManyMetadata {
	final String nome;
	final String classe;
	final String joinColumn;
	
	Method methodGet;
}
