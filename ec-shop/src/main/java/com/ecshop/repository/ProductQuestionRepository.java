package com.ecshop.repository;

import com.ecshop.model.ProductQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductQuestionRepository extends JpaRepository<ProductQuestion, Long> {
    List<ProductQuestion> findByProductIdOrderByCreatedAtDesc(Long productId);
}
