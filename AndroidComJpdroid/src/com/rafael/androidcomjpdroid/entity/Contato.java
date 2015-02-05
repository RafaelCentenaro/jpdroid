package com.rafael.androidcomjpdroid.entity;

import java.io.Serializable;

import br.com.rafael.jpdroid.annotations.Column;
import br.com.rafael.jpdroid.annotations.Entity;
import br.com.rafael.jpdroid.annotations.ForeignKey;
import br.com.rafael.jpdroid.annotations.Ignorable;
import br.com.rafael.jpdroid.annotations.PrimaryKey;
import br.com.rafael.jpdroid.annotations.RelationClass;
import br.com.rafael.jpdroid.annotations.ViewColumn;
import br.com.rafael.jpdroid.enums.RelationType;

@Entity
public class Contato implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Column
	private Long _id;
	
	@ForeignKey(joinEntity=TipoContato.class,joinPrimaryKey="_id")
	@Column
	private Long idTipoContato;
	
	@Ignorable
	@ViewColumn(entity=TipoContato.class,foreignKey="idTipoContato", atributo = "descricao")
	private String nomeTipoContato;

	@ForeignKey(joinEntity=Pessoa.class,joinPrimaryKey="_id",deleteCascade=true)
	@Column
	private Long idPessoa;
	
	@RelationClass(relationType=RelationType.OneToMany, joinColumn="idTipoContato")
	private TipoContato tipoContato;
	
	@Column
	private String contato;

	public Long get_id() {
		return _id;
	}

	public void set_id(Long _id) {
		this._id = _id;
	}

	public Long getIdTipoContato() {
		return idTipoContato;
	}

	public void setIdTipoContato(Long idTipoContato) {
		this.idTipoContato = idTipoContato;
	}

	public TipoContato getTipoContato() {
		return tipoContato;
	}

	public void setTipoContato(TipoContato tipoContato) {
		this.tipoContato = tipoContato;
	}

	public String getContato() {
		return contato;
	}

	public void setContato(String contato) {
		this.contato = contato;
	}
	
	public String getNomeTipoContato() {
		return nomeTipoContato;
	}

	public void setNomeTipoContato(String nomeTipoContato) {
		this.nomeTipoContato = nomeTipoContato;
	}

	public Long getIdPessoa() {
		return idPessoa;
	}

	public void setIdPessoa(Long idPessoa) {
		this.idPessoa = idPessoa;
	}
	

}
