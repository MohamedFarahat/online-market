package com.miu.onlinemarket.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotEmpty;

@Entity
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@OneToOne
	private Product product;

	private long quantity = 0;

	@NotEmpty
	private Status shippingStatus;

	public Item(Product product, long quantity, Status shippingStatus) {
		this.product = product;
		this.quantity = quantity;
		this.shippingStatus = shippingStatus;
	}

	public Item() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public Status getShippingStatus() {
		return shippingStatus;
	}

	public void setShippingStatus(Status shippingStatus) {
		this.shippingStatus = shippingStatus;
	}

}
