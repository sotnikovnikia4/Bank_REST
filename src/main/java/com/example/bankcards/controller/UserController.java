package com.example.bankcards.controller;

import com.example.bankcards.dto.*;
import com.example.bankcards.security.UserDetailsHolder;
import com.example.bankcards.service.BlockRequestService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.ErrorMessageCreator;
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
    public PageDTO<UserDTO> getUsers(
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @ModelAttribute UserFilterDTO filter
    ){
        return userService.getUsers(pageNumber, pageSize, filter);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getUserById(@PathVariable UUID id){
        return userService.getUser(id);
    }

    @GetMapping("/me")
    public UserDTO getUserInfo(){
        return userService.getCurrentUserInfo();
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

    @GetMapping("/me/cards")
    @ResponseStatus(HttpStatus.OK)
    public PageDTO<CardDTO> getCards(
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @ModelAttribute CardFilterDTO cardFilterDTO
    ){
        return cardService.getCardsLikeUser(pageNumber, pageSize, cardFilterDTO);
    }

    @GetMapping("/me/cards/{cardId}")
    @ResponseStatus(HttpStatus.OK)
    public CardDTO getCards(@PathVariable UUID cardId){
        return cardService.getCardLikeUser(cardId);
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
    @ResponseStatus(HttpStatus.OK)
    public PageDTO<BlockRequestDTO> getAllBlockRequests(@RequestParam int pageNumber, @RequestParam int pageSize){
        return blockRequestService.getAll(pageNumber, pageSize);
    }

    @GetMapping("/block-requests/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BlockRequestDTO getBlockRequest(@PathVariable UUID id){
        return blockRequestService.getOne(id);
    }
}
