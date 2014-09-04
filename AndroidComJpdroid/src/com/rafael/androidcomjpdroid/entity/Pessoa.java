package com.rafael.androidcomjpdroid.entity;

import java.util.List;

import br.com.rafael.jpdroid.annotations.Column;
import br.com.rafael.jpdroid.annotations.Entity;
import br.com.rafael.jpdroid.annotations.PrimaryKey;
import br.com.rafael.jpdroid.annotations.RelationClass;
import br.com.rafael.jpdroid.enums.RelationType;

@Entity
public class Pessoa {

	@PrimaryKey
	@Column
	private long _id;
	
	@Column
	private String nome;
	
	@RelationClass(relationType=RelationType.ManyToOne,joinColumn="idPessoa")
	private List<Contato> contatos;
	
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
	public List<Contato> getContatos() {
		return contatos;
	}
	public void setContatos(List<Contato> contatos) {
		this.contatos = contatos;
	}
}
