package com.guanyanliang.persona5.repository;

import com.guanyanliang.persona5.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId); // 用户查看自己的订单
    List<Order> findByReturnStatus(String returnStatus);
    List<Order> findByRefundStatus(String refundStatus);
}