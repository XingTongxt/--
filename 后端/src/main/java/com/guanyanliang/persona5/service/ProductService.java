package com.guanyanliang.persona5.service;

import com.guanyanliang.persona5.entity.Product;
import com.guanyanliang.persona5.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public void buyProduct(Long productId, int quantity) {

        Product product = productRepository.findById(productId).orElseThrow();

        if (product.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }

        product.setStock(product.getStock() - quantity);

        product.setSales(product.getSales() + quantity);

        productRepository.save(product);
    }
}