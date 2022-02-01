package com.fernandesDev.dscatalog.repositories;

import com.fernandesDev.dscatalog.entities.Category;
import com.fernandesDev.dscatalog.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}