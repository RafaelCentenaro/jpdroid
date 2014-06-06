package br.com.rafael.pedidojpdroid.entity;

import java.util.Date;
import java.util.List;

import br.com.rafael.jpdroid.annotations.Column;
import br.com.rafael.jpdroid.annotations.Entity;
import br.com.rafael.jpdroid.annotations.ForeignKey;
import br.com.rafael.jpdroid.annotations.PrimaryKey;
import br.com.rafael.jpdroid.annotations.RelationClass;
import br.com.rafael.jpdroid.enums.RelationType;

@Entity
public class Pedido {
	
	@PrimaryKey
	@Column(name="_id")
	private long _id;
	
	@Column(name="data")
	private Date data;
	
	@ForeignKey(joinEntity="Pessoa",joinPrimaryKey="_id")
	@Column(name="idCliente")
	private long idCliente;
	
	@Column(name="valorTotal")
	private double valorTotal;
	
	@RelationClass(relationType=RelationType.OneToMany,fieldName="cliente",joinColumn="idCliente",Transient=true)
	private Pessoa cliente;
	
	@ForeignKey(joinEntity="Endereco",joinPrimaryKey="_id")
	@Column(name="idEnderecoEntrega")
	private long idEnderecoEntrega;
	
	@RelationClass(relationType=RelationType.OneToMany,fieldName="enderecoEntrega",joinColumn="idEnderecoEntrega",Transient=true)
	private Endereco enderecoEntrega;
	
	@RelationClass(relationType=RelationType.ManyToOne,fieldName="itensPedido",joinColumn="idPedido")
	private List<ItensPedido> itensPedido;

	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public long getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(long idCliente) {
		this.idCliente = idCliente;
	}

	public long getIdEnderecoEntrega() {
		return idEnderecoEntrega;
	}

	public void setIdEnderecoEntrega(long idEnderecoEntrega) {
		this.idEnderecoEntrega = idEnderecoEntrega;
	}

	public List<ItensPedido> getItensPedido() {
		return itensPedido;
	}

	public void setItensPedido(List<ItensPedido> itensPedido) {
		this.itensPedido = itensPedido;
	}

	public Pessoa getCliente() {
		return cliente;
	}

	public void setCliente(Pessoa cliente) {
		this.cliente = cliente;
	}

	public double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(double valorTotal) {
		this.valorTotal = valorTotal;
	}
	
	
}
