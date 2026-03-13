package com.guanyanliang.persona5.repository;

import com.guanyanliang.persona5.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByOrderBySalesDesc();
    // 按价格排序（低→高）
    List<Product> findAllByOrderByPriceAsc();

    // 按价格排序（高→低）
    List<Product> findAllByOrderByPriceDesc();
}
