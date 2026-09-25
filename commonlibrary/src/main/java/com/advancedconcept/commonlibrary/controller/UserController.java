package com.advancedconcept.commonlibrary.controller;

import com.advancedconcept.commonlibrary.dto.request.UserRq;
import com.advancedconcept.commonlibrary.dto.response.UserRp;
import com.advancedconcept.commonlibrary.service.UserService;
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
    public ResponseEntity<List<UserRp>> listUsers() {
        return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createNewUser(@RequestBody UserRq request) {
        userService.create(request);
        return new ResponseEntity<>("Create a new user successfully", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(
            @PathVariable String id,
            @RequestBody UserRq request
    ) {
        userService.update(id, request);
        return new ResponseEntity<>("Update a user successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        userService.delete(id);
        return new ResponseEntity<>("Delete a user successfully", HttpStatus.OK);
    }
}
