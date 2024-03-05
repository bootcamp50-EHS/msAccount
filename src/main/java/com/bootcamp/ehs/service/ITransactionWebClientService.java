package com.bootcamp.ehs.service;

import com.bootcamp.ehs.dto.TransactionDTO;
import reactor.core.publisher.Mono;

public interface ITransactionWebClientService {

    Mono<TransactionDTO> saveTransaction(TransactionDTO transaction);
}
