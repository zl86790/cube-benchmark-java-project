package com.ecshop.service;

import com.ecshop.exception.BusinessException;
import com.ecshop.model.Product;
import com.ecshop.model.ProductAnswer;
import com.ecshop.model.ProductQuestion;
import com.ecshop.model.User;
import com.ecshop.repository.ProductAnswerRepository;
import com.ecshop.repository.ProductQuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductQAService {

    private final ProductQuestionRepository productQuestionRepository;
    private final ProductAnswerRepository productAnswerRepository;
    private final ProductService productService;

    @Transactional
    public ProductQuestion askQuestion(Long productId, Long userId, String question) {
        Product product = productService.getProduct(productId);

        ProductQuestion productQuestion = new ProductQuestion();
        productQuestion.setProduct(product);
        User user = new User();
        user.setId(userId);
        productQuestion.setUser(user);
        productQuestion.setQuestion(question);

        return productQuestionRepository.save(productQuestion);
    }

    @Transactional
    public ProductAnswer answerQuestion(Long questionId, Long userId, String answerText) {
        ProductQuestion question = productQuestionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException("Question not found: " + questionId));

        ProductAnswer answer = new ProductAnswer();
        answer.setQuestion(question);
        User user = new User();
        user.setId(userId);
        answer.setUser(user);
        answer.setAnswer(answerText);

        return productAnswerRepository.save(answer);
    }

    public List<ProductQuestion> getQuestionsByProduct(Long productId) {
        return productQuestionRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }
}
