package br.com.pockettaxi.client.model;

public class Taxista {
	private Long id;
	private String nome;
	private Long viatura;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public Long getViatura() {
		return viatura;
	}
	
	public void setViatura(Long viatura) {
		this.viatura = viatura;
	}	
}
