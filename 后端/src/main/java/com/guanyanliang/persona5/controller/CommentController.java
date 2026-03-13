package com.guanyanliang.persona5.controller;

import com.guanyanliang.persona5.entity.Comment;
import com.guanyanliang.persona5.entity.User;
import com.guanyanliang.persona5.repository.CommentRepository;
import com.guanyanliang.persona5.service.UserService;
import com.guanyanliang.persona5.util.JwtUtil;

import io.jsonwebtoken.Claims;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin
public class CommentController {

    private final CommentRepository commentRepository;
    private final UserService userService;

    public CommentController(CommentRepository commentRepository, UserService userService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
    }
    // 获取商品评论
    @GetMapping("/{productId}")
    public List<Map<String, Object>> getComments(@PathVariable Long productId){
        List<Comment> comments = commentRepository.findByProductId(productId);

        return comments.stream().map(c -> {
            Map<String,Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("productId", c.getProductId());
            map.put("userId", c.getUserId());
            map.put("content", c.getContent());
            map.put("rating", c.getRating());
            map.put("createTime", c.getCreateTime());

            // 查用户名
            String username = "匿名";
            if (c.getUserId() != null) {
                userService.findById(c.getUserId()).ifPresent(u -> map.put("username", u.getUsername()));
            } else {
                map.put("username", username);
            }

            return map;
        }).toList();
    }
    // 发表评论
    @PostMapping
    public Comment addComment(
            @RequestBody Comment comment,
            @RequestHeader("Authorization") String authHeader
    ){
        String token = authHeader.replace("Bearer ", "");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            throw new RuntimeException("无效的 token，未找到 userId");
        }
        comment.setUserId(userId);
        comment.setCreateTime(LocalDateTime.now());
        return commentRepository.save(comment);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id,
                                           @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "").trim();
        String username = JwtUtil.getUsername(token);

        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isEmpty())
            return ResponseEntity.status(404).body("评论不存在");

        Comment comment = optionalComment.get();

        // 只有自己可以删除自己的评论
        if (!userService.findById(comment.getUserId())
                .map(u -> u.getUsername().equals(username))
                .orElse(false)) {
            return ResponseEntity.status(403).body("只能删除自己的评论");
        }

        commentRepository.delete(comment);
        return ResponseEntity.ok("删除成功");
    }

}