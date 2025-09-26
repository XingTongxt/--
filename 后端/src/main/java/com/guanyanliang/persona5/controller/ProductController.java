package com.guanyanliang.persona5.controller;

import com.guanyanliang.persona5.entity.Product;
import com.guanyanliang.persona5.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // 允许所有跨域请求，解决前端 fetch 跨域问题
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
}
