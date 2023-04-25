package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized void decrease(Long id, Long quantity) {
        try {
            Stock stock = stockRepository.findById(id).orElse(null);
            stock.decrease(quantity);
            stockRepository.saveAndFlush(stock);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
