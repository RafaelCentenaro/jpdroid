package br.com.rafael.pedidojpdroid.entity;

import java.io.Serializable;

import br.com.rafael.jpdroid.annotations.Column;
import br.com.rafael.jpdroid.annotations.Entity;
import br.com.rafael.jpdroid.annotations.PrimaryKey;

@Entity
public class Estado implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@PrimaryKey
	@Column(name="_id")
	private long _id;
	
	@Column(name="nome")
	private String nome;
	
	@Column(name="sigla")
	private String sigla;

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

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
}
