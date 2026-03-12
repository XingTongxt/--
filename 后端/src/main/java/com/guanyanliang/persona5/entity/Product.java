package com.guanyanliang.persona5.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double price;
    private String img;
    private String category;
    private String description;
    private Integer stock;
    private Integer sales;

    // 构造函数
    public Product() {}

    public Product(String name, Double price, String img, String category, String description, Integer stock, Integer sales) {
        this.name = name;
        this.price = price;
        this.img = img;
        this.category = category;
        this.description = description;
        this.stock = stock;
        this.sales = sales;
    }

    // getter 和 setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description;}
    public void setDescription(String description) { this.description = description;}

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Integer getSales() { return sales; }
    public void setSales(Integer sales) { this.sales = sales; }
}
