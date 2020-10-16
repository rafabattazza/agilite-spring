package info.agilite.spring.base.crud;

import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Data
public class CrudListCompleteFilter {
	boolean showArchivedOnly;
	List<CrudListCompleteFilterValue> values;
}
