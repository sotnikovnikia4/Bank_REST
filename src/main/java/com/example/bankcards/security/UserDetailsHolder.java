package com.example.bankcards.security;

import com.example.bankcards.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public final class UserDetailsHolder {
    public User getUserFromSecurityContext(){
        return ((UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

    public UserDetails getUserDetailsSecurityContext(){
        return (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
