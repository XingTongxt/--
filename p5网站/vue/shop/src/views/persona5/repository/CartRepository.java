package com.guanyanliang.persona5.repository;

import com.guanyanliang.persona5.entity.Cart;
import com.guanyanliang.persona5.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // 查询某个用户的购物车列表
    List<Cart> findByUser(User user);

    // 查询某个用户购物车里指定商品
    Optional<Cart> findByUserAndProductId(User user, Long productId);

    // 删除用户购物车里指定商品
    void deleteByUserAndProductId(User user, Long productId);

}
