package info.agilite.spring.base.crud;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class CrudOrderBy {
	String key;
	boolean desc;
	
	public String getSqlOrder() {
		return key + (desc ? " DESC " : " ASC ");
	}
}
