package com.bootcamp.ehs.service.impl;

import com.bootcamp.ehs.dto.BankDTO;
import com.bootcamp.ehs.dto.CustomerDTO;
import com.bootcamp.ehs.service.IBankWebClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BankWebClientServiceImpl implements IBankWebClientService {

    @Qualifier("bankWebClient")
    private final WebClient bankWebClient;

    @Override
    public Mono<BankDTO> findBankByCode(String codeBank) {
        return bankWebClient.get()
                .uri("/bank/code/{codeBank}", codeBank)
                .retrieve()
                .bodyToMono(BankDTO.class);
    }
}
