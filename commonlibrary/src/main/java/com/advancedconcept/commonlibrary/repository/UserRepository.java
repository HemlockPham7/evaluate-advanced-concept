package com.advancedconcept.commonlibrary.repository;

import com.advancedconcept.commonlibrary.entity.User;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    void deleteById(@NonNull Long id);
}
