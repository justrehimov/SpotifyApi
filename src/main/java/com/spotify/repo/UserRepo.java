package com.spotify.repo;

import com.spotify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    Boolean existsByEmailOrUsername(String email, String username);

    @Query("select u from User u where lower(u.username)=lower(:username)")
    Optional<User> findByUsername(String username);

    @Query("select u from User u where (lower(u.username)=lower(:username) or lower(u.email)=lower(:username))")
    Optional<User> findUserByEmailOrUsername(String username);

    @Query("select u from User u where lower(u.email)=lower(:email)")
    Optional<User> findByEmail(String email);
}
