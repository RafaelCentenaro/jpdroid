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
	@Column
	private long _id;
	
	@Column
	private Date data;
	
	@ForeignKey(joinEntity=Pessoa.class,joinPrimaryKey="_id")
	@Column
	private long idCliente;
	
	@Column
	private double valorTotal;
	
	@RelationClass(relationType=RelationType.OneToMany,joinColumn="idCliente",Transient=true)
	private Pessoa cliente;
	
	@ForeignKey(joinEntity=Endereco.class,joinPrimaryKey="_id")
	@Column
	private long idEnderecoEntrega;
	
	@RelationClass(relationType=RelationType.OneToMany,joinColumn="idEnderecoEntrega",Transient=true)
	private Endereco enderecoEntrega;
	
	@RelationClass(relationType=RelationType.ManyToOne,joinColumn="idPedido")
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
