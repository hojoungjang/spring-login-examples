package com.github.hojoungjang.spring_login_examples.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.hojoungjang.spring_login_examples.domain.User;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);
}
