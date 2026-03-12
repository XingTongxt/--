package com.guanyanliang.persona5.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private Long userId;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer rating;

    private LocalDateTime createTime = LocalDateTime.now();

    // getter setter
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public Long getProductId() {return productId;}
    public void setProductId(Long productId) {this.productId = productId;}

    public Long getUserId() {return userId;}
    public void setUserId(Long userId) {this.userId = userId;}

    public String getContent() {return content;}
    public void setContent(String content) {this.content = content;}

    public Integer getRating() {return rating;}
    public void setRating(Integer rating) {this.rating = rating;}

    public LocalDateTime getCreateTime() {return createTime;}
    public void setCreateTime(LocalDateTime createTime) {this.createTime = createTime;}
}