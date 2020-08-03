package info.agilite.spring.base.metadata;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE)
@RequiredArgsConstructor(access=AccessLevel.PUBLIC)
@Getter
@Setter
public class EntityMetadata {
	final String simpleClassName;
	final String tableName;
	
	List<PropertyMetadata> properties = new ArrayList<>();
	
	public void addPropertyMetadata(PropertyMetadata property) {
		property.setEntityMetadata(this);
		properties.add(property);
	}
}
