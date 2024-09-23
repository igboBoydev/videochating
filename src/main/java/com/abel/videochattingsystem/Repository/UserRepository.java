package com.abel.videochattingsystem.Repository;

import com.abel.videochattingsystem.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET enabled = true WHERE email = :email", nativeQuery = true)
    int enableUser(String email);
}
