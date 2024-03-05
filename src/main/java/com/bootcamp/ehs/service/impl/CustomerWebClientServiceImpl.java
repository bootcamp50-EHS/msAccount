package com.bootcamp.ehs.service.impl;

import com.bootcamp.ehs.dto.CustomerDTO;
import com.bootcamp.ehs.service.ICustomerWebClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerWebClientServiceImpl implements ICustomerWebClientService {

    @Qualifier("customerWebClient")
    private final WebClient customerWebClient;
    @Override
    public Mono<CustomerDTO> findCustomerById(String id) {
        return customerWebClient.get()
                .uri("/customer/{id}", id)
                .retrieve()
                .bodyToMono(CustomerDTO.class);
    }
}
