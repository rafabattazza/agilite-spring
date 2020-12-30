package info.agilite.spring.base.crud;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import info.agilite.spring.base.AgiliteAbstractEntity;
import info.agilite.spring.base.UserSession;
import info.agilite.spring.base.database.HibernateWrapper;
import info.agilite.spring.base.metadata.EntitiesMetadata;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgiliteCrudService {
	@Value("${info.agilite.spring.entity.base-package}")
	private String entityBasePackage;
	
	private final CrudProviderMapper providerMapper;
	private final ApplicationContext appContext;
	private final UserSession session;
	private final HibernateWrapper hibenateWrapper;
	
	public void saveEntity(AgiliteAbstractEntity entity) {
		getCrudProvider(entity.getClass().getSimpleName()).salvar(entity);
	}
	
	public CrudListResponse list(String classe, CrudListRequest crudRequest) {
		return getCrudProvider(classe).listar(crudRequest);
	}
	
	public Object editar(String entityName, Long idEntity, List<String> viewPropertiesToFetchJoin) {
		return getCrudProvider(entityName).editar(idEntity, viewPropertiesToFetchJoin);
	}
	
	public Object copiar(String entityName, Long idEntity, List<String> viewPropertiesToFetchJoin) {
		return getCrudProvider(entityName).copiar(idEntity, viewPropertiesToFetchJoin);
	}
	
	public void arquivar(String entityName, List<Long> ids) {
		getCrudProvider(entityName).arquivar(ids);
	}
	
	public void imprimir(String entityName, List<Long> ids, HttpServletResponse response) {
		getCrudProvider(entityName).imprimir(ids, response);
	}

	public void exportarExcel(String entityName, List<Long> ids, HttpServletResponse response) {
		getCrudProvider(entityName).exportar("excel", ids, response);
	}
	
	public void desArquivar(String entityName, List<Long> ids) {
		getCrudProvider(entityName).desArquivar(ids);
	}
	
	public void delete(String entityName, List<Long> ids) {
		getCrudProvider(entityName).deletar(ids);
	}
	
	public AgiliteAbstractEntity novo(String entityName) {
		return getCrudProvider(entityName).novo();
	}
	
	protected AgiliteCrudProvider getCrudProvider(final String entityName) {
		AgiliteCrudProvider crudProvider;
		try {
			crudProvider = providerMapper.getCrudProvider(entityName, appContext);
		} catch (Exception e) {
			throw new RuntimeException("Erro ao criar classe do Provider para o cadastro", e);
		}
		
		Class<? extends AgiliteAbstractEntity> entityClass = EntitiesMetadata.INSTANCE.getEntityClass(entityName);		
		
		crudProvider.init(entityClass, hibenateWrapper, session);
		return crudProvider;
	}
}