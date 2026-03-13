package com.guanyanliang.persona5.repository;

import com.guanyanliang.persona5.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId); // 查询某个订单的所有商品
}