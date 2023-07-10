package com.example.stock.facade;

import com.example.stock.repository.RedisRepository;
import com.example.stock.service.StockService;
import org.springframework.stereotype.Service;

@Service
public class LettuceLockStockFacade {

    private RedisRepository redisRepository;

    private StockService stockService;

    public LettuceLockStockFacade(RedisRepository redisRepository, StockService stockService) {
        this.redisRepository = redisRepository;
        this.stockService = stockService;
    }

    public void decrease(Long key, Long quantity) throws InterruptedException {
        while (!redisRepository.lock(key)) {
            Thread.sleep(100);
        }

        try {
            stockService.decrease(key, quantity);
        } finally {
            redisRepository.unlock(key);
        }
    }
}
