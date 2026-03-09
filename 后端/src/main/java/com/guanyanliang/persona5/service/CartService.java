package com.guanyanliang.persona5.service;

import com.guanyanliang.persona5.dto.CartDTO;
import com.guanyanliang.persona5.entity.Cart;
import com.guanyanliang.persona5.entity.Product;
import com.guanyanliang.persona5.entity.User;
import com.guanyanliang.persona5.repository.CartRepository;
import com.guanyanliang.persona5.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    // 获取 Cart 列表（只包含数据库字段）
    public List<Cart> getCart(User user) {
        return cartRepository.findByUser(user);
    }

    // 获取 CartDTO（带商品信息）用于前端渲染
    public List<CartDTO> getCartWithProductInfo(User user) {
        List<Cart> cartList = cartRepository.findByUser(user);
        return cartList.stream().map(cart -> {
            Product product = productRepository.findById(cart.getProductId())
                    .orElseThrow(() -> new RuntimeException("商品不存在"));
            return new CartDTO(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getImg(),
                    cart.getQuantity()
            );
        }).toList();
    }

    // 添加商品到购物车
    public Cart addToCart(User user, Long productId) {
        Cart cartItem = cartRepository.findByUserAndProductId(user, productId)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUser(user);
                    c.setProductId(productId);
                    c.setQuantity(0);
                    return c;
                });
        cartItem.setQuantity(cartItem.getQuantity() + 1);
        return cartRepository.save(cartItem);
    }

    // 修改购物车数量
    public Cart updateQuantity(User user, Long productId, Integer quantity) {
        Cart cartItem = cartRepository.findByUserAndProductId(user, productId)
                .orElseThrow(() -> new RuntimeException("购物车商品不存在"));
        cartItem.setQuantity(quantity);
        return cartRepository.save(cartItem);
    }

    // 删除购物车商品
    @Transactional
    public void removeItem(User user, Long productId) {
        Optional<Cart> cartOpt = cartRepository.findByUserAndProductId(user, productId);
        if (cartOpt.isPresent()) {
            cartRepository.delete(cartOpt.get());
        } else {
            // 不存在的商品返回 409
            throw new RuntimeException("购物车项不存在或已被删除");
        }
    }

}
