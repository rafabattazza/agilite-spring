package info.agilite.spring.base.crud;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import info.agilite.spring.base.AgiliteAbstractEntity;
import info.agilite.spring.base.UserSession;
import info.agilite.spring.base.database.HibernateWrapper;
import info.agilite.spring.base.json.RemoveIdsJsonFilter;
import info.agilite.spring.base.metadata.EntitiesMetadata;
import info.agilite.utils.StringUtils;
import info.agilite.utils.jackson.JSonMapperCreator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CrudProviderDefault implements AgiliteCrudProvider{
	protected Class<? extends AgiliteAbstractEntity> entityClass;
	protected UserSession session;
	protected HibernateWrapper hibernate;
	
	@Override
	public void init(Class<? extends AgiliteAbstractEntity> entityClass, HibernateWrapper hibernate, UserSession session) {
		this.entityClass = entityClass;
		this.hibernate = hibernate;
		this.session = session;
	}
	
	@Override
	public void salvar(AgiliteAbstractEntity entity) {
		//TODO - Montar LOGS de alteração
		hibernate.persistForceRemoveChildren(entity);
		salvarAnexos(entity);
	}
	
	@Override
	public CrudListResponse listar(CrudListRequest crudRequest) {
		return new CrudProviderListUtils(entityClass, session, hibernate).listar(crudRequest);
	}
	
	@Override
	public AgiliteAbstractEntity novo() {
		try {
			//TODO - Montar LOGS de inclusão
			return entityClass.newInstance();
		} catch (Exception err) {
			throw new RuntimeException("Erro ao instanciar um novo registro do tipo " + entityClass.getSimpleName(), err);
		}
	}
	
	@Override
	public AgiliteAbstractEntity editar(Long id, List<String> viewPropertiesToFetchJoin) {
		AgiliteAbstractEntity entity = new CrudProviderEditUtils(entityClass, session, hibernate).editar(id, viewPropertiesToFetchJoin);
		carregarAnexos(entity);
		return entity;
	}
	
	@Override
	public void arquivar(List<Long> ids) {
		String entityName = entityClass.getSimpleName();
		hibernate.query("UPDATE " + entityName + " SET " + entityName + "dtArq = :dtArq WHERE " + entityName +"id IN (:ids)")
		  .setParameter("dtArq", LocalDate.now())
		  .setParameter("ids", ids)
		  .executeUpdate();
	}
	
	@Override
	public void desArquivar(List<Long> ids) {
		String entityName = entityClass.getSimpleName();
		hibernate.query("UPDATE " + entityName + " SET " + entityName + "dtArq = null WHERE " + entityName +"id IN (:ids)")
		  .setParameter("ids", ids)
		  .executeUpdate();
	}


	
	@Override
	public void deletar(List<Long> ids) {
		String entityName = entityClass.getSimpleName();
		for(Long id : ids) {
			hibernate.delete(EntitiesMetadata.INSTANCE.getEntityClass(entityName), id);
			
			deletarAnexos(id);
		}
	}

	@Override
	public AgiliteAbstractEntity copiar(Long id, List<String> viewPropertiesToFetchJoin) {
		AgiliteAbstractEntity entity = editar(id, viewPropertiesToFetchJoin);
		
		String json;
		try {
			 json = JSonMapperCreator.createWithNoAttFilter(new RemoveIdsJsonFilter(entityClass)).writeValueAsString(entity);
			 entity = JSonMapperCreator.create().readValue(json, entityClass);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Erro ao copiar registros", e);
		}
		
		return entity;
	}

	@Override
	public void imprimir(List<Long> ids, HttpServletResponse response) {
		throw new RuntimeException("Método imprimir não implementado");
	}
	
	protected List<CrudExportColumn> getColunasExportacao() {
		return new CrudProviderExportarUtils(entityClass, hibernate).getColunasExportacao();
	}
	
	protected String getJoinJdbcParaExportacao() {
		return new CrudProviderExportarUtils(entityClass, hibernate).getJoinJdbcParaExportacao();
	}
	
	@Override
	public void exportar(String type, List<Long> ids, HttpServletResponse response) {
		List<CrudExportColumn> colunas = getColunasExportacao();
		String joinJdbc = getJoinJdbcParaExportacao();
		
		new CrudProviderExportarUtils(entityClass, hibernate).exportar(type, ids, response, colunas, joinJdbc);
	}
	
	@Override
	public void salvarAnexos(AgiliteAbstractEntity entity) {
		List<Long> filesIds = ((AgiliteAbstractEntity)entity).getFilesIds();
		if(filesIds != null){
			String table = EntitiesMetadata.INSTANCE.getTableToFiles();
			hibernate.query(StringUtils.concat("UPDATE ", table, " SET ", table, "reg_id = :regId WHERE ", table,"id IN (:ids)"))
			  .setParameter("regId", ((AgiliteAbstractEntity)entity).getIdValue())
			  .setParameter("ids", filesIds)
			  .executeUpdate();
		}
	}

	@Override
	public void carregarAnexos(AgiliteAbstractEntity entity) {
		try {
			List<?> anexos = hibernate.query(StringUtils.concat("FROM Ca10 WHERE LOWER(ca10tabela) = :ca10tabela AND ca10regId = :ca10regId AND ca10dtArq IS NULL ORDER BY ca10nome"))
			  .setParameter("ca10tabela", entity.getClass().getSimpleName().toLowerCase())
			  .setParameter("ca10regId",  ((AgiliteAbstractEntity)entity).getIdValue())
			  .list();

			Field field = entity.getClass().getDeclaredField("ca10s");
			field.setAccessible(true);
			field.set(entity, anexos);
		} catch (Exception e) {
			throw new RuntimeException("Erro ao carregar arquivos anexos", e);
		}
	}

	@Override
	public void deletarAnexos(Long id) {
		hibernate.query(StringUtils.concat("UPDATE Ca10 SET ca10dtArq = NOW() WHERE LOWER(ca10tabela) = :ca10tabela AND ca10regId = :ca10regId"))
		  .setParameter("ca10tabela", entityClass.getSimpleName().toLowerCase())
		  .setParameter("ca10regId",  id)
		  .executeUpdate();
	}
}
