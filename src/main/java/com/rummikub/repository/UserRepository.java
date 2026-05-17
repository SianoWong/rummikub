package com.rummikub.repository;

import com.rummikub.entity.po.UserPO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserPO, Long> {

    Optional<UserPO> findByUsername(String username);

    boolean existsByUsername(String username);
}
