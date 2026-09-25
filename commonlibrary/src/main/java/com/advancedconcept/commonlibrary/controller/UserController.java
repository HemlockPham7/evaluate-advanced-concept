package com.advancedconcept.commonlibrary.controller;

import com.advancedconcept.commonlibrary.common.ApiResponse;
import com.advancedconcept.commonlibrary.common.BaseController;
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
public class UserController extends BaseController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<List<UserResponse>> listUsers() {
        return getSuccessResponse(userService.getAll());
    }

    @PostMapping
    public ApiResponse<String> createNewUser(@Valid @RequestBody UserRequest request) {
        userService.create(request);
        return createSuccessResponse("Create a new user successfully");
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
