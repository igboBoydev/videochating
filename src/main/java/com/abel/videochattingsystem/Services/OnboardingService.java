package com.abel.videochattingsystem.Services;

import com.abel.videochattingsystem.Config.EmailValidator;
import com.abel.videochattingsystem.Config.Helpers;
import com.abel.videochattingsystem.Config.JwtService;
import com.abel.videochattingsystem.Enums.Role;
import com.abel.videochattingsystem.Exception.ApiRequestException;
import com.abel.videochattingsystem.Models.ConfirmationToken;
import com.abel.videochattingsystem.Models.User;
import com.abel.videochattingsystem.Repository.UserRepository;
import com.abel.videochattingsystem.Schemas.LoginRequest;
import com.abel.videochattingsystem.Schemas.LoginResponse;
import com.abel.videochattingsystem.Schemas.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final UserRepository appUserRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final Helpers helpers;
    private final EmailValidator emailValidator;
    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;

    //TODO: service for login logic
    public LoginResponse login(LoginRequest request){
        //TODO: authenticate user email and password
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        //TODO: get user from database
        var user = appUserRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        //TODO: set all already existing user tokens to expired and revoked
        helpers.revokeUsersTokens(user);


        //TODO: create a user access token
        String token = jwtService.generateToken(user);

        //TODO: save user access token
        helpers.saveUserToken(user, token);

        //TODO: return token
        return new LoginResponse(token);
    }

    public String register (RegistrationRequest request){
        //TODO: validate user email
        boolean isEmailValid = emailValidator.test(request.getEmail());
        if(!isEmailValid){
            throw new IllegalStateException("email not valid");
        }

        //TODO: create a user email token from user details
        //NOTE: this token is supposed to be sent the user's email for email verification but since this is just for test I will not be implementing the email part of the service.
        String token = userService.signUpUser(
                new User(request.getUsername(), request.getEmail(), true, false, request.getPassword(), "online", Role.USER)
        );

        //TODO: return token
        return token;
    }


    @Transactional
    public LoginResponse confirmToken(String token) {
        //TODO: get token exists
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new ApiRequestException("Token not found")
                );

        //TODO:  check if token ecists
        if (confirmationToken.getConfirmedAt() != null) {
            throw new ApiRequestException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        // TODO: check if token is expired
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new ApiRequestException("Token expired");
        }

        //TODO set confirmed date
        confirmationTokenService.setConfirmedAt(token);

        //TODO: set user enabled status to true
        userService.enableAppUser(
                confirmationToken.getUser().getEmail());

        //TODO: get user by confirmation token
        var user = appUserRepository.findByEmail(confirmationToken.getUser().
                        getEmail()).
                orElseThrow(() -> new UsernameNotFoundException("User not found"));

        //TODO: generate jwt for user
        var jwtToken = jwtService.generateToken(user);

        //TODO: save user jwt token
        helpers.saveUserToken(user, jwtToken);

        //TODO: return token
        return new LoginResponse(jwtToken);
    }
}
