package com.example.service;

import com.example.entity.*;
import com.example.exception.*;
import com.example.repository.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AccountService {

    private AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    /**
     * Register new user
     * @param account
     * @return persisted Account object with newly created accountId
     * @throws UserRegistrationException
     * @throws DuplicateUsernameException
     */
    public Account register(Account account) throws UserRegistrationException, DuplicateUsernameException {
        if (account.getUsername().length() < 1)
            throw new UserRegistrationException("Username cannot be blank");
        if (account.getPassword().length() < 4)
            throw new UserRegistrationException("Password must be longer than 4 characters");
        if (accountRepository.existsByUsername(account.getUsername()))
            throw new DuplicateUsernameException("Account with username " + account.getUsername() + " already exists");

        return accountRepository.save(account);
    }

    /**
     * Login method
     * @param credentials Account object containing username and password
     * @return Account of persisted user
     * @throws AuthenticationException if username and password combo does not exist in database
     */
    public Account login(Account credentials) throws AuthenticationException {
        Optional<Account> loggedInAccount = accountRepository.findByUsernameAndPassword(credentials.getUsername(), credentials.getPassword());
        if (loggedInAccount.isPresent())
            return loggedInAccount.get();
            
        throw new AuthenticationException("Username or password incorrect");
    }

}
