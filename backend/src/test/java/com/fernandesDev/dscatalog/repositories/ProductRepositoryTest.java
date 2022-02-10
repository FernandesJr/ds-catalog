package com.fernandesDev.dscatalog.repositories;

import java.util.Optional;

import com.fernandesDev.dscatalog.services.exceptions.ResourceNotFoundException;
import com.fernandesDev.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.fernandesDev.dscatalog.entities.Product;

import javax.persistence.EntityNotFoundException;

@DataJpaTest
public class ProductRepositoryTest {
	
	@Autowired
	private ProductRepository repository;

	long exintingId;
	long notExistingId;
	long countTotalProducts;

	@BeforeEach
	void setUp() {
		//Executa antes de todos os métodos
		exintingId = 1L;
		notExistingId = 1000L;
		countTotalProducts = 25;
	}

	@Test
	public void deleteShouldDeleteWhenIdExists() {

		repository.deleteById(exintingId);
		Optional<Product> result = repository.findById(exintingId);
		Assertions.assertFalse(result.isPresent());
	}
	
	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist() {

		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(notExistingId);
		});
	}

	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull(){
		Product product = Factory.creatProduct();
		product.setId(null);

		repository.save(product);

		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts + 1, product.getId());

	}

	@Test
	public void findShouldFindProductWhenIdIsExist(){
		Optional<Product> product = repository.findById(exintingId);
		Assertions.assertNotNull(product);
	}

	@Test
	public void findShouldThrowExceptionWhenIdDoesNotExist(){

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			Product product = repository.findById(notExistingId).orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		});
	}
}
