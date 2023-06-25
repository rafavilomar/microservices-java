package com.microservice_level_up.module.category;

import com.microservice_level_up.module.category.dto.CategoryRegistrationRequest;
import com.microservice_level_up.module.category.dto.CategoryResponse;
import com.microservice_level_up.module.category.dto.UpdateCategoryRequest;

import java.util.List;

public interface ICategoryService {
    Category getById(long id);

    List<CategoryResponse> findAll();

    long addCategory(CategoryRegistrationRequest category);

    long updateCategory(UpdateCategoryRequest categoryToUpdate);
}
