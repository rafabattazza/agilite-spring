package info.agilite.spring.base.crud;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CrudListPagination {
	String sortBy;
	boolean descending;
	Integer page, rowsPerPage, rowsNumber;
	
	
	
}
