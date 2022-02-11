package com.fernandesDev.dscatalog.services;

import com.fernandesDev.dscatalog.repositories.CategoryRepository;
import com.fernandesDev.dscatalog.repositories.ProductRepository;
import com.fernandesDev.dscatalog.services.exceptions.DataBaseException;
import com.fernandesDev.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

    @BeforeEach
    void setUp() {
        exintingId = 1L;
        notExistingId = 1000L;
        dependentId = 4L;

        //Preparando os objetos Mokados, preparando os comportamentos do que deve acontecer
        Mockito.doNothing().when(productRepository).deleteById(exintingId);

        Mockito.doThrow(ResourceNotFoundException.class).when(productRepository).deleteById(notExistingId);

        Mockito.doThrow(DataBaseException.class).when(productRepository).deleteById(dependentId);
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
}
