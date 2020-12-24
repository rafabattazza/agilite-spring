package info.agilite.spring.base.crud;

import java.lang.reflect.Field;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;

import info.agilite.spring.base.AgiliteAbstractEntity;
import info.agilite.spring.base.UserSession;
import info.agilite.spring.base.database.HibernateWrapper;
import info.agilite.spring.base.json.RemoveIdsJsonFilter;
import info.agilite.spring.base.metadata.EntitiesMetadata;
import info.agilite.utils.StringUtils;
import info.agilite.utils.jackson.JSonMapperCreator;

public class CrudProviderDefault implements AgiliteCrudProvider{
	protected Class<? extends AgiliteAbstractEntity> entityClass;
	protected UserSession session;
	protected HibernateWrapper hibernate;
	
	@Override
	public void init(Class<? extends AgiliteAbstractEntity> entityClass, UserSession session, HibernateWrapper hibernate) {
		this.entityClass = entityClass;
		this.session = session;
		this.hibernate = hibernate;
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
		hibernate.query("UPDATE " + entityName + " SET " + entityName + "arquivado = :arquivado WHERE " + entityName +"id IN (:ids)")
		  .setParameter("arquivado", 1)
		  .setParameter("ids", ids)
		  .executeUpdate();
	}
	
	@Override
	public void desArquivar(List<Long> ids) {
		String entityName = entityClass.getSimpleName();
		hibernate.query("UPDATE " + entityName + " SET " + entityName + "arquivado = null WHERE " + entityName +"id IN (:ids)")
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
			List<?> anexos = hibernate.query(StringUtils.concat("FROM Ax10 WHERE LOWER(ax10table) = :ax10table AND ax10regId = :ax10regId AND ax10arquivado IS NULL ORDER BY ax10clientName"))
			  .setParameter("ax10table", entity.getClass().getSimpleName().toLowerCase())
			  .setParameter("ax10regId",  ((AgiliteAbstractEntity)entity).getIdValue())
			  .list();

			Field field = entity.getClass().getDeclaredField("ax10s");
			field.setAccessible(true);
			field.set(entity, anexos);
		} catch (Exception e) {
			throw new RuntimeException("Erro ao carregar arquivos anexos", e);
		}
	}

	@Override
	public void deletarAnexos(Long id) {
		hibernate.query(StringUtils.concat("UPDATE Ax10 SET ax10arquivado = 1 WHERE LOWER(ax10table) = :ax10table AND ax10regId = :ax10regId"))
		  .setParameter("ax10table", entityClass.getSimpleName().toLowerCase())
		  .setParameter("ax10regId",  id)
		  .executeUpdate();
	}
}
