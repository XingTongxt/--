package com.guanyanliang.persona5.repository;

import com.guanyanliang.persona5.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository

public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findByTypeIgnoreCase(String type);
    List<Log> findByUsernameIgnoreCase(String username);
}

