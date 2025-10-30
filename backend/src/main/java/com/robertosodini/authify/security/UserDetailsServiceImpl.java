package com.robertosodini.authify.security;

import com.robertosodini.authify.model.UserModel;
import com.robertosodini.authify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       UserModel existUserModel =  userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Email non trovata per: " + username));

       return new User(existUserModel.getEmail(), existUserModel.getPassword(), new ArrayList<>());
    }
}
