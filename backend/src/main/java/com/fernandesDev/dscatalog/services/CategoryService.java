package com.fernandesDev.dscatalog.services;

import com.fernandesDev.dscatalog.entities.Category;
import com.fernandesDev.dscatalog.repositories.CategoryRepository;
import com.fernandesDev.dscatalog.dto.CategoryDTO;
import com.fernandesDev.dscatalog.services.exceptions.DataBaseException;
import com.fernandesDev.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public Page<CategoryDTO> findPaged(PageRequest pageRequest){
        return repository.findAll(pageRequest).map(c -> new CategoryDTO(c));
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Category category = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new CategoryDTO(category);
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category = repository.save(category);
        return new CategoryDTO(category);
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {
        try {
            Category category = repository.getById(id); //Não acessa ao database, retorna apenas uma instância com id
            category.setName(dto.getName());
            category = repository.save(category); //Por causa do vinculo do getById mesmo sendo um id inexistente ele não salva na bd
            return new CategoryDTO(category);
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
            //Caso tenho algum produto vinculado a category não pode ser excluída
            throw new DataBaseException("Integrity violation");
        }
    }
}
