package com.tma.spm.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tma.spm.model.Product;
import com.tma.spm.services.ProductService;

@RestController
public class ProductController {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private ProductService productService;

	@RequestMapping(value = "/product", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Product>> getAll() {
		return new ResponseEntity<List<Product>>(productService.getProducts(), HttpStatus.OK);
	}

	@RequestMapping(value = "/product", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> create(@RequestBody Product product) throws URISyntaxException {
		log.info("create product {}", product);
		if (product.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new product cannot already have an ID").build();
		}
		productService.create(product);
		return ResponseEntity.created(new URI("/product/" + product.getId())).build();
	}

	@RequestMapping(value = "/product/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Product> get(@PathVariable("id") Long id) {
		log.info("Rest get product by id {}", id);
		Product product = productService.getProduct(id);
		if (product == null) {
			log.debug("Query not found {}", id);
			return new ResponseEntity<Product>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Product>(product, HttpStatus.OK);
	}

	@RequestMapping(value = "/product/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Product> update(@PathVariable("id") Long id, @RequestBody Product product) {
		Product pro = productService.getProduct(id);
		if (pro == null) {
			log.debug("Query not found product {}", id);
			return new ResponseEntity<Product>(HttpStatus.NOT_FOUND);
		}
		pro.setName(product.getName());
		pro.setDescription(product.getDescription());
		productService.update(pro);
		return new ResponseEntity<Product>(pro, HttpStatus.OK);
	}

	@RequestMapping(value = "/product/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Product> delete(@PathVariable("id") Long id) {
		log.info("rest delete product {}", id);
		Product product = productService.getProduct(id);
		if (product == null) {
			return new ResponseEntity<Product>(HttpStatus.NOT_FOUND);
		}
		productService.delete(id);
		return new ResponseEntity<Product>(HttpStatus.NO_CONTENT);
	}
}
