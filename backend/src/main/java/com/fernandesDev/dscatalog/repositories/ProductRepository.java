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
    @Query("SELECT DISTINCT obj FROM Product obj " +
            "INNER JOIN obj.categories cats " +
            "WHERE (COALESCE(:categories) IS NULL OR cats IN :categories ) " + //Se vier null a consulta já não verifica o IN Desta forma retorna todos os produtos
            "AND LOWER(obj.name) LIKE LOWER(CONCAT('%',:name,'%'))")
    Page<Product> findByCategoryOrName(List<Category> categories, Pageable pageable, String name);


    //Resolvendo o problema N+1 consultas no método findByCategoryOrName o Hibernate faz n consultas para cada vez que
    //ele precisa de uma categoria, isso afeta a performace do sistema
    //Ao usar o INNER FETCH ele busca todas as categorias de uma vez e armazena na memória
    //Sendo assim ao precisar de alguma categoria o JPA é inteligente suficiente para não ir buscar no banco novamente
    @Query("SELECT obj FROM Product obj JOIN FETCH obj.categories WHERE obj IN :products")
    List<Product> findProductWithCategory(List<Product> products);
}
