package com.fernandesDev.dscatalog.services;

import com.fernandesDev.dscatalog.dto.ProductDTO;
import com.fernandesDev.dscatalog.entities.Category;
import com.fernandesDev.dscatalog.entities.Product;
import com.fernandesDev.dscatalog.repositories.CategoryRepository;
import com.fernandesDev.dscatalog.repositories.ProductRepository;
import com.fernandesDev.dscatalog.services.exceptions.DataBaseException;
import com.fernandesDev.dscatalog.services.exceptions.ResourceNotFoundException;
import com.fernandesDev.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    long exintingId;
    long notExistingId;
    long dependentId;
    PageImpl<Product> page;
    Product product;
    Category category;

    @BeforeEach
    void setUp() {
        exintingId = 1L;
        notExistingId = 1000L;
        dependentId = 4L;
        product = Factory.creatProduct();
        page = new PageImpl<>(List.of(product));
        category = Factory.creatCategory();

        //Preparando os objetos Mokados, preparando os comportamentos do que deve acontecer
        Mockito.doNothing().when(productRepository).deleteById(exintingId);

        Mockito.doThrow(ResourceNotFoundException.class).when(productRepository).deleteById(notExistingId);

        Mockito.doThrow(DataBaseException.class).when(productRepository).deleteById(dependentId);

        Mockito.when(productRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(productRepository.findById(exintingId)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.findById(notExistingId)).thenReturn(Optional.empty());

        Mockito.when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(productRepository.getById(exintingId)).thenReturn(product);
        Mockito.doThrow(ResourceNotFoundException.class).when(productRepository).getById(notExistingId);

        Mockito.when(categoryRepository.getById(exintingId)).thenReturn(category);


    }

    @Test
    public void deleteShouldDoThingWhenIdExists(){
        Assertions.assertDoesNotThrow(() -> {
            productService.delete(exintingId);
        });

        //Times verifica quantas vezes o método é chamado
        Mockito.verify(productRepository, Mockito.times(1)).deleteById(exintingId);
    }

    @Test
    public void deleteShouldResourceNotFoundExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(notExistingId);
        });

        //Times verifica quantas vezes o método é chamado
        Mockito.verify(productRepository, Mockito.times(1)).deleteById(notExistingId);
    }

    @Test
    public void deleteShouldDataBaseExceptionWhenDependentId(){
        Assertions.assertThrows(DataBaseException.class, () -> {
            productService.delete(dependentId);
        });

        //Times verifica quantas vezes o método é chamado
        Mockito.verify(productRepository, Mockito.times(1)).deleteById(dependentId);
    }

    @Test
    public void findAllPagedShouldReturnPage(){
        Pageable pageable = PageRequest.of(0,10);
        Page<ProductDTO> result = productService.findPaged(pageable, null);

        Assertions.assertNotNull(result);
        Mockito.verify(productRepository).findAll(pageable); //Verificando se o método é executado apenas uma vez
    }

    @Test
    public void findByIdShouldReturnProductDtoWhenIdExistes(){
        ProductDTO result = productService.findById(exintingId);
        Assertions.assertNotNull(result);

        Mockito.verify(productRepository).findById(exintingId);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            ProductDTO productDTO = productService.findById(notExistingId);
        });

        Mockito.verify(productRepository).findById(notExistingId);
    }

    @Test
    public void updateShouldReturnProductDtoWhenIdExists(){
        ProductDTO dto = productService.update(exintingId, Factory.getProductDTO());
        Assertions.assertNotNull(dto);

    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class, () ->{
            ProductDTO dto = productService.update(notExistingId, Factory.getProductDTO());
        });

    }
}
