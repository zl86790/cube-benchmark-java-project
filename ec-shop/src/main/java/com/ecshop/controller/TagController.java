package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.model.Tag;
import com.ecshop.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @GetMapping
    public ApiResponse<List<Tag>> getAllTags() {
        return ApiResponse.success(tagService.getAllTags());
    }

    @PostMapping("/product/{productId}")
    public ApiResponse<Void> tagProduct(@PathVariable Long productId, @RequestParam String tagName) {
        tagService.tagProduct(productId, tagName);
        return ApiResponse.success("Product tagged", null);
    }

    @GetMapping("/product/{productId}")
    public ApiResponse<List<Tag>> getProductTags(@PathVariable Long productId) {
        return ApiResponse.success(tagService.getTagsForProduct(productId));
    }
}
