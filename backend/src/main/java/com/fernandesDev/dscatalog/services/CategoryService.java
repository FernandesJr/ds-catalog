package com.fernandesDev.dscatalog.services;

import com.fernandesDev.dscatalog.entities.Category;
import com.fernandesDev.dscatalog.repositories.CategoryRepository;
import com.fernandesDev.dscatalog.dto.CategoryDTO;
import com.fernandesDev.dscatalog.services.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    public List<CategoryDTO> findAll(){
        return repository.findAll().stream().map(c -> new CategoryDTO(c)).collect(Collectors.toList());
    }

    public CategoryDTO findById(Long id) {
        Category category = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Entity not found"));
        return new CategoryDTO(category);
    }
}
