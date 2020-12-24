package info.agilite.spring.base.crud;

import java.lang.reflect.Type;
import java.util.Map;

import info.agilite.utils.StringUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Setter
public class CrudExportColumn {
	String columnSql, label, tableAlias;
	Type type;
	Map<String, String> legendas;
	
	public String getToSQL() {
		return StringUtils.concat(tableAlias, ".", columnSql);
	}
}
