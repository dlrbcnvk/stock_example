package com.example.stock.repository;

import com.example.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LockRepository extends JpaRepository<Stock, Long> {
    // 실무에서는 이렇게 하면 안 되고,
    // 별도의 jdbc 를 사용해야 함

    // 실제 로직 전, 후로 get_lock() 과 release_lock() 을 수행해야 하므로 facade 클래스를 추가함.
    @Query(value = "select get_lock(:key, 3000)", nativeQuery = true)
    void getLock(String key);

    @Query(value = "select release_lock(:key, 3000)", nativeQuery = true)
    void releaseLock(String key);
}
