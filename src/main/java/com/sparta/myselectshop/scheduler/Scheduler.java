package com.sparta.myselectshop.scheduler;

import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.naver.dto.ItemDto;
import com.sparta.myselectshop.naver.service.NaverApiService;
import com.sparta.myselectshop.repository.ProductRepository;
import com.sparta.myselectshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "Scheduler")
@Component
@RequiredArgsConstructor
public class Scheduler {

    // 해당하는 목록을 다시 재검색을 해야하기 때문에 주입 받아와야 한다.

    private final NaverApiService naverApiService; //해당 목록 검색
    private final ProductService productService; // 검색할 Product의 목록
    private final ProductRepository productRepository;

    // 초, 분, 시, 일, 월, 주 순서
    //@Scheduled(cron = "*/10 * * * * *") // 테스트(10초)
    @Scheduled(cron = "0 0 1 * * *") // 매일 새벽 1시
    public void updatePrice() throws InterruptedException {
        log.info("가격 업데이트 실행");
        List<Product> productList = productRepository.findAll();
        for (Product product : productList) {
            // 1초에 한 상품 씩 조회합니다 (NAVER 제한)
            TimeUnit.SECONDS.sleep(1);

            // i 번째 관심 상품의 제목으로 검색을 실행합니다.
            String title = product.getTitle();
            List<ItemDto> itemDtoList = naverApiService.searchItems(title);

            if (itemDtoList.size() > 0) {
                ItemDto itemDto = itemDtoList.get(0);
                // i 번째 관심 상품 정보를 업데이트합니다.
                Long id = product.getId();
                try {
                    productService.updateBySearch(id, itemDto);
                } catch (Exception e) {
                    log.error(id + " : " + e.getMessage());
                }
            }
        }
    }

}