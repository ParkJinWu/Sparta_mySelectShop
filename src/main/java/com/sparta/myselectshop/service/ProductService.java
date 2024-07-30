package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.*;
import com.sparta.myselectshop.naver.dto.ItemDto;
import com.sparta.myselectshop.repository.FolderRepository;
import com.sparta.myselectshop.repository.ProductFolderRepository;
import com.sparta.myselectshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final FolderRepository folderRepository;
    private final ProductFolderRepository productFolderRepository;

    public static final int MIN_MY_PRICE = 100;



    public ProductResponseDto createProduct(ProductRequestDto requestDto, User user) {
        // Entity 객체로 만든다.

        //Spring Data JPA - save (저장하면서 반환)
        Product product = productRepository.save(new Product(requestDto,user));

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

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProducts(User user, int page, int size, String sortBy, boolean isAsc) {

        //정렬 & 페이징 처리하기 위한 Pageable 객체
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page , size, sort);

        UserRoleEnum userRoleEnum = user.getRole(); //로그인 요청을 한 User의 권한 정보 가져오기

        Page<Product> productList;

        if (userRoleEnum == UserRoleEnum.USER) {
            productList = productRepository.findAllByUser(user,pageable);
        }else {
            productList = productRepository.findAll(pageable); // select * from
        }

        return productList.map(ProductResponseDto::new);
    }

    @Transactional
    public void updateBySearch(Long id, ItemDto itemDto) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new NullPointerException("해당 상품은 존재하지 않습니다.")
        );

        product.updateByItemDto(itemDto);
    }

    public void addFolder(Long productId, Long folderId, User user) {
        // 받아온 id로 부터 상품 조회, 없으면 throw
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NullPointerException("해당 상품이 존재하지 않습니다.")
        );

        // 받아온 id로 부터 폴더 조회, 없으면 throw
        Folder folder = folderRepository.findById(folderId).orElseThrow(
                () -> new NullPointerException("해당 폴더가 존재하지 않습니다.")
        );

        // 해당 상품과 해당 폴더가 현재 로그인한 유저(user)의 상품과 폴더가 맞는지 확인
        if(!product.getUser().getId().equals(user.getId()) || !folder.getUser().getId().equals(user.getId())){
            throw new IllegalArgumentException("회원님의 관심상품이 아니거나, 회원님의 폴더가 아닙니다.");
        }

        // 중복 확인 (이미 추가한 폴더인지)
        // select * from ? where product_id = ? and folder_id ?
        Optional<ProductFolder> overlapFolder = productFolderRepository.findByProductAndFolder(product,folder);

        //존재하면 중복
        if(overlapFolder.isPresent()){
            throw new IllegalArgumentException("중복된 폴더입니다.");
        }

        //현재 로그인한 유저가 등록한 상품이 맞다면 폴더 등록
        // 새롭게 만드는 ProductFolder 객체 1개 == DB 1 row
        productFolderRepository.save(new ProductFolder(product,folder));



    }

    public Page<ProductResponseDto> getProductsInFolder(
            Long folderId,
            int page,
            int size,
            String sortBy,
            boolean isAsc,
            User user) {

        //정렬 & 페이징 처리하기 위한 Pageable 객체
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page , size, sort);

        // User의 해당 폴더에 등록된 상품 가져오기
        // 현재 로그인한 유저가 등록한 폴더에 속한 상품들 조회
        Page<Product> productList = productRepository.findAllByUserAndProductFolderList_FolderId(user,folderId,pageable);

        // map() 메서드를 사용하여 ProductResponseDto로 convert
        Page<ProductResponseDto> responseDtoList = productList.map(ProductResponseDto::new);

        return responseDtoList;
    }

//    //Admin 계정으로 로그인 시 수행
//    public List<ProductResponseDto> getAllproducts() {
//        // select * from
//        List<Product> productList = productRepository.findAll();
//        List<ProductResponseDto> responseDtoList = new ArrayList<>();
//
//        //iter
//        for (Product product : productList) {
//            //생성자 사용
//            responseDtoList.add(new ProductResponseDto(product));
//        }
//
//        return responseDtoList;
//    }
}
