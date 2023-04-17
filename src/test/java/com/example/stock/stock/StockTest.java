package com.example.stock.stock;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import com.example.stock.service.StockService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class StockTest {
    @Autowired
    StockService stockService;
    @Autowired
    StockRepository stockRepository;

    @BeforeEach
    public void 생성() {
        // given
        Stock stock = new Stock(1L , 100L);
        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    public void 삭제() {
        stockRepository.deleteAll();
    }

    @Test
    public void 감소테스트() {
        // given
        Stock stock = stockRepository.findById(1L).orElse(null);

        // when
        stockService.decrease(1L , 1L);

        // then
        Assertions.assertThat(stock.getQuantity()).isEqualTo(9L);
    }

    @Test
    public void 동시에_100개의_감소요청() throws InterruptedException{
        int thread = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(thread);
        CountDownLatch latch = new CountDownLatch(thread);
        for (int i = 0; i < thread; i++) {
            executorService.submit(() -> {
                try{
                    stockService.decrease(1L , 1L);
                }finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Stock stock = stockRepository.findById(1L).orElse(null);

        System.out.println(stock.getQuantity());
        Assertions.assertThat(stock.getQuantity()).isEqualTo(0L);
    }
}
