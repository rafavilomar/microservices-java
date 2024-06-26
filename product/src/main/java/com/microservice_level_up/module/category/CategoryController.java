package com.microservice_level_up.module.category;

import com.microservice_level_up.module.category.dto.CategoryRegistrationRequest;
import com.microservice_level_up.dto.CategoryResponse;
import com.microservice_level_up.module.category.dto.UpdateCategoryRequest;
import com.microservice_level_up.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Product Category")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryController {

    private final ICategoryService service;

    @GetMapping
    @PreAuthorize("hasAuthority('GET_CATEGORY')")
    @Operation(summary = "Get all categories")
    public ResponseEntity<BaseResponse<List<CategoryResponse>>> findAll() {
        log.info("Get all categories");
        List<CategoryResponse> payload = service.findAll();

        BaseResponse<List<CategoryResponse>> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.OK,
                "Categories found"
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_CATEGORY')")
    @Operation(summary = "Create a new category")
    public ResponseEntity<BaseResponse<Long>> addCategory(@RequestBody @Valid CategoryRegistrationRequest request) {
        log.info("Add new category {}", request);
        long payload = service.addCategory(request);

        BaseResponse<Long> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.CREATED,
                "Category created"
        );
    }

    @PutMapping
    @PreAuthorize("hasAuthority('UPDATE_CATEGORY')")
    @Operation(summary = "Update an existing category")
    public ResponseEntity<BaseResponse<Long>> updateCategory(@RequestBody UpdateCategoryRequest request) {
        log.info("Add new category {}", request);
        long payload = service.updateCategory(request);

        BaseResponse<Long> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.OK,
                "Category updated"
        );
    }
}
