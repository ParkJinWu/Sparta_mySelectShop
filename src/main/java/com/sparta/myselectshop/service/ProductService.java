package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;


    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        // Entity 객체로 만든다.

        //Spring Data JPA - save (저장하면서 반환)
        Product product = productRepository.save(new Product(requestDto));

        //product의 데이터를 dto로 변환하기위해서 생성자로 보낸다.
        return new ProductResponseDto(product);
    }
}
