package com.advancedconcept.commonlibrary.repository;

import com.advancedconcept.commonlibrary.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    void deleteById(String id);
}
