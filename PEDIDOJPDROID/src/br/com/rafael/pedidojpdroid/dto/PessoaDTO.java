package br.com.rafael.pedidojpdroid.dto;

import java.util.List;

import br.com.rafael.jpdroid.annotations.Dto;
import br.com.rafael.jpdroid.annotations.DtoField;

@Dto
public class PessoaDTO {

	@DtoField
	private long _id;
	@DtoField
	private String nome;
	@DtoField
	private List<ContatoDTO> contato;
	
	public long get_id() {
		return _id;
	}
	public void set_id(long _id) {
		this._id = _id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
}
