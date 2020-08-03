package info.agilite.spring.base.crud;

import java.math.BigDecimal;

import info.agilite.utils.DateUtils;
import info.agilite.utils.StringUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE)
@Getter
@Setter
public class CrudFilter {
	public static final String PARAM_PREFIX = "filter_param_"; 
	
	CrudFilterOperation oper;
	String key;
	CrudFilterType type;
	String[] values;
	
	public String getSqlField(String alias) {
		if(oper == CrudFilterOperation.isNotNull || oper == CrudFilterOperation.isNull) {
			return StringUtils.concat(alias, ".", key);
		}else if(type == null || type == CrudFilterType.STRING) {
			return StringUtils.concat(" LOWER(", alias, ".", key, ")");
		}else {
			return StringUtils.concat(alias, ".", key);
		}
	}
	
	public String getSqlParams(int index) {
		if(oper == CrudFilterOperation.isNotNull || oper == CrudFilterOperation.isNull) {
			return " ";
		}else if(oper == CrudFilterOperation.between) {
			return StringUtils.concat(" :", PARAM_PREFIX, index, " AND :", PARAM_PREFIX,  index + 1);
		}else {
			return StringUtils.concat(" :", PARAM_PREFIX, index);
		}
	}
	
	
	public Object[] convertValues () {
		if(oper == CrudFilterOperation.isNotNull || oper == CrudFilterOperation.isNull) {
			return new Object[0];
		}else if(oper == CrudFilterOperation.between) {
			int indexSecondValue = values.length >= 2 ? 1 : 0;
			return new Object[] {string2Object(values[0]), string2Object(values[indexSecondValue])};
		}else {
			return new Object[] {string2Object(values[0])};
		}
	}
	
	private Object string2Object(String value) {
		if(value == null) return null;
		if(type == null || type == CrudFilterType.STRING) {
			if(oper == CrudFilterOperation.like) {
				return StringUtils.concat("%", value.toLowerCase(), "%");	
			}else {
				return value.toLowerCase();
			}
		}else {
			switch (type) {
			case DATE:
				return DateUtils.parseDate(value, "yyyy-MM-dd");
			case INTEGER:
				return new Integer(value);
			case DECIMAL:
				return new BigDecimal(value);
			default:
				throw new RuntimeException("Tipo " + type + " não possui conversor definido");
			}
		}
		
	}
	
}
