package com.fernandesDev.dscatalog.tests;

import com.fernandesDev.dscatalog.dto.ProductDTO;
import com.fernandesDev.dscatalog.entities.Category;
import com.fernandesDev.dscatalog.entities.Product;

import java.time.Instant;

public class Factory {

    public static Product creatProduct(){
        Product product = new Product(
                1L, "Phone", "Good phone",
                750.56, "http://img.com.br", Instant.now());
        product.getCategories().add(new Category(2L, "Eletrônicos"));
        return product;
    }

    public static ProductDTO getProductDTO(){
        return new ProductDTO(creatProduct());
    }
}
