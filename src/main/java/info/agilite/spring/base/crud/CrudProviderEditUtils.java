package info.agilite.spring.base.crud;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.query.Query;

import info.agilite.spring.base.AgiliteAbstractEntity;
import info.agilite.spring.base.UserSession;
import info.agilite.spring.base.database.HibernateWrapper;
import info.agilite.spring.base.metadata.EntitiesMetadata;
import info.agilite.spring.base.metadata.OneToManyMetadata;
import info.agilite.spring.base.metadata.PropertyMetadata;
import info.agilite.utils.StringUtils;
import info.agilite.utils.Utils;

class CrudProviderEditUtils {
	private UserSession session;
	private Class<?> entityClass;
	private HibernateWrapper hibernate;
	
	public CrudProviderEditUtils(Class<?> entityClass, UserSession session, HibernateWrapper hibernate) {
		super();
		this.entityClass = entityClass;
		this.session = session;
		this.hibernate = hibernate;
	}

	public AgiliteAbstractEntity editar(Long id, List<String> viewPropertiesToFetchJoin) {
		Query query = createQueryToEdit(viewPropertiesToFetchJoin);
		query.setParameter("ids", Arrays.asList(id));
		
		AgiliteAbstractEntity result = (AgiliteAbstractEntity)query.uniqueResult();
		return result;
	}
	
	private Query createQueryToEdit(List<String> viewPropertiesToFetchJoin) {
		String entityName = entityClass.getSimpleName();
		String alias = entityName.toLowerCase();
		String viewJoins = Utils.isEmpty(viewPropertiesToFetchJoin) ? " " : viewPropertiesToFetchJoin.stream().map(prop -> " LEFT JOIN FETCH ".concat(prop)).collect(Collectors.joining(" "));
		
		@SuppressWarnings("rawtypes")
		Query query = hibernate.query(StringUtils.concat(
				createFrom(entityName, alias, viewJoins), 
				" WHERE ",
				alias, ".id IN (:ids)")
			);
		
		return query;
	}
	
	private String createFrom(String entityName, String alias, String customJoins){
		if(customJoins == null)customJoins = "";
		String result = StringUtils.concat(" FROM ",  entityName, " ", alias);
		
		String fetch = createFetch(entityName, alias);
		
		return StringUtils.concat(result, " ", fetch, " ", customJoins);
	}
	
	private String createFetch(String entityName, String alias){
		String result = "";
		
		List<PropertyMetadata> properties = EntitiesMetadata.INSTANCE.getPropertiesByTable(entityName);
		List<PropertyMetadata> fks = properties.stream().filter(entity -> entity.isFk()).collect(Collectors.toList());
		if(!Utils.isEmpty(fks)){
			for(PropertyMetadata fk : fks){
				result = StringUtils.concat(result, (fk.isRequired() ? " INNER " : " LEFT "), " JOIN FETCH ", alias, ".", fk.getNome(), " as ", fk.getNome());
			};
		}
		
		List<OneToManyMetadata> ones = EntitiesMetadata.INSTANCE.getOneToManysByTable(entityName);
		if(!Utils.isEmpty(ones)){
			for(OneToManyMetadata one : ones){
				result = StringUtils.concat(result, " LEFT JOIN FETCH ", alias, ".", one.getNome(),  " as ", one.getNome().toLowerCase());
				
				String fetchToOneToMany = createFetchToOneToMany(one.getClasse(), one.getNome().toLowerCase(), entityName);
				
				result = result.concat(fetchToOneToMany);
			}
		}
		
		return result;
	}
	
	private String createFetchToOneToMany(String entityName, String alias, String parentTable){
		String result = "";
		
		List<PropertyMetadata> properties = EntitiesMetadata.INSTANCE.getPropertiesByTable(entityName);
		List<PropertyMetadata> fks = properties.stream().filter(entity -> entity.isFk()).collect(Collectors.toList());
		if(!Utils.isEmpty(fks)){
			for(PropertyMetadata fk : fks){
				if(fk.getType().getTypeName().endsWith(".".concat(parentTable)))continue;
				result = StringUtils.concat(result, " LEFT JOIN FETCH ", alias, ".", fk.getNome(), " as ", fk.getNome());
			};
		}
		
		return result;
	}

}
