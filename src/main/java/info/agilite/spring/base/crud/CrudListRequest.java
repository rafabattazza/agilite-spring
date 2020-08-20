package info.agilite.spring.base.crud;

import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CrudListRequest {
	Integer page, rowsPerPage;
	String sortBy;
	boolean desc;
	List<String> columns;
	
	List<String> columnsSimpleFilter;
	String simpleFilterValue;
	
	String defaultFilter;
	
	CrudListCompleteFilter completeFilters;
}
