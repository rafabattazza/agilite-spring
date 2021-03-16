package info.agilite.spring.base.json;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import info.agilite.spring.base.metadata.EntitiesMetadata;
import info.agilite.spring.base.metadata.OneToManyMetadata;
import info.agilite.utils.Utils;

public class RemoveIdsJsonFilter implements Predicate<String>{
	private Class<?> entityClass;
	private Set<String> nomesExcluir = new HashSet<>();
	
	public RemoveIdsJsonFilter(Class<?> entityClass) {
		super();
		this.entityClass = entityClass;
		nomesExcluir.add(this.entityClass.getSimpleName().toLowerCase()+"id");
		
		List<OneToManyMetadata> oneToManysByTable = EntitiesMetadata.INSTANCE.getOneToManysByTable(entityClass.getSimpleName());
		if(!Utils.isEmpty(oneToManysByTable)) {
			oneToManysByTable.forEach(one -> {
				nomesExcluir.add((one.getClasse()+"id").toLowerCase());
			});
		}
	}



	@Override
	public boolean test(String t) {
		return nomesExcluir.contains(t.toLowerCase());
	}
}
