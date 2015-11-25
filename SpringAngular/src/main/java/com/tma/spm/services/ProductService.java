package com.tma.spm.services;

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
}
