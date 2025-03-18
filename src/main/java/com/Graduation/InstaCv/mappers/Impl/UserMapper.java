package com.Graduation.InstaCv.mappers.Impl;

import com.Graduation.InstaCv.data.dto.UserDto;
import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.mappers.Mapper;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapper implements Mapper<User, UserDto> {
    private ProfileMapper profileMapper;

    @Override
    public UserDto mapTo(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profile(profileMapper.mapTo(user.getProfile()))
                .build();
    }

    @Override
    public User mapFrom(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .profile(profileMapper.mapFrom(userDto.getProfile()))
                .build();
    }
}
