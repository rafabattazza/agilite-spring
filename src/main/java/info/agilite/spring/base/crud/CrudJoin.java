package info.agilite.spring.base.crud;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE)
@Getter
@Setter
@Accessors(fluent=false, chain=true)
@RequiredArgsConstructor
public class CrudJoin {
	@NonNull
	String property;
	boolean left;
	String alias;
	
}
