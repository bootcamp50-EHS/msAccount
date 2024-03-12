package com.bootcamp.ehs.repo;

import com.bootcamp.ehs.dto.BankDTO;
import com.bootcamp.ehs.dto.ProductDTO;
import com.bootcamp.ehs.models.BankAccount;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface IBankAccountRepo extends ReactiveMongoRepository<BankAccount, String> {


    Mono<Long> countByCustomerId (String customerId);

    Mono<Long> countByCustomerIdAndProductIdAndBank(BankAccount bankAccount);


    Mono<Boolean> existsByCustomerIdAndProductIdAndBank(String customerId, String productId, BankDTO bank);
}
