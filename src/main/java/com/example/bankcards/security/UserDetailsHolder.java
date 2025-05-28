package com.example.bankcards.security;

import com.example.bankcards.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public final class UserDetailsHolder {
    public User getUserFromSecurityContext(){
        return ((UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

    public User getUserFromPrincipal(Object principal) throws RuntimeException{
        if(principal instanceof UserDetailsImpl){
            return ((UserDetailsImpl)principal).getUser();
        }
        else{
            throw new RuntimeException("Error getting user from principal");
        }
    }
}
