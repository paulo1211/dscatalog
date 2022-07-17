package me.gnomeshell.dscatalog.services;

import me.gnomeshell.dscatalog.dto.CategoryDTO;
import me.gnomeshell.dscatalog.entities.Category;
import me.gnomeshell.dscatalog.repositories.CategoryRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // garante a integridade da transação e não deixa lockar o banco e melhorar a performance

    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAllPaged(PageRequest pageRequest){
        Page<Category> list = categoryRepository.findAll(pageRequest);
        return list.map(x -> new CategoryDTO(x));
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id){
        Optional<Category> obj = categoryRepository.findById(id);
        Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO categoryDTO) {
        Category entity = new Category();
        entity.setName(categoryDTO.getName());
        entity = categoryRepository.save(entity);
        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO categoryDTO) {
       try {
           Category entity = categoryRepository.getById(id);
           entity.setName(categoryDTO.getName());
           entity = categoryRepository.save(entity);
           return new CategoryDTO(entity);
       } catch (EntityNotFoundException e){
           throw new ResourceNotFoundException("Id not found" + id);
       }
    }


    public void delete(Long id) {
        try {
            categoryRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e){
            throw new ResourceNotFoundException("Id not found");
        } catch (DataIntegrityViolationException e){
            throw new DatabaseException("Integrity Violation");
        }


    }


}
