package com.guanyanliang.persona5.dto;

public class CartDTO {
    private Long productId;
    private String name;
    private Double price;
    private String img;
    private Integer quantity;

    public CartDTO(Long productId, String name, Double price, String img, Integer quantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.img = img;
        this.quantity = quantity;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
