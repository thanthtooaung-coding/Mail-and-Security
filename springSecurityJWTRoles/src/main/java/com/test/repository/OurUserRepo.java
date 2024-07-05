package com.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.entity.OurUsers;

import java.util.Optional;

public interface OurUserRepo extends JpaRepository<OurUsers, Integer> {
    Optional<OurUsers> findByEmail(String email);
}
