package com.microservice_level_up.module.category;

import com.microservice_level_up.error.duplicated_category.DuplicatedCategoryException;
import com.microservice_level_up.module.category.dto.CategoryRegistrationRequest;
import com.microservice_level_up.module.category.dto.CategoryResponse;
import com.microservice_level_up.module.category.dto.UpdateCategoryRequest;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public record CategoryService(
        CategoryRepository repository
) implements ICategoryService {

    @Override
    public Category getById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    @Override
    public List<CategoryResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(this::mapResponse)
                .toList();
    }

    @Override
    public long addCategory(CategoryRegistrationRequest category) {
        validateDuplicatedName(category.name());
        Category newCategory = Category.builder()
                .name(category.name())
                .description(category.description())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return repository.save(newCategory).getId();
    }

    @Override
    public long updateCategory(UpdateCategoryRequest categoryToUpdate) {
        Category category = getById(categoryToUpdate.id());
        validateDuplicatedName(categoryToUpdate.name());

        category.setName(categoryToUpdate.name());
        category.setDescription(categoryToUpdate.description());
        category.setUpdatedAt(LocalDateTime.now());

        return repository.save(category).getId();
    }

    private void validateDuplicatedName(String name) {
        if (repository.existsByName(name))
            throw new DuplicatedCategoryException("Duplicated name: " + name);
    }

    private CategoryResponse mapResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .description(category.getDescription())
                .name(category.getName())
                .build();
    }
}
