package br.com.rafael.pedidojpdroid.entity;

import java.io.Serializable;

import br.com.rafael.jpdroid.annotations.Column;
import br.com.rafael.jpdroid.annotations.Entity;
import br.com.rafael.jpdroid.annotations.ForeignKey;
import br.com.rafael.jpdroid.annotations.PrimaryKey;
import br.com.rafael.jpdroid.annotations.RelationClass;
import br.com.rafael.jpdroid.enums.RelationType;

@Entity
public class Cidade implements Serializable {

	private static final long serialVersionUID = 1L;


	@PrimaryKey
	@Column(name="_id")
	private long _id;
	
	@Column(name="nome")
	private String nome;
	
	@ForeignKey(joinEntity="Estado",joinPrimaryKey="_id")
	@Column(name="id_estado")
	private long id_estado;
	
	@RelationClass(relationType=RelationType.OneToMany,fieldName="estado",joinColumn="id_estado")
	private Estado estado;
	
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

	public long getId_estado() {
		return id_estado;
	}

	public void setId_estado(long id_estado) {
		this.id_estado = id_estado;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

}
