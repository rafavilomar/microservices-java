package com.microservice_level_up.module.category;

import com.microservice_level_up.error.duplicated_user_email.DuplicatedCategoryException;
import com.microservice_level_up.module.category.dto.CategoryRegistrationRequest;
import com.microservice_level_up.module.category.dto.CategoryResponse;
import com.microservice_level_up.module.category.dto.UpdateCategoryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @InjectMocks
    private CategoryService underTest;

    @Mock
    private CategoryRepository repository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getById_ShouldBeOk() {
        long id = 1L;
        Category category = getCategory(id);

        when(repository.findById(id)).thenReturn(Optional.of(category));

        Category actualResponse = underTest.getById(id);

        assertEquals(category, actualResponse);

        verify(repository, times(1)).findById(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getById_NotFound() {
        long id = 1L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> underTest.getById(id));

        assertEquals("Category not found", exception.getMessage());

        verify(repository, times(1)).findById(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findAll() {
        List<Category> categories = List.of(getCategory(1L));

        when(repository.findAll()).thenReturn(categories);

        List<CategoryResponse> actualResponse = underTest.findAll();

        assertNotNull(actualResponse);
        assertAll(
                "Find all categories",
                () -> assertEquals(1, actualResponse.size()),
                () -> assertEquals(categories.get(0).getId(), actualResponse.get(0).id()),
                () -> assertEquals(categories.get(0).getName(), actualResponse.get(0).name()),
                () -> assertEquals(categories.get(0).getDescription(), actualResponse.get(0).description())
        );
        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void addCategory_ShouldBeOk() {
        long id = 1L;
        CategoryRegistrationRequest category = new CategoryRegistrationRequest("Category", "Description");

        when(repository.save(any(Category.class))).thenReturn(getCategory(id));

        long actualResponse = underTest.addCategory(category);

        assertEquals(id, actualResponse);

        verify(repository, times(1)).existsByName(category.name());
        verify(repository, times(1)).save(any(Category.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void addCategory_DuplicatedName() {
        CategoryRegistrationRequest category = new CategoryRegistrationRequest("Category", "Description");

        when(repository.existsByName(category.name())).thenReturn(true);

        DuplicatedCategoryException exception = assertThrows(DuplicatedCategoryException.class,
                () -> underTest.addCategory(category));

        assertEquals("Duplicated name: " + category.name(), exception.getMessage());

        verify(repository, times(1)).existsByName(category.name());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void updateCategory_ShouldBeOk() {
        long id = 1L;
        UpdateCategoryRequest categoryToUpdate = new UpdateCategoryRequest(id, "Category", "Description");

        Category category = getCategory(id);
        when(repository.findById(id)).thenReturn(Optional.of(category));

        category.setName(categoryToUpdate.name());
        category.setDescription(categoryToUpdate.description());
        when(repository.save(category)).thenReturn(category);

        long actualResponse = underTest.updateCategory(categoryToUpdate);

        assertEquals(id, actualResponse);

        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).existsByName(categoryToUpdate.name());
        verify(repository, times(1)).save(category);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void updateCategory_NotFound() {
        long id = 1L;
        UpdateCategoryRequest categoryToUpdate = new UpdateCategoryRequest(id, "Category", "Description");

        when(repository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> underTest.updateCategory(categoryToUpdate));

        assertEquals("Category not found", exception.getMessage());

        verify(repository, times(1)).findById(id);
        verifyNoMoreInteractions(repository);
    }

    private Category getCategory(long id) {
        return Category.builder()
                .id(id)
                .name("Category")
                .description("Description")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}