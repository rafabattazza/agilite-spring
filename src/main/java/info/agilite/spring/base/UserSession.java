package info.agilite.spring.base;

public interface UserSession {
	public int getNivelUsuario();
	
	public void validarLeitura(String tarefa);
	public void validarAlteracao(String tarefa);
}
