package br.com.rafael.pedidojpdroid.entity;

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
public class Endereco implements Serializable {

	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Column(name = "_id")
	private long _id;

	@ForeignKey(joinEntity = "Pessoa", joinPrimaryKey = "_id", deleteCascade = true)
	@Column(name = "idPessoa", nullable = false)
	private long idPessoa;

	@Column(name = "rua")
	private String rua;

	@Column(name = "bairro")
	private String bairro;

	@Column(name = "numero")
	private long numero;

	@ForeignKey(joinEntity = "Cidade", joinPrimaryKey = "_id")
	@Column(name = "id_Cidade")
	private long id_Cidade;

	@RelationClass(relationType = RelationType.OneToMany, fieldName = "cidade", joinColumn = "id_Cidade",Transient=true)
	private Cidade cidade;

	@Column(name = "principal")
	private boolean principal;

	@Ignorable
	@ViewColumn(entity="Cidade",atributo="nome",foreignKey="id_Cidade")
	private String nomeCidade;

	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public long getIdPessoa() {
		return idPessoa;
	}

	public void setIdPessoa(long idPessoa) {
		this.idPessoa = idPessoa;
	}

	public String getRua() {
		return rua;
	}

	public void setRua(String rua) {
		this.rua = rua;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public long getNumero() {
		return numero;
	}

	public void setNumero(long numero) {
		this.numero = numero;
	}

	public long getId_Cidade() {
		return id_Cidade;
	}

	public void setId_Cidade(long id_Cidade) {
		this.id_Cidade = id_Cidade;
	}

	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}

	public boolean isPrincipal() {
		return principal;
	}

	public void setPrincipal(boolean principal) {
		this.principal = principal;
	}

	public String getNomeCidade() {
		return nomeCidade;
	}

	public void setNomeCidade(String nomeCidade) {
		this.nomeCidade = nomeCidade;
	}
}
