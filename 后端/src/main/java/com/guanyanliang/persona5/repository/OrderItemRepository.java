package com.guanyanliang.persona5.repository;

import com.guanyanliang.persona5.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}