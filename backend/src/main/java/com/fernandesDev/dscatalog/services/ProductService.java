package com.fernandesDev.dscatalog.services;

import com.fernandesDev.dscatalog.dto.CategoryDTO;
import com.fernandesDev.dscatalog.dto.ProductDTO;
import com.fernandesDev.dscatalog.entities.Category;
import com.fernandesDev.dscatalog.entities.Product;
import com.fernandesDev.dscatalog.repositories.CategoryRepository;
import com.fernandesDev.dscatalog.repositories.ProductRepository;
import com.fernandesDev.dscatalog.services.exceptions.DataBaseException;
import com.fernandesDev.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findPaged(Pageable pageable, Long idCat, String name){
        Category category = (idCat == 0) ? null : categoryRepository.getById(idCat); //Se vier 0 retornar todos independente da categoria
        return repository.findByCategoryOrName(category, pageable, name).map(p -> new ProductDTO(p, p.getCategories()));
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product product = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new ProductDTO(product, product.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product product = new Product();
        CopyDtoToEntity(dto, product);
        product = repository.save(product);
        return new ProductDTO(product, product.getCategories());
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product product = repository.getById(id); //Não acessa ao database, retorna apenas uma instância com id
            CopyDtoToEntity(dto, product);
            product = repository.save(product); //Por causa do vinculo do getById mesmo sendo um id inexistente ele não salva na bd
            return new ProductDTO(product);
        } catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Entity not find by id "+id);
        }
    }

    public void delete(Long id) {
        try{
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e){
            //Caso o id não tenha na bd
            throw new ResourceNotFoundException("Entity not found by id "+id);
        } catch (DataIntegrityViolationException d){
            //Caso tenho algum produto vinculado a Product não pode ser excluída
            throw new DataBaseException("Integrity violation");
        }
    }

    private void CopyDtoToEntity(ProductDTO dto, Product product) {
        //Em estado de percistência
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setImgUrl(dto.getImgUrl());
        product.setPrice(dto.getPrice());
        product.setDate(dto.getDate());
        product.getCategories().clear();
        for(CategoryDTO ctDto : dto.getCategories()){
            product.getCategories().add(categoryRepository.getById(ctDto.getId())); //Uma referência ao bd *geyby*
        }
    }
}
