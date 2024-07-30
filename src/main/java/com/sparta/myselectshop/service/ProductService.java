package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public static final int MIN_MY_PRICE = 100;


    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        // Entity 객체로 만든다.

        //Spring Data JPA - save (저장하면서 반환)
        Product product = productRepository.save(new Product(requestDto));

        //product의 데이터를 dto로 변환하기위해서 생성자로 보낸다.
        return new ProductResponseDto(product);
    }

    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductMypriceRequestDto requestDto) {
        int myprice = requestDto.getMyprice();
        if (myprice < MIN_MY_PRICE) {
            throw new IllegalArgumentException("유효하지 않은 관심 가격입니다. 최소 " + MIN_MY_PRICE + "원 이상으로 설정해 주세요.");
        }

        Product product = productRepository.findById(id).orElseThrow(
                () -> new NullPointerException("해당 상품을 찾을 수 없습니다.")
        );

        product.update(requestDto);

        //product의 데이터를 dto로 변환하기위해서 생성자로 보낸다.
        return new ProductResponseDto(product);
    }
}
