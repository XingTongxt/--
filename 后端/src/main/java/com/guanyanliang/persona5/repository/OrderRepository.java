package com.guanyanliang.persona5.repository;

import com.guanyanliang.persona5.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}