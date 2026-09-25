package com.advancedconcept.commonlibrary.controller;

import com.advancedconcept.commonlibrary.dto.record.UserRequest;
import com.advancedconcept.commonlibrary.dto.record.UserResponse;
import com.advancedconcept.commonlibrary.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> listUsers() {
        return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createNewUser(@Valid @RequestBody UserRequest request) {
        userService.create(request);
        return new ResponseEntity<>("Create a new user successfully", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(
            @Valid
            @PathVariable String id,
            @RequestBody UserRequest request
    ) {
        userService.update(id, request);
        return new ResponseEntity<>("Update a user successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@Valid @PathVariable String id) {
        userService.delete(id);
        return new ResponseEntity<>("Delete a user successfully", HttpStatus.OK);
    }
}
