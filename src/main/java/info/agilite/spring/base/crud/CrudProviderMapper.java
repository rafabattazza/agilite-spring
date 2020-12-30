package info.agilite.spring.base.crud;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

@Service
public class CrudProviderMapper {
	@Value("${info.agilite.spring.provider.base-package}")
	private String providerBasePackage;
	
	private final Map<String, Class<? extends AgiliteCrudProvider>> crudProviders = new HashMap<>();

	public AgiliteCrudProvider getCrudProvider(String entityName, ApplicationContext appContext) {
		if(crudProviders.containsKey(entityName.toLowerCase())) {
			return appContext.getBean(crudProviders.get(entityName.toLowerCase()));
		}else {
			return appContext.getBean(CrudProviderDefault.class);
		}
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized void init()  {
		try {
			Long t1 = System.currentTimeMillis();
			ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
			scanner.addIncludeFilter(new AnnotationTypeFilter(CrudProvider.class));

			for (BeanDefinition bd : scanner.findCandidateComponents(providerBasePackage)) {
				Class c = Class.forName(bd.getBeanClassName());
				if(c.isAnnotationPresent(CrudProvider.class)) {
					CrudProvider a = (CrudProvider)c.getAnnotation(CrudProvider.class);
					crudProviders.put(a.value().toLowerCase(), c);
				}
			}
			System.out.println("TimeLapse Provider Scanner " + (System.currentTimeMillis()-t1));
		}catch (Exception e) {
			throw new RuntimeException("Erro ao ler as classes anotadas com @CrudProvider", e);
		}
	}
}

