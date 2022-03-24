package com.fernandesDev.dscatalog.resources;

import com.fernandesDev.dscatalog.dto.ProductDTO;
import com.fernandesDev.dscatalog.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RequestMapping(value = "/products")
@RestController
public class ProductResource {

    @Autowired
    private ProductService service;

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> findpaged(Pageable pageable,
                                                      @RequestParam(value = "idcat", defaultValue = "0") Long idCat,
                                                      @RequestParam(value = "name", defaultValue = "") String name){
        Page<ProductDTO> list = service.findPaged(pageable, idCat, name.trim()); //O trim retira os espaços em branco do início e fim
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id){
        ProductDTO dto = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> insert(@Valid @RequestBody ProductDTO dto){
        dto = service.insert(dto);
        //Quando se cria um novo recurso deve-se devolver um status 201
        //E no head da response por convenção declara o location do recurso criado
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id,@Valid @RequestBody ProductDTO dto){
        dto = service.update(id, dto);
        return ResponseEntity.ok().body(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build(); //Vazio, com status 204
    }
}
