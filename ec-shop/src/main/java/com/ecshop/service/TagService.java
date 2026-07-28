package com.ecshop.service;

import com.ecshop.model.Product;
import com.ecshop.model.ProductTag;
import com.ecshop.model.Tag;
import com.ecshop.repository.ProductTagRepository;
import com.ecshop.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagService {
    private final TagRepository tagRepository;
    private final ProductTagRepository productTagRepository;
    private final ProductService productService;

    public Tag getOrCreateTag(String name) {
        return tagRepository.findByName(name);
    }

    @Transactional
    public void tagProduct(Long productId, String tagName) {
        Product product = productService.getProduct(productId);
        Tag tag = getOrCreateTag(tagName);

        ProductTag productTag = new ProductTag();
        productTag.setProduct(product);
        productTag.setTag(tag);
        productTagRepository.save(productTag);
    }

    public List<Tag> getTagsForProduct(Long productId) {
        return productTagRepository.findByProductId(productId).stream()
                .map(ProductTag::getTag)
                .collect(Collectors.toList());
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }
}
