package com.tma.spm.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tma.spm.model.Product;

public interface ProductDAO extends JpaRepository<Product, Long> {

}
