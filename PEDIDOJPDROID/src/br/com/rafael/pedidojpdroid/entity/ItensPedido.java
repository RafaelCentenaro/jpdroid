package br.com.rafael.pedidojpdroid.entity;

import java.io.Serializable;

import br.com.rafael.jpdroid.annotations.Column;
import br.com.rafael.jpdroid.annotations.Entity;
import br.com.rafael.jpdroid.annotations.ForeignKey;
import br.com.rafael.jpdroid.annotations.Ignorable;
import br.com.rafael.jpdroid.annotations.PrimaryKey;
import br.com.rafael.jpdroid.annotations.ViewColumn;

@Entity
public class ItensPedido implements Serializable {

  private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Column
	private long _id;
	
	@ForeignKey(joinEntity=Pedido.class,joinPrimaryKey="_id",deleteCascade=true)
	@Column
	private long idPedido;
	
	@Ignorable
	@ViewColumn(entity=Produto.class,atributo="nome",foreignKey="idProduto")
	private String nomeProduto;
	
	@ForeignKey(joinEntity=Produto.class,joinPrimaryKey="_id")
	@Column
	private long idProduto;
	
	@Column
	private double qtdProduto;
	
	@Column
	private double valorUnitario;
	

	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public long getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(long idPedido) {
		this.idPedido = idPedido;
	}

	public long getIdProduto() {
		return idProduto;
	}

	public void setIdProduto(long idProduto) {
		this.idProduto = idProduto;
	}

	public double getQtdProduto() {
		return qtdProduto;
	}

	public void setQtdProduto(double qtdProduto) {
		this.qtdProduto = qtdProduto;
	}

	public double getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public String getNomeProduto() {
		return nomeProduto;
	}

	public void setNomeProduto(String nomeProduto) {
		this.nomeProduto = nomeProduto;
	}
}
