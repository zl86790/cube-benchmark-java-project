package com.ecshop.repository;

import com.ecshop.model.ProductAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductAnswerRepository extends JpaRepository<ProductAnswer, Long> {

    List<ProductAnswer> findByQuestionId(Long questionId);
}
