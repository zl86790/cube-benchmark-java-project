package com.ecshop.service;

import com.ecshop.dto.SearchQueryDTO;
import com.ecshop.dto.SearchResultDTO;
import com.ecshop.model.Product;
import com.ecshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {
    private final ProductRepository productRepository;
    private final ProductService productService;

    public SearchResultDTO search(SearchQueryDTO query) {
        List<Product> candidates;
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            candidates = productRepository.searchByKeyword(query.getKeyword());
        } else if (query.getCategoryId() != null) {
            candidates = productRepository.findByCategoryId(query.getCategoryId());
        } else {
            candidates = productRepository.findByIsActiveTrue();
        }

        List<Product> filtered = candidates.stream()
                .filter(p -> query.getMinPrice() == null || p.getPrice().compareTo(query.getMinPrice()) >= 0)
                .filter(p -> query.getMaxPrice() == null || p.getPrice().compareTo(query.getMaxPrice()) <= 0)
                .collect(Collectors.toList());

        if ("price_asc".equals(query.getSortBy())) {
            filtered.sort(Comparator.comparing(Product::getPrice, BigDecimal.ZERO));
        } else if ("price_desc".equals(query.getSortBy())) {
            filtered.sort(Comparator.comparing(Product::getPrice).reversed());
        }

        int fromIndex = Math.min(query.getPage() * query.getSize(), filtered.size());
        int toIndex = Math.min(fromIndex + query.getSize(), filtered.size());
        List<Product> pageContent = filtered.subList(fromIndex, toIndex);

        SearchResultDTO result = new SearchResultDTO();
        result.setProducts(productService.toDTOList(pageContent));
        result.setTotalResults(filtered.size());
        result.setPage(query.getPage());
        result.setSize(query.getSize());
        return result;
    }
}
