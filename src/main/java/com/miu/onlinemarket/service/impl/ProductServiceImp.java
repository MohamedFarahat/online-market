package com.miu.onlinemarket.service.impl;

import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miu.onlinemarket.domain.Product;
import com.miu.onlinemarket.exceptionhandling.ResourceNotFoundException;
import com.miu.onlinemarket.repository.ProductRepository;
import com.miu.onlinemarket.service.ProductService;

@Service
public class ProductServiceImp implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Override
	public List<Product> findAll() {
		List<Product> products = productRepository.findAll();
		products.forEach(product -> {
			if (product.getPhoto() != null && product.getPhoto().length != 0)
				product.setPhotoBase64(Base64.getEncoder().encodeToString(product.getPhoto()));
		});
		return products;
	}

	@Override
	public List<Product> searchByName(String name) {
		List<Product> products = productRepository.SearchByName(name);
		products.forEach(product -> {
			if (product.getPhoto() != null && product.getPhoto().length != 0)
				product.setPhotoBase64(Base64.getEncoder().encodeToString(product.getPhoto()));
		});
		return products;
	}

	@Override
	public Product findById(Long id) throws ResourceNotFoundException {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
		if (product.getPhoto() != null && product.getPhoto().length != 0)
			product.setPhotoBase64(Base64.getEncoder().encodeToString(product.getPhoto()));
		return product;
	}

	@Override
	public Product save(Product product) {
		return productRepository.save(product);
	}

	@Override
	public void delete(Product product) {

		productRepository.delete(product);
	}

	@Override
	public Product update(Product product, Long id) throws ResourceNotFoundException {
		Product oldProduct = productRepository.findById(id).orElse(new Product());
		if (oldProduct == null) {
			throw new ResourceNotFoundException("Product with username " + product.getName() + " is not found");
		}
		oldProduct.setName(product.getName());
		oldProduct.setDescription(product.getDescription());
		oldProduct.setQuantity(product.getQuantity());
		oldProduct.setPrice(product.getPrice());
		oldProduct.setSeller(product.getSeller());
		oldProduct.setPhoto(product.getPhoto());
		return productRepository.save(oldProduct);
	}
}
