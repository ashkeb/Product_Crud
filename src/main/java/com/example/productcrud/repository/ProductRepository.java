package com.example.productcrud.repository;

import com.example.productcrud.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Basic CRUD methods (save, findById, findAll, deleteById, existsById) are
    // inherited from JpaRepository - no extra code needed for the required operations.
}
