package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class StockServiceTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private PessimisticLockStockService pessimisticLockStockService;

    @BeforeEach
    public void before() {
        Stock stock = new Stock(1L, 100L);
        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    public void after() {
        stockRepository.deleteAll();
    }

    @Test
    public void stock_decrease() {
        stockService.decrease(1L, 1L);

        Stock findStock = stockRepository.findById(1L).orElseThrow();

        assertThat(findStock.getQuantity()).isEqualTo(99);
    }

    @Test
    public void 동시에_100개의_요청() throws InterruptedException {
        // 멀티쓰레드 요청
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decrease(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Stock findStock = stockRepository.findById(1L).orElseThrow();

        // 100 - (1 * 100)
        assertThat(findStock.getQuantity()).isEqualTo(0L);
    }

    @Test
    public void PessimisticLock_동시에_100개의_요청() throws InterruptedException {
        // 멀티쓰레드 요청
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pessimisticLockStockService.decrease(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Stock findStock = stockRepository.findById(1L).orElseThrow();

        // 100 - (1 * 100)
        assertThat(findStock.getQuantity()).isEqualTo(0L);
    }
}
