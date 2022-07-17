package me.gnomeshell.dscatalog.services;

import me.gnomeshell.dscatalog.dto.CategoryDTO;
import me.gnomeshell.dscatalog.dto.ProductDTO;
import me.gnomeshell.dscatalog.entities.Category;
import me.gnomeshell.dscatalog.entities.Product;
import me.gnomeshell.dscatalog.repositories.CategoryRepository;
import me.gnomeshell.dscatalog.repositories.ProductRepository;
import me.gnomeshell.dscatalog.services.exceptions.DatabaseException;
import me.gnomeshell.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;


    @Autowired
    private CategoryRepository categoryRepository;

    // garante a integridade da transação e não deixa lockar o banco e melhorar a performance

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(PageRequest pageRequest){
        Page<Product> list = repository.findAll(pageRequest);
        return list.map(x -> new ProductDTO(x));
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id){
        Optional<Product> obj = repository.findById(id);
        Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO categoryDTO) {
        Product entity = new Product();
        copyDtoToEntity(categoryDTO, entity);
        entity = repository.save(entity);
        return new ProductDTO(entity);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO categoryDTO) {
       try {
           Product entity = repository.getById(id);
           copyDtoToEntity(categoryDTO, entity);
           entity = repository.save(entity);
           return new ProductDTO(entity);
       } catch (EntityNotFoundException e){
           throw new ResourceNotFoundException("Id not found" + id);
       }
    }


    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e){
            throw new ResourceNotFoundException("Id not found");
        } catch (DataIntegrityViolationException e){
            throw new DatabaseException("Integrity Violation");
        }
    }

    public void copyDtoToEntity(ProductDTO productDTO, Product entity){
        entity.setName(productDTO.getName());
        entity.setDescription(productDTO.getDescription());
        entity.setDate(productDTO.getDate());
        entity.setImgUrl(productDTO.getImgUrl());
        entity.setPrice(productDTO.getPrice());

        entity.getCategories().clear();
        for (CategoryDTO catDto: productDTO.getCategories()) {
            Category category = categoryRepository.getOne(catDto.getId());
            entity.getCategories().add(category);
        }

    }
}