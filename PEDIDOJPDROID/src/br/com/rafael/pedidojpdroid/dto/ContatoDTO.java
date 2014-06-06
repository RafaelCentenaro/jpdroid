package br.com.rafael.pedidojpdroid.dto;

import br.com.rafael.jpdroid.annotations.Dto;
import br.com.rafael.jpdroid.annotations.DtoField;

@Dto
public class ContatoDTO {
	
	@DtoField
	private long _id;
	@DtoField
	private String contato;
	public long get_id() {
		return _id;
	}
	public void set_id(long _id) {
		this._id = _id;
	}
	public String getContato() {
		return contato;
	}
	public void setContato(String contato) {
		this.contato = contato;
	}
}
