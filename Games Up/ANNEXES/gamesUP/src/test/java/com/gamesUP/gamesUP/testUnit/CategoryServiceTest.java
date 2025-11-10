package com.gamesUP.gamesUP.testUnit;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Optional;
import com.gamesUP.gamesUP.dto.CategoryDTO;
import com.gamesUP.gamesUP.entity.Category;
import com.gamesUP.gamesUP.repository.CategoryRepository;
import com.gamesUP.gamesUP.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void shouldCreateCategorySuccess() {
        CategoryDTO dto = CategoryDTO.builder()
                .type("Action")
                .build();

        when(categoryRepository.findByType("Action")).thenReturn(Optional.empty());

        Category saved = Category.builder()
                .id(1L)
                .type("Action")
                .build();
        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        CategoryDTO result = categoryService.create(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Action", result.getType());
        verify(categoryRepository).findByType("Action");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void shouldThrowWhenTypeExistsOnCreate() {
        CategoryDTO dto = CategoryDTO.builder()
                .type("RPG")
                .build();

        Category existing = Category.builder().id(2L).type("RPG").build();
        when(categoryRepository.findByType("RPG")).thenReturn(Optional.of(existing));

        assertThrows(RuntimeException.class, () -> categoryService.create(dto));
        verify(categoryRepository).findByType("RPG");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void shouldUpdateCategorySuccess() {
        Long id = 3L;
        Category existing = Category.builder().id(id).type("Old").build();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.findByType("New")).thenReturn(Optional.empty());

        Category saved = Category.builder().id(id).type("New").build();
        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        CategoryDTO dto = CategoryDTO.builder().type("New").build();

        CategoryDTO result = categoryService.update(id, dto);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("New", result.getType());
        verify(categoryRepository).findById(id);
        verify(categoryRepository).findByType("New");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void shouldThrowWhenTypeExistsOnUpdate() {
        Long id = 4L;
        Category existing = Category.builder().id(id).type("Some").build();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));

        Category other = Category.builder().id(99L).type("Duplicate").build();
        when(categoryRepository.findByType("Duplicate")).thenReturn(Optional.of(other));

        CategoryDTO dto = CategoryDTO.builder().type("Duplicate").build();

        assertThrows(RuntimeException.class, () -> categoryService.update(id, dto));
        verify(categoryRepository).findById(id);
        verify(categoryRepository).findByType("Duplicate");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void shouldDeleteCategorySuccess() {
        Long id = 5L;
        when(categoryRepository.existsById(id)).thenReturn(true);

        categoryService.delete(id);

        verify(categoryRepository).existsById(id);
        verify(categoryRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeleteNotFound() {
        Long id = 6L;
        when(categoryRepository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> categoryService.delete(id));
        verify(categoryRepository).existsById(id);
        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldFindByIdSuccess() {
        Long id = 7L;
        Category c = Category.builder().id(id).type("Puzzle").build();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(c));

        CategoryDTO dto = categoryService.findById(id);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("Puzzle", dto.getType());
        verify(categoryRepository).findById(id);
    }

    @Test
    void shouldThrowWhenFindByIdNotFound() {
        Long id = 8L;
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.findById(id));
        verify(categoryRepository).findById(id);
    }

    @Test
    void shouldFindAll() {
        Category c1 = Category.builder().id(1L).type("A").build();
        Category c2 = Category.builder().id(2L).type("B").build();
        when(categoryRepository.findAll()).thenReturn(List.of(c1, c2));

        var list = categoryService.findAll();

        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals("A", list.get(0).getType());
        assertEquals(2L, list.get(1).getId());
        assertEquals("B", list.get(1).getType());
        verify(categoryRepository).findAll();
    }
}
