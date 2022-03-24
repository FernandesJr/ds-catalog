package com.fernandesDev.dscatalog.repositories;


import com.fernandesDev.dscatalog.entities.Category;
import com.fernandesDev.dscatalog.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    //Tem que apelidar o inner join e o IN verifica se a categoria contém na lista do relacionamento
    @Query("SELECT DISTINCT obj FROM Product obj " +
            "INNER JOIN obj.categories cats " +
            "WHERE (:category IS NULL OR :category IN cats) " +
            "AND LOWER(obj.name) LIKE LOWER(CONCAT('%',:name,'%'))") //Se vier null a consulta já não verifica o IN Desta forma retorna todos os produtos
    Page<Product> findByCategoryOrName(Category category, Pageable pageable, String name);
}
