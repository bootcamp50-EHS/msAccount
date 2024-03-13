package com.bootcamp.ehs.service.impl;

import com.bootcamp.ehs.dto.BankDTO;
import com.bootcamp.ehs.dto.CustomerDTO;
import com.bootcamp.ehs.service.IBankWebClientService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BankWebClientServiceImpl implements IBankWebClientService {

    @Qualifier("bankWebClient")
    private final WebClient bankWebClient;

    @Qualifier("gatewayServiceUrl")
    private final WebClient webClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerWebClientServiceImpl.class);

    @Override
    public Mono<BankDTO> findBankByCode(String codeBank) {
        LOGGER.info("En findBankByCode: el codeBank= "+ codeBank);
        /*return webClient.get()
                .uri("/api/bank/list/code/{codeBank}", codeBank)
                .retrieve()
                .bodyToMono(BankDTO.class);*/

        return bankWebClient.get()
                .uri("/list/code/{codeBank}", codeBank)
                .retrieve()
                .bodyToMono(BankDTO.class);
    }
}
