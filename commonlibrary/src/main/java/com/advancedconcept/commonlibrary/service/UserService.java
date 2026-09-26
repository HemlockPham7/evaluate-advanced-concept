package com.advancedconcept.commonlibrary.service;

import com.advancedconcept.commonlibrary.dto.record.UserRequest;
import com.advancedconcept.commonlibrary.dto.record.UserResponse;

import java.util.List;

public interface UserService {

    void create(UserRequest request);

    void update(Long id, UserRequest request);

    List<UserResponse> getAll();

    void delete(Long id);
}
