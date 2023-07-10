package com.example.stock.domain;

import jakarta.persistence.*;

@Entity
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Long quantity;

    @Version
    private Long version;

    public Stock() {

    }

    public Stock(Long productId, Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getQuantity() {
        return quantity;
    }

    // 재고감소
    public void decrease(Long quantity) {
        if (this.quantity - quantity < 0) {
            throw new RuntimeException("재고감소 실패");
        }
        this.quantity = this.quantity - quantity;
    }
}
