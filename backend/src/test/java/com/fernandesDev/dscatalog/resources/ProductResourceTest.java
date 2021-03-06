package com.fernandesDev.dscatalog.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fernandesDev.dscatalog.dto.ProductDTO;
import com.fernandesDev.dscatalog.services.ProductService;
import com.fernandesDev.dscatalog.services.exceptions.DataBaseException;
import com.fernandesDev.dscatalog.services.exceptions.ResourceNotFoundException;
import com.fernandesDev.dscatalog.tests.Factory;
import com.fernandesDev.dscatalog.tests.TokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductResourceTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    @Autowired
    private TokenUtil tokenUtil;

    private ProductDTO productDTO;
    private PageImpl<ProductDTO> page;

    private long existingId;
    private long nonExistingId;
    private long dependentId;

    private String username;
    private String password;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        productDTO = Factory.getProductDTO();
        page = new PageImpl<>(List.of(productDTO));

        username = "maria@gmail.com";
        password = "123456";

        when(service.findPaged(any(), any(), any())).thenReturn(page);
        when(service.findById(existingId)).thenReturn(productDTO);
        when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class); //Por??m em ResourceException ?? tratado para retorna um NotFound

        when(service.update(eq(existingId), any())).thenReturn(productDTO);
        when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class); //'quando for isso, fa??a isso'

        //Comportamentos do delete
        doNothing().when(service).delete(existingId); //Lembrando que quando m??todo ?? void inverte a a????o, 'n??o fa??a nada quando'
        doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
        doThrow(DataBaseException.class).when(service).delete(dependentId);

        when(service.insert(any())).thenReturn(productDTO);

    }

    @Test
    public void findpagedShouldReturnPage() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/products")
                .accept(MediaType.APPLICATION_JSON)); //Para garantir que est?? retornando um JSON

        result.andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExist() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON)); //Para garantir que est?? retornando um JSON);
        result.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void findByIdShouldReturnNotFOUNDWhenIdDoesNotExist() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON)); //Para garantir que est?? retornando um JSON);
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {

        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);

        String jsonBody = objectMapper.writeValueAsString(productDTO); //Convertendo um objeto JAVA em JSON

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)); //Para garantir que est?? retornando um JSON);

        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());

    }

    @Test
    public void updateShouldReturnNotFOUNDWhenIdDoesNotExist() throws Exception {

        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);

        String jsonBody = objectMapper.writeValueAsString(productDTO); //Convertendo um objeto JAVA em JSON

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", nonExistingId)
                .header("Authorization", "Bearer " + accessToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)); //Para garantir que est?? retornando um JSON);
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void insertShouldReturnCreatedAndProductDTO() throws Exception{

        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);

        String jsonBody = objectMapper.writeValueAsString(productDTO); //Convertendo um objeto JAVA em JSON

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/products")
                .header("Authorization", "Bearer " + accessToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void deleteShouldNoContentWhenIdExists()throws Exception{

        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", existingId)
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deleteShouldNotFoundWhenIdDoesNotExist()throws Exception{

        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", nonExistingId)
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteShouldBadRequestWhenDependentId()throws Exception{

        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", dependentId)
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}
