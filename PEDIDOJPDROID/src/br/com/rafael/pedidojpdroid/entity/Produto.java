package br.com.rafael.pedidojpdroid.entity;

import android.graphics.Bitmap;
import br.com.rafael.jpdroid.annotations.Column;
import br.com.rafael.jpdroid.annotations.Entity;
import br.com.rafael.jpdroid.annotations.PrimaryKey;

@Entity
public class Produto  {

  
	@PrimaryKey
	@Column(name="_id")
	private long _id;
	
	@Column(name="nome")
	private String nome;
	
	@Column(name="unidadeMedida")
	private String unidadeMedida;
	
	@Column(name="quantidade")
	private double quantidade;
	
	@Column(name="preco")
	private double preco;
	
	@Column(name="foto")
	private Bitmap foto;


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

	public Bitmap getFoto() {
		return foto;
	}

	public void setFoto(Bitmap foto) {
		this.foto = foto;
	}

	public double getPreco() {
		return preco;
	}

	public void setPreco(double preco) {
		this.preco = preco;
	}

	public double getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(double quantidade) {
		this.quantidade = quantidade;
	}

	public String getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(String unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}
}
