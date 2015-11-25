package com.tma.spm.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tma.spm.dao.ProductDAO;
import com.tma.spm.model.Product;

@Service
public class ProductService {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	ProductDAO productDAO;

	public void create(Product product) {
		log.info("create product");
		productDAO.saveAndFlush(product);
	}

	public void update(Product product) {
		log.info("update product {}", product);
		if (product.getId() != null) {
			productDAO.save(product);
		}
	}

	public void delete(Long id) {
		log.info("delete product {}", id);
		productDAO.delete(id);
	}

	public Product getProduct(Long id) {
		log.info("get one product {}", id);
		return productDAO.findOne(id);
	}

	public List<Product> getProducts() {
		log.info("get all product ");
		return productDAO.findAll();
	}
}
