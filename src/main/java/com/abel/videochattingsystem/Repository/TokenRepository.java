package com.abel.videochattingsystem.Repository;


import com.abel.videochattingsystem.Models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query("""
            select t from Token t inner join users u on t.users.id = u.id
            where u.id = :userId and (t.expired = false or t.revoked=false)
            """)
    List<Token> findAllValidTokensByUsers(Integer userId);

    Optional<Token> findByKey(String key);
}
