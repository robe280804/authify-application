package com.robertosodini.authify.service;

import com.robertosodini.authify.dto.UserRequestDto;
import com.robertosodini.authify.dto.UserResponseDto;
import com.robertosodini.authify.exceptions.EmailAlredyRegister;
import com.robertosodini.authify.mapper.UserMapper;
import com.robertosodini.authify.model.UserModel;
import com.robertosodini.authify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto createUser(UserRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())){
            throw new EmailAlredyRegister("Email già registrata");
        }
        UserModel userModel = userMapper.convertToUserEntity(request);
        UserModel newUserModel = userRepository.save(userModel);
        return userMapper.convertToUserResponse(newUserModel);
    }
}
