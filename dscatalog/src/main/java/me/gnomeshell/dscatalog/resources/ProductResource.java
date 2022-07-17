package me.gnomeshell.dscatalog.resources;

import me.gnomeshell.dscatalog.dto.ProductDTO;
import me.gnomeshell.dscatalog.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/products")
public class ProductResource {

    @Autowired
    private ProductService service;

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction) {
        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
        Page<ProductDTO> list = service.findAllPaged(pageRequest);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
        ProductDTO dto = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }


    @PostMapping
    public ResponseEntity<ProductDTO> insert(@RequestBody ProductDTO categoryDTO) {
        categoryDTO = service.insert(categoryDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(categoryDTO.getId())
                .toUri();
        return ResponseEntity.created(uri).body(categoryDTO);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id, @RequestBody ProductDTO categoryDTO) {
        categoryDTO = service.update(id, categoryDTO);
        return ResponseEntity.ok().body(categoryDTO);
    }


    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }



}
