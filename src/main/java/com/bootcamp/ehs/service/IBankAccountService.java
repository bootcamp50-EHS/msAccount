package com.bootcamp.ehs.service;

import com.bootcamp.ehs.dto.BankAccountDTO;
import com.bootcamp.ehs.dto.CustomerDTO;
import com.bootcamp.ehs.dto.TransactionDTO;
import com.bootcamp.ehs.dto.TransferDTO;
import com.bootcamp.ehs.models.BankAccount;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface IBankAccountService {

    Mono<BankAccount> saveAccount(BankAccount bankAccount);

    Mono<BankAccount> updateAccount(BankAccount bankAccount);

    Flux<BankAccount> findAllByCustomer(String customerId );

    Mono<BankAccount> findByAccountNumber(String accountNumber);

    Mono<Long> countByCustomerId(String customerId) ;


    Mono<Boolean> hasLeastOneOwner(BankAccount bankAccount);


    Mono<BankAccount> getByAccountId(String accountId);

}
