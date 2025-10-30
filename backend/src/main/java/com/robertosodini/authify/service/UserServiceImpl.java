package com.robertosodini.authify.service;

import com.robertosodini.authify.dto.UserRequestDto;
import com.robertosodini.authify.dto.UserResponseDto;
import com.robertosodini.authify.mapper.UserMapper;
import com.robertosodini.authify.model.User;
import com.robertosodini.authify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto createUser(UserRequestDto request) {
        User user = userMapper.convertToUserEntity(request);
        User newUser = userRepository.save(user);
        return userMapper.convertToUserResponse(newUser);
    }
}
