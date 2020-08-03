package info.agilite.spring.base.crud;

public class WebServiceCep {
	private String resultado_txt;
    private String uf;
    private String estado;
    private String cidade;
    private String bairro;
    private String logradouro;
    private String logradouro_curto;
    private Long municipioId;
    
	public String getResultado_txt() {
		return resultado_txt;
	}
	public void setResultado_txt(String resultado_txt) {
		this.resultado_txt = resultado_txt;
	}
	public String getUf() {
		return uf;
	}
	public void setUf(String uf) {
		this.uf = uf;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public String getBairro() {
		return bairro;
	}
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	public String getLogradouro() {
		return logradouro;
	}
	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}
	public String getLogradouro_curto() {
		return logradouro_curto;
	}
	public void setLogradouro_curto(String logradouro_curto) {
		this.logradouro_curto = logradouro_curto;
	}
	public Long getMunicipioId() {
		return municipioId;
	}
	public void setMunicipioId(Long municipioId) {
		this.municipioId = municipioId;
	}
}
