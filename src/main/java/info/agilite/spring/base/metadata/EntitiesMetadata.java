package info.agilite.spring.base.metadata;

import java.util.Map;

public abstract class EntitiesMetadata {
	public static EntitiesMetadata INSTANCE;

	protected abstract  Map<String, PropertyMetadata> getAttributesMetadata();
	
	public PropertyMetadata getPropertyByName(String name) {
		return getAttributesMetadata().get(name.toLowerCase());
	}
}

