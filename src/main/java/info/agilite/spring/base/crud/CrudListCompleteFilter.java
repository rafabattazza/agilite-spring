package info.agilite.spring.base.crud;

import java.util.Map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Data
public class CrudListCompleteFilter {
	boolean showArchivedOnly;
	Map<String, String> values;
}
