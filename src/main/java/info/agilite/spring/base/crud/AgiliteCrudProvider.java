package info.agilite.spring.base.crud;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import info.agilite.spring.base.AgiliteAbstractEntity;
import info.agilite.spring.base.UserSession;
import info.agilite.spring.base.database.HibernateWrapper;

public interface AgiliteCrudProvider{
	void init(Class<? extends AgiliteAbstractEntity> entityClass, UserSession session, HibernateWrapper hibernate);
	
	//Listar - Filtrar
	CrudListResponse listar(CrudListRequest crudRequest);
	
	//Operacoes da lista
	void arquivar(List<Long> ids);
	void desArquivar(List<Long> ids);
	AgiliteAbstractEntity editar(Long id, List<String> viewPropertiesToFetchJoin);
	AgiliteAbstractEntity copiar(Long id, List<String> viewPropertiesToFetchJoin);
	void deletar(List<Long> ids);
	void imprimir(List<Long> ids, HttpServletResponse response);
	void exportar(String type, List<Long> ids, HttpServletResponse response);
	
	
	//Operacoes do editar
	AgiliteAbstractEntity novo();
	void salvar(AgiliteAbstractEntity entity);
	
	//Anexos
	void salvarAnexos(AgiliteAbstractEntity entity);
	void carregarAnexos(AgiliteAbstractEntity entity);
	void deletarAnexos(Long id);
	
}