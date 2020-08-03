package info.agilite.spring.base.fk;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
public class FkRequest {
	String query;
	String fields;
	String entityName;
	
	
}
