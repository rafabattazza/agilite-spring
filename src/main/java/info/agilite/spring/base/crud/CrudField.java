package info.agilite.spring.base.crud;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE)
@Getter
@Setter
@Accessors(fluent=false, chain=true)
@NoArgsConstructor
public class CrudField {
	String key;
	CrudFilterType type;
	boolean filterable = true;
}
