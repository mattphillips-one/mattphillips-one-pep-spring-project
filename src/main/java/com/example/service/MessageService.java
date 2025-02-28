package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.*;
import com.example.exception.MessageCreationException;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;

@Service
public class MessageService {

    MessageRepository messageRepository;
    AccountRepository accountRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * Save new message to repository
     * @param transientMessage
     * @return persisted message object with newly created messageId
     * @throws MessageCreationException
     */
    public Message createNewMessage(Message transientMessage) throws MessageCreationException {
        // will throw MessageCreationException if length not between 0-255 chars
        this.verifyMessageLength(transientMessage.getMessageText());

        if (!accountRepository.existsById(transientMessage.getPostedBy()))
            throw new MessageCreationException("Invalid posted_by id");
        
        return messageRepository.save(transientMessage);
    }

    /**
     * Gets all messages in repository
     * @return List of Message objects, empty if none
     */
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    /**
     * Gets message by message_id
     * @param id
     * @return Message object if exists, null otherwise
     */
    public Message getMessageById(int id) {
        Optional<Message> optionalMessage = messageRepository.findById(id);
        if (optionalMessage.isPresent())
            return optionalMessage.get();
        return null;
    }

    /**
     * Deletes a message in the repository by message_id
     * @param id
     * @return Number of rows updated by deletion (1), null otherwise
     */
    public Integer deleteMessageById(int id) {
        Optional<Message> optionalMessage = messageRepository.findById(id);
        if (optionalMessage.isPresent()) {
            messageRepository.deleteById(id);
            return 1;
        }
        return null;
    }

    /**
     * Update existing message in repository
     * @param id
     * @param messageText
     * @return Integer representing number of rows updated (1), null otherwise
     * @throws MessageCreationException if message text not between 0-255 characters
     */
    public Integer updateMessage(int id, String messageText) throws MessageCreationException {
        // automatically throws MessageCreationException if messageText not valid
        this.verifyMessageLength(messageText);
        System.err.print("Message length:  " + messageText.length() + "\nMessage: " + messageText);

        Optional<Message> optionalMessage = messageRepository.findById(id);
        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            message.setMessageText(messageText);
            messageRepository.save(message);
            return 1;
        }

        throw new MessageCreationException("message_id invalid");
    }

    /**
     * @param accountId
     * @return List of messages from user with accountId
     */
    public List<Message> getMessagesByAccountId(int accountId) {
        return messageRepository.findAllByPostedBy(accountId);
    }

    /*
     * Throws MessageCreationException with appropriate message if length of text not
     * between 0 and 255 characters
     */
    private boolean verifyMessageLength(String messageText) throws MessageCreationException {
        if (messageText.length() == 0)
            throw new MessageCreationException("Message cannot be blank");
        if (messageText.length() > 255)
            throw new MessageCreationException("Message cannot be longer than 255 characters");
        return true;
    }

}
