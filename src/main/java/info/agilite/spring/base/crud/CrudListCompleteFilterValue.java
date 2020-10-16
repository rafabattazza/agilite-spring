package info.agilite.spring.base.crud;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Data
public class CrudListCompleteFilterValue {
	String name;
	String value;
}
