package com.bootcamp.ehs.service.impl;

import com.bootcamp.ehs.dto.CustomerDTO;
import com.bootcamp.ehs.service.ICustomerWebClientService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerWebClientServiceImpl implements ICustomerWebClientService {

    @Qualifier("customerWebClient")
    private final WebClient customerWebClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerWebClientServiceImpl.class);


    @Qualifier("gatewayServiceUrl")
    private final WebClient webClient;

    @Override
    public Mono<CustomerDTO> findCustomerById(String id) {
        LOGGER.info("Gateway -> En findCustomerById: el id= "+ id);
        return webClient.get()
                .uri("/api/customer/list/{id}", id)
                .retrieve()
                .bodyToMono(CustomerDTO.class);

        /*return customerWebClient.get()
                .uri("/api/customer/list/{id}", id)
                .retrieve()
                .bodyToMono(CustomerDTO.class);*/
    }

    @Override
    public Mono<CustomerDTO> findCustomerByDocNumber(String docNumber) {

        /*return webClient.get()
                .uri("/api/customer/list/bydocnumber/{docNumber}", docNumber)
                .retrieve()
                .bodyToMono(CustomerDTO.class);*/

        return customerWebClient.get()
                .uri("/api/customer/bydocnumber/{docNumber}", docNumber)
                .retrieve()
                .bodyToMono(CustomerDTO.class);
    }
}
