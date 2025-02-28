package com.example.controller;

import com.example.entity.*;
import com.example.exception.AuthenticationException;
import com.example.exception.DuplicateUsernameException;
import com.example.exception.MessageCreationException;
import com.example.exception.UserRegistrationException;
import com.example.service.*;


import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
public class SocialMediaController {

    AccountService accountService;
    MessageService messageService;

    @Autowired
    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    @PostMapping("register")
    public ResponseEntity<Account> register(@RequestBody Account account)
        throws UserRegistrationException, DuplicateUsernameException
    {
        Account persistedAccount = accountService.register(account);
        return ResponseEntity.status(HttpStatus.OK).body(persistedAccount);
    }

    @PostMapping("login")
    public ResponseEntity<Account> login(@RequestBody Account account) throws AuthenticationException {
        Account loggedInAccount = accountService.login(account);
        return ResponseEntity.status(200).body(loggedInAccount);
    }

    @PostMapping("messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message msg) throws MessageCreationException {
        Message persistedMessage = messageService.createNewMessage(msg);
        return ResponseEntity.ok(persistedMessage);
    }

    @GetMapping("messages")
    public List<Message> getAllMessages() {
        return messageService.getAllMessages();
    }

    @GetMapping("messages/{message_id}")
    public ResponseEntity<Message> getMessageById(@PathVariable int message_id) {
        Message message = messageService.getMessageById(message_id);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("messages/{message_id}")
    public ResponseEntity<Integer> deleteMessage(@PathVariable int message_id) {
        Integer rowsUpdated = messageService.deleteMessageById(message_id);
        return ResponseEntity.ok(rowsUpdated);
    }

    @PatchMapping("messages/{message_id}")
    public ResponseEntity<Integer> updateMessage(@PathVariable int message_id, @RequestBody Message message)
        throws MessageCreationException
    {
        String messageText = message.getMessageText(); // request body wrapped in Message class then extract text
        Integer rowsUpdated = messageService.updateMessage(message_id, messageText);
        return ResponseEntity.ok(rowsUpdated);
    }

    @GetMapping("accounts/{account_id}/messages")
    public ResponseEntity<List<Message>> getAllUserMessages(@PathVariable int account_id) {

        List<Message> messages = messageService.getMessagesByAccountId(account_id);
        return ResponseEntity.ok(messages);
    }

    /*
     * EXCEPTION HANDLING methods below
     * Return relevant status code and blank response body when the above methods fail
     * 
     * note: Exception handling in this way is not required or necessary for this project
     * and might even make the code a bit more difficult to read, but I thought it would
     * interesting to use Spring's exception handling features for practice
     */

    @ExceptionHandler(UserRegistrationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleUserRegistrationException(){}

    @ExceptionHandler(DuplicateUsernameException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public void handleDuplicateUsernameException(){}

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleAuthenticationException(){}

    @ExceptionHandler(MessageCreationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleMessageCreationException(){}

}
