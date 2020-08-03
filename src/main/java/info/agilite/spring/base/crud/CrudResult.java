package info.agilite.spring.base.crud;

import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal=true, level=AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
public class CrudResult {
	Long count;
	List<Map<String, Object>> items;
	String order;
}
