package info.agilite.spring.base;


import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.http.MediaType;

public class DadosParaDownload {
	private byte[] dados;
	private String nome;
	private MediaType type;
	private final Map<String, String> headers = new HashedMap<>();
	
	public DadosParaDownload() {
		super();
	}
	public DadosParaDownload(byte[] dados, String nome, MediaType type) {
		super();
		this.dados = dados;
		this.nome = nome;
		this.type = type;
	}
	public byte[] getDados() {
		return dados;
	}
	public void setDados(byte[] dados) {
		this.dados = dados;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public MediaType getType() {
		return type;
	}
	public void setType(MediaType type) {
		this.type = type;
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	public String putHeader(String key, String value) {
		return headers.put(key, value);
	}
	
	
}
