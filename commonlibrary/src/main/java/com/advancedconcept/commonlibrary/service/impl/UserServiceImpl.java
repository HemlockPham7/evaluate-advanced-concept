package com.advancedconcept.commonlibrary.service.impl;

import com.advancedconcept.commonlibrary.dto.request.UserRq;
import com.advancedconcept.commonlibrary.dto.response.UserRp;
import com.advancedconcept.commonlibrary.entity.User;
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

    @Override
    public void create(UserRq request) {
        userRepository.save(mapToEntity(request));
    }

    @Override
    public void update(String id, UserRq request) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found!");
        }

        User updatedUser = user.get();
        BeanUtils.copyProperties(request, updatedUser);
        userRepository.save(updatedUser);
    }

    @Override
    public List<UserRp> getAll() {
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

    private User mapToEntity(UserRq request) {
        return User.builder()
                .name(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
    }

    private UserRp mapToResponse(User user) {
        return UserRp.builder()
                .id(user.getId())
                .name(user.getName())
                .address(user.getAddress())
                .phone(user.getPhone())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }
}
