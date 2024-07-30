package com.sparta.myselectshop.controller;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.security.UserDetailsImpl;
import com.sparta.myselectshop.service.ProductService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;


    @PostMapping("/products")
    public ProductResponseDto createProduct(@RequestBody ProductRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return productService.createProduct(requestDto,userDetails.getUser());
    }

    @PutMapping("/products/{id}")
    public ProductResponseDto updateProduct(@PathVariable Long id, @RequestBody ProductMypriceRequestDto requestDto ) {
        return productService.updateProduct(id,requestDto);
    }

    @GetMapping("/products")
    public Page<ProductResponseDto> getProducts(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return productService.getProducts(userDetails.getUser(),
                //페이지 정보
                page - 1, size, sortBy, isAsc);
    }

    // 관심상품에 폴더를 추가
    @PostMapping("/products/{productId}/folder")
    public void addFolder(
            @PathVariable Long productId,
            @RequestParam Long folderId, //form 형식으로 넘어온 폴더의 ID
            // 해당 상품과 해당 폴더가 현재 로그인한 유저의 상품과 폴더가 맞는지 확인 하기위해 유저 정보를 받는다.
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ){
        productService.addFolder(productId, folderId, userDetails.getUser());
    }

//    //Admin은 모든 계정에서 등록한 상품을 조회할 수 있어야 합니다.
//    @GetMapping("/admin/products")
//    public List<ProductResponseDto> getAllproducts(){
//        return productService.getAllproducts();
//    }

}
