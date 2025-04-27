package com.Graduation.InstaCv.mappers.Impl;

import com.Graduation.InstaCv.data.dto.UserDto;
import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.mappers.Mapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapper implements Mapper<User, UserDto> {
    private ProfileMapper profileMapper;

    @Override
    public UserDto mapTo(User user) {
        UserDto.UserDtoBuilder builder = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail());
        if (user.getProfile() != null)
            builder.profile(profileMapper.mapTo(user.getProfile()));
        return builder.build();
    }

    @Override
    public User mapFrom(UserDto userDto) {
        User.UserBuilder builder = User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail());
        User user = builder.build();
        if (userDto.getProfile() != null)
            builder.profile(profileMapper.mapFrom(userDto.getProfile(), user));
        return user;
    }
}
