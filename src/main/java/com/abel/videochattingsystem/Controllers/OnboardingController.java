package com.abel.videochattingsystem.Controllers;

import com.abel.videochattingsystem.Models.User;
import com.abel.videochattingsystem.Schemas.LoginRequest;
import com.abel.videochattingsystem.Schemas.LoginResponse;
import com.abel.videochattingsystem.Schemas.RegistrationRequest;
import com.abel.videochattingsystem.Services.OnboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OnboardingController {

    private final OnboardingService onboardingService;

    @PostMapping("/register")
    public void register(@RequestBody @Valid RegistrationRequest request){
        onboardingService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return onboardingService.login(request);
    }

    @GetMapping(path = "/confirm")
    public LoginResponse confirm(@RequestParam("token") String token) {
        System.out.println("token =  " + token);
        return onboardingService.confirmToken(token);
    }
}
