package com.example.bankcards.service;

import com.example.bankcards.dto.AuthenticationDTO;
import com.example.bankcards.dto.CreationUserDTO;
import com.example.bankcards.dto.TokenDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JWTUtil;
import com.example.bankcards.security.UserDetailsHolder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsHolder userDetailsHolder;
    private final JWTUtil jwtUtil;

    private final UserRepository userRepository;

    public TokenDTO register(CreationUserDTO userDTO) {
        User user = User.builder()
                .name(userDTO.getName())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .login(userDTO.getLogin())
                .role(userDTO.getRole())
                .build();

        user = userRepository.save(user);

        return getTokenDTO(user);
    }

    public TokenDTO authenticate(@Valid AuthenticationDTO authenticationDTO) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(), authenticationDTO.getPassword()));

        User user = userDetailsHolder.getUserFromPrincipal(authentication.getPrincipal());

        return getTokenDTO(user);
    }

    private TokenDTO getTokenDTO(User user){
        return TokenDTO.builder()
                .token(jwtUtil.generateToken(user))
                .build();
    }
}
