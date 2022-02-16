package com.fernandesDev.dscatalog.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fernandesDev.dscatalog.dto.ProductDTO;
import com.fernandesDev.dscatalog.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIT {

    @Autowired
    private ProductResource resource;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long existingId;
    private Long nonExistId;
    private Long countTotalProducts;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistId = 1000L;
        countTotalProducts = 25L; //Total de produtos no seed do BD
    }

    @Test
    public void findAllPagedShouldReturnPageSortedByName() throws Exception {

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/products?page=0&size=10&sort=name,asc")
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
        result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
        result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {

        ProductDTO productDTO = Factory.getProductDTO();

        String name = productDTO.getName();
        String description = productDTO.getDescription();

        String jsonBody = objectMapper.writeValueAsString(productDTO); //Convertendo um objeto JAVA em JSON

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", existingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)); //Para garantir que está retornando um JSON);

        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(jsonPath("$.id").value(existingId));
        result.andExpect(jsonPath("$.name").value(name));
        result.andExpect(jsonPath("$.description").value(description));

    }

    @Test
    public void updateShouldReturnNotFoundWhenDoesNotIdExist() throws Exception {

        ProductDTO productDTO = Factory.getProductDTO();

        String jsonBody = objectMapper.writeValueAsString(productDTO); //Convertendo um objeto JAVA em JSON

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", nonExistId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)); //Para garantir que está retornando um JSON);

        result.andExpect(MockMvcResultMatchers.status().isNotFound());

    }
}
