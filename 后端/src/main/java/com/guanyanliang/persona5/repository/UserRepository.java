package com.guanyanliang.persona5.repository;

import com.guanyanliang.persona5.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);  // 查找用户名
    boolean existsByUsername(String username);      // 判断用户名是否存在
}
