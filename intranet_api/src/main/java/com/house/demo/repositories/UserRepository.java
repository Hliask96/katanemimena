package com.house.demo.repositories;

import java.util.List;

import com.house.demo.classes.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByLastName(String lastName);

    User findByUsername(String username);


    @Query( "select u from User u inner join u.roles r where r.name = :role" )
    List<User> findBySpecificRole(@Param("role") String role);
}