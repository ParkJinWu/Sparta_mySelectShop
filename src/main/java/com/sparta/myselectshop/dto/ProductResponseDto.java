package com.sparta.myselectshop.dto;

import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.ProductFolder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ProductResponseDto {
    private Long id;
    private String title;
    private String link;
    private String image;
    private int lprice;
    private int myprice;

    private List<FolderResponseDto> productFolderList = new ArrayList<>();

    public ProductResponseDto(Product product) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.link = product.getLink();
        this.image = product.getImage();
        this.lprice = product.getLprice();
        this.myprice = product.getMyprice();



        // product에 우리가 연결한 중간 테이블 productFolderList를 가져옵니다.
        // productFolderList에는 폴더 정보가 있다.
        // 따라서 productFolderList를 FolderResponseDto로 변환하면서 productFolderList에 add
        // 지연 로딩을 사용하기 위해서는 영속성 컨텍스트가 필요하기 때문에 해당하는 메서드에 Transaction 환경을 조정해야함
        for (ProductFolder productFolder : product.getProductFolderList()) {
            productFolderList.add(new FolderResponseDto(productFolder.getFolder()));
        }
    }
}