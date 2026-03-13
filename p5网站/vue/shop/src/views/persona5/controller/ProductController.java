package com.guanyanliang.persona5.controller;

import com.guanyanliang.persona5.entity.Product;
import com.guanyanliang.persona5.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 获取所有商品
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 根据 id 获取商品
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productRepository.findById(id).orElse(null);
    }

    // 按销量排序
    @GetMapping("/sales")
    public List<Product> getProductsBySales() {
        return productRepository.findAllByOrderBySalesDesc();
    }
    // 按价格排序（低到高）
    @GetMapping("/priceAsc")
    public List<Product> getProductsByPriceAsc() {
        return productRepository.findAllByOrderByPriceAsc();
    }

    // 按价格排序（高到低）
    @GetMapping("/priceDesc")
    public List<Product> getProductsByPriceDesc() {
        return productRepository.findAllByOrderByPriceDesc();
    }
}
