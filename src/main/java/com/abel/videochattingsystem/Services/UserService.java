package com.abel.videochattingsystem.Services;


import com.abel.videochattingsystem.Exception.ApiRequestException;
import com.abel.videochattingsystem.Models.ConfirmationToken;
import com.abel.videochattingsystem.Models.User;
import com.abel.videochattingsystem.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

    public String signUpUser(User user){
        boolean checkUser = usersRepository.findByEmail(user.getEmail()).isPresent();

        if(checkUser){
            throw new ApiRequestException("User already registered");
//            throw new IllegalStateException("User already registered");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        usersRepository.save(user);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), user);

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        return token;

    }

    public int enableAppUser(String email) {
        System.out.println("email  = " + email);
        return usersRepository.enableUser(email);
    }
}
