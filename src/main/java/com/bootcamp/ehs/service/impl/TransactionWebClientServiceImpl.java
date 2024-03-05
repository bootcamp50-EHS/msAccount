package com.bootcamp.ehs.service.impl;

import com.bootcamp.ehs.dto.TransactionDTO;
import com.bootcamp.ehs.service.ITransactionWebClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TransactionWebClientServiceImpl implements ITransactionWebClientService {

    @Qualifier("transactionWebClient")
    private final WebClient transactionWebClient;

    @Override
    public Mono<TransactionDTO> saveTransaction(TransactionDTO transaction) {
        return transactionWebClient.post()
                .uri("/transaction")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(transaction)
                .retrieve()
                .bodyToMono(TransactionDTO.class);
    }
}
