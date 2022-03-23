package com.fernandesDev.dscatalog.repositories;


import com.fernandesDev.dscatalog.entities.Category;
import com.fernandesDev.dscatalog.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    //Tem que apelidar o inner join e o IN verifica se a categoria contém na lista do relacionamento
    @Query("SELECT obj FROM Product obj " +
            "INNER JOIN obj.categories cats " +
            "WHERE :category IN cats")
    Page<Product> findByCategory(Category category, Pageable pageable);
}
