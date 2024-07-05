package com.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.entity.Product;

public interface ProductRepo extends JpaRepository<Product, Integer> {
}
