package com.advancedconcept.commonlibrary.service.impl;

import com.advancedconcept.commonlibrary.dto.record.UserRequest;
import com.advancedconcept.commonlibrary.dto.record.UserResponse;
import com.advancedconcept.commonlibrary.entity.User;
import com.advancedconcept.commonlibrary.mapper.UserMapper;
import com.advancedconcept.commonlibrary.repository.UserRepository;
import com.advancedconcept.commonlibrary.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public void create(UserRequest request) {
        userRepository.save(mapToEntity(request));
    }

    @Override
    public void update(String id, UserRequest request) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found!");
        }

        User updatedUser = user.get();
        BeanUtils.copyProperties(request, updatedUser);
        userRepository.save(updatedUser);
    }

    @Override
    public List<UserResponse> getAll() {
        List<User> list = userRepository.findAll();
        return list.stream().map(this::mapToResponse).toList();
    }

    @Override
    public void delete(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found!");
        }

        userRepository.deleteById(id);
    }

    private User mapToEntity(UserRequest request) {
        return userMapper.toEntity(request);
    }

    private UserResponse mapToResponse(User user) {
        return userMapper.toResponse(user);
    }
}
