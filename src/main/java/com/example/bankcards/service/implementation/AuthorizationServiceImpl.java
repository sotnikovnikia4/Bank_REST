package com.example.bankcards.service.implementation;

import com.example.bankcards.dto.AuthenticationDTO;
import com.example.bankcards.dto.CreationUserDTO;
import com.example.bankcards.dto.TokenDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.JWTUtil;
import com.example.bankcards.security.UserDetailsHolder;
import com.example.bankcards.service.AuthorizationService;
import com.example.bankcards.service.RoleService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsHolder userDetailsHolder;
    private final JWTUtil jwtUtil;

    private final RoleService roleService;
    private final UserService userService;

    public TokenDTO register(CreationUserDTO userDTO) {
        userService.checkIfLoginFreeOtherwiseThrowValidationException(userDTO.getLogin());
        Role role = roleService.getRoleOrThrowValidationException(userDTO.getRole());

        User user = User.builder()
                .name(userDTO.getName())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .login(userDTO.getLogin())
                .role(role)
                .build();

        user = userService.saveUser(user);

        return getTokenDTO(user);
    }

    public TokenDTO authenticate(AuthenticationDTO authenticationDTO) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationDTO.getLogin(), authenticationDTO.getPassword()));

        User user = userDetailsHolder.getUserFromPrincipal(authentication.getPrincipal());

        return getTokenDTO(user);
    }

    private TokenDTO getTokenDTO(User user){
        return TokenDTO.builder()
                .token(jwtUtil.generateToken(user))
                .build();
    }
}
