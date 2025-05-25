package com.example.bankcards.controller;

import com.example.bankcards.dto.*;
import com.example.bankcards.security.UserDetailsHolder;
import com.example.bankcards.service.BlockRequestService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.utl.ErrorMessageCreator;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {
    private final CardService cardService;
    private final ErrorMessageCreator errorMessageCreator;
    private final UserService userService;
    private final UserDetailsHolder userDetailsHolder;
    private final BlockRequestService blockRequestService;

    @PostMapping("/me/transfer")
    public List<CardDTO> transfer(@RequestBody @Valid TransferDTO transferDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new ValidationException(errorMessageCreator.createErrorMessage(bindingResult));
        }

        return cardService.transfer(transferDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getUsers(){
        return null;//TODO пагниация нужна
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getUserById(@PathVariable UUID id){
        return userService.getUser(id);
    }

    @GetMapping("/me/info")
    public UserDTO getUserInfo(){
        return userService.convertToUserDTO(userDetailsHolder.getUserFromSecurityContext());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID id){
        userService.deleteUser(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO updateUser(@PathVariable UUID id, @RequestBody @Valid UpdatingUserDTO userDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new ValidationException(errorMessageCreator.createErrorMessage(bindingResult));
        }

        return userService.updateUser(id, userDTO);
    }

    @PostMapping("/me/cards/{id}/block-request")
    @ResponseStatus(HttpStatus.CREATED)
    public void createBlockRequest(@PathVariable UUID id){
        blockRequestService.createBlockRequest(id);
    }

    @DeleteMapping("/block-requests/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeBlockRequest(@PathVariable UUID id){
        blockRequestService.closeBlockRequest(id);
    }

    @GetMapping("/block-requests")
    public List<BlockRequestDTO> getAllBlockRequests(){
        return null;//TODO пагинация
    }
}
