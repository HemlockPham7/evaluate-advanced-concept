package com.advancedconcept.commonlibrary.service;

import com.advancedconcept.commonlibrary.dto.request.UserRq;
import com.advancedconcept.commonlibrary.dto.response.UserRp;

import java.util.List;

public interface UserService {

    void create(UserRq request);

    void update(String id, UserRq request);

    List<UserRp> getAll();

    void delete(String id);
}
