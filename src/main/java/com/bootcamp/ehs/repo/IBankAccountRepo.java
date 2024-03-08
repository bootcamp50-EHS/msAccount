package com.bootcamp.ehs.repo;

import com.bootcamp.ehs.dto.ProductDTO;
import com.bootcamp.ehs.models.BankAccount;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface IBankAccountRepo extends ReactiveMongoRepository<BankAccount, String> {


    Mono<Long> countByCustomerId (String customerId);

    Mono<Long> countByCustomerIdAndProductIdAndBank(BankAccount bankAccount);
}
