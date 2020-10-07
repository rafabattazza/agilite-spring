package info.agilite.spring.base.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class EntitiesMetadata {
	public static EntitiesMetadata INSTANCE;

	protected abstract  Map<String, PropertyMetadata> getAttributesMetadata();
	
	public PropertyMetadata getPropertyByName(String name) {
		return getAttributesMetadata().get(name.toLowerCase());
	}
	public List<PropertyMetadata> getPropertiesByTable(String tableName){
		tableName = tableName.toLowerCase();
		
		Map<String, PropertyMetadata> metadata = getAttributesMetadata();
		List<PropertyMetadata> properties = new ArrayList<>();
		for(String columnName : metadata.keySet()){
			if(columnName.startsWith(tableName)){
				PropertyMetadata property = metadata.get(columnName);
				if(property.getTable().equals(tableName)){
					properties.add(property);
				}
			}
		}
		
		return properties;
	}
}

