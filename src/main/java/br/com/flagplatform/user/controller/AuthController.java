package br.com.flagplatform.user.controller;

import br.com.flagplatform.user.dto.request.ChangeUserRoleRequest;
import br.com.flagplatform.user.dto.request.CreateUserRequest;
import br.com.flagplatform.user.dto.request.DevTokenRequest;
import br.com.flagplatform.user.dto.request.RegisterRequest;
import br.com.flagplatform.user.dto.response.DevTokenResponse;
import br.com.flagplatform.user.dto.response.UserResponse;
import br.com.flagplatform.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService service;

    @Override
    public UserResponse register(RegisterRequest request) {
        return service.register(request);
    }

    @Override
    public DevTokenResponse generateDevToken(DevTokenRequest request) {
        return service.generateDevToken(request);
    }

    @Override
    public UserResponse me(Object principal) {
        return service.me(principal);
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        return service.createUser(request);
    }

    @Override
    public List<UserResponse> listUsers() {
        return service.findAll();
    }

    @Override
    public List<UserResponse> listPending() {
        return service.listPending();
    }

    @Override
    public UserResponse approve(UUID id) {
        return service.approve(id);
    }

    @Override
    public UserResponse reject(UUID id) {
        return service.reject(id);
    }

    @Override
    public UserResponse changeRole(UUID id, ChangeUserRoleRequest request) {
        return service.changeRole(id, request);
    }
}
