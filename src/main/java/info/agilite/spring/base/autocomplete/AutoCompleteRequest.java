package info.agilite.spring.base.autocomplete;

import java.util.List;

import info.agilite.utils.StringUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
public class AutoCompleteRequest {
	String table;
	List<String> columns;
	String query;
	String defaultFilter;
	
	
	public String getColumnId() {
		return StringUtils.concat(getTable().toLowerCase(), "id");
	}
}
