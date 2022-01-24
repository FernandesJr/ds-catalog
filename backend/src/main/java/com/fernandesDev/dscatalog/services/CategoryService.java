package com.fernandesDev.dscatalog.services;

import com.fernandesDev.dscatalog.CategoryRepository;
import com.fernandesDev.dscatalog.dto.CategoryDTO;
import com.fernandesDev.dscatalog.entities.Category;
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
}
