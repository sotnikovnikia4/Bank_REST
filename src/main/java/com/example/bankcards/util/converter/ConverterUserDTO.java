package com.example.bankcards.util.converter;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.RoleService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ConverterUserDTO implements Converter<User, UserDTO> {
    @Override
    public UserDTO convert(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .login(user.getLogin())
                .role(user.getRole().getRole().substring(RoleService.PREFIX_ROLE.length()))
                .build();
    }
}
