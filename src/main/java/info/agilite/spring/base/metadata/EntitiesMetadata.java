package info.agilite.spring.base.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import info.agilite.spring.base.AgiliteAbstractEntity;
import info.agilite.utils.StringUtils;

public abstract class EntitiesMetadata {
	public static EntitiesMetadata INSTANCE;

	protected abstract  Map<String, PropertyMetadata> getAttributesMetadata();
	protected abstract  Map<String, List<OneToManyMetadata>> getOneToManys();
	protected abstract  Map<String, String> getTables();
	protected abstract  Map<String, String> getUks();
	
	public abstract String getTableToFiles();
	public abstract Class<? extends AgiliteAbstractEntity> getEntityClass(String name);
	
	public String getTableLabelByName(String name) {
		return getTables().get(name.toLowerCase());
	}
	
	public PropertyMetadata getPropertyByName(String name) {
		return getAttributesMetadata().get(name.contains(".") ? StringUtils.substringAfterLast(name.toLowerCase(), ".")  : name.toLowerCase());
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
	
	public String getUkFields(String ukName){
		return getUks().get(ukName.toLowerCase());
	}
}

