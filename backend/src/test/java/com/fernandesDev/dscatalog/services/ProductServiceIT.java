package com.fernandesDev.dscatalog.services;

import com.fernandesDev.dscatalog.dto.ProductDTO;
import com.fernandesDev.dscatalog.repositories.ProductRepository;
import com.fernandesDev.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest //Carrega o contesto da aplicação, de fato altera no DataBase
@Transactional //Para que aconteça o rollBack do DataBase, garantindo assim que nenhum método interfira no outro
public class ProductServiceIT {

    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repository;

    private Long idExisting;
    private Long nonExistId;
    private Long countTotalProducts;

    @BeforeEach
    void setUp() {
        idExisting = 1L;
        nonExistId = 1000L;
        countTotalProducts = 25L; //Total de produtos no seed do BD
    }

    @Test
    public void deleteShouldDeleteWhenIdExists(){

        service.delete(idExisting);

        Assertions.assertEquals(countTotalProducts - 1, repository.count());
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistId);
        });
    }

    @Test
    public void findAllPagedShouldReturnPageZeroWithTenProducts(){

        PageRequest request = PageRequest.of(0, 10);

        Page<ProductDTO> page = service.findPaged(request,0L, "");

        Assertions.assertFalse(page.isEmpty());
        Assertions.assertEquals(10, page.getSize());
        Assertions.assertEquals(25, page.getTotalElements());
        Assertions.assertEquals(0, page.getNumber());

    }

    @Test
    public void findAllPagedShouldReturnPageEmpty(){

        PageRequest request = PageRequest.of(50, 10);

        Page<ProductDTO> page = service.findPaged(request,0L, "");

        Assertions.assertTrue(page.isEmpty());

    }

    @Test
    public void findAllPagedShouldReturnSortesPageSortByName(){

        PageRequest request = PageRequest.of(0, 10, Sort.by("name"));

        Page<ProductDTO> page = service.findPaged(request,0L, "");

        Assertions.assertEquals("Macbook Pro", page.getContent().get(0).getName());
        Assertions.assertEquals("PC Gamer", page.getContent().get(1).getName());
        Assertions.assertEquals("PC Gamer Alfa", page.getContent().get(2).getName());

    }
}
