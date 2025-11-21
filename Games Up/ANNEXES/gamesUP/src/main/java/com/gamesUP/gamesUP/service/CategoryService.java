package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.CategoryDTO;
import com.gamesUP.gamesUP.entity.Category;
import com.gamesUP.gamesUP.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryDTO create(CategoryDTO dto) {
        categoryRepository.findByType(dto.getType()).ifPresent(c ->
        { throw new RuntimeException("Category already exists with type: " + dto.getType()); });
        Category saved = categoryRepository.save(toEntity(dto));
        return toDto(saved);
    }

    public CategoryDTO update(Long id, CategoryDTO dto) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
        if (!existing.getType().equals(dto.getType())) {
            categoryRepository.findByType(dto.getType()).ifPresent(c ->
            { throw new RuntimeException("Category already exists with type: " + dto.getType()); });
        }
        existing.setType(dto.getType());
        Category saved = categoryRepository.save(existing);
        return toDto(saved);
    }

    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }

    public CategoryDTO findById(Long id) {
        Category c = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
        return toDto(c);
    }

    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private Category toEntity(CategoryDTO dto) {
        return Category.builder()
                .id(dto.getId())
                .type(dto.getType())
                .build();
    }

    private CategoryDTO toDto(Category c) {
        return CategoryDTO.builder()
                .id(c.getId())
                .type(c.getType())
                .build();
    }
}