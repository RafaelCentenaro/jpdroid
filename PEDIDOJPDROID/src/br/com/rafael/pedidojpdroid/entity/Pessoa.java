package br.com.rafael.pedidojpdroid.entity;

import java.util.List;

import android.graphics.Bitmap;
import br.com.rafael.jpdroid.annotations.Column;
import br.com.rafael.jpdroid.annotations.Entity;
import br.com.rafael.jpdroid.annotations.DefaultOrder;
import br.com.rafael.jpdroid.annotations.PrimaryKey;
import br.com.rafael.jpdroid.annotations.RelationClass;
import br.com.rafael.jpdroid.enums.Order;
import br.com.rafael.jpdroid.enums.RelationType;

@Entity
public class Pessoa {
	
	@PrimaryKey
	@Column
	private long _id;
	
	@DefaultOrder(order=Order.asc)
	@Column
	private String nome;
	
	@RelationClass(relationType=RelationType.ManyToOne,joinColumn="idPessoa")
	private List<Endereco> endereco;
	
	@Column
	private Bitmap foto;
	
	@RelationClass(relationType=RelationType.ManyToOne,joinColumn="idPessoa")
	private List<Contato> contato;

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

	public List<Endereco> getEndereco() {
		return endereco;
	}

	public void setEndereco(List<Endereco> endereco) {
		this.endereco = endereco;
	}

	public List<Contato> getContato() {
		return contato;
	}

	public void setContato(List<Contato> contato) {
		this.contato = contato;
	}

	public void addContato(Contato contato) {
		this.contato.add(contato);
  }
	public void addEndereco(Endereco endereco) {
		this.endereco.add(endereco);
  }

	public Bitmap getFoto() {
		return foto;
	}

	public void setFoto(Bitmap foto) {
		this.foto = foto;
	}
}
