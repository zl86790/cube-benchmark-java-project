package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.dto.SearchQueryDTO;
import com.ecshop.dto.SearchResultDTO;
import com.ecshop.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/products")
    public ApiResponse<SearchResultDTO> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        SearchQueryDTO query = new SearchQueryDTO(keyword, categoryId, minPrice, maxPrice, sortBy, page, size);
        return ApiResponse.success(searchService.search(query));
    }
}
