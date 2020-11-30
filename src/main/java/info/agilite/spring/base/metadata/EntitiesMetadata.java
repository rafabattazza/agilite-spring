package info.agilite.spring.base.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class EntitiesMetadata {
	public static EntitiesMetadata INSTANCE;

	protected abstract  Map<String, PropertyMetadata> getAttributesMetadata();
	protected abstract  Map<String, List<OneToManyMetadata>> getOneToManys();
	
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
	public List<OneToManyMetadata> getOneToManysByTable(String tableName){
		tableName = tableName.toLowerCase();
		return getOneToManys().get(tableName);
	}
}

