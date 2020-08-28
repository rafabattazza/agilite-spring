package info.agilite.spring.base.database;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Join {
	String name;
	boolean left;
	
	public Join(String name) {
		this(name, false);
	}
}
