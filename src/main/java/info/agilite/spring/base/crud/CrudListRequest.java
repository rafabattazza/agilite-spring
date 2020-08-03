package info.agilite.spring.base.crud;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import info.agilite.utils.StringUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE)
@Getter
@Setter
public class CrudListRequest {
	List<CrudField> fields;
	List<CrudFilter> filters;
	List<CrudOrderBy> orders;
	List<CrudJoin> joins;
	String query;

	Integer pageSize;
	Integer pageNumber;
	
	@JsonIgnore
	List<CrudFilterParameter> parameters;
	
	public void addToParameter(CrudFilterParameter parameter) {
		if(this.parameters == null)this.parameters = new ArrayList<>();
		
		this.parameters.add(parameter);
	}
	
	public String getFieldsToSQL(String alias) {
		return StringUtils.concat(alias, ".id, ", fields.stream()
				.map(field -> StringUtils.concat(alias, ".", field.getKey()))
				.collect(Collectors.joining(", ")));
	}

	public String getFieldsAsString() {
		return StringUtils.concat("id, ", fields.stream()
				.map(field -> field.getKey())
				.collect(Collectors.joining(", ")));
	}

}
