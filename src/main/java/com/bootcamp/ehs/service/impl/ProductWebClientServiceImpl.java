package com.bootcamp.ehs.service.impl;

import com.bootcamp.ehs.dto.CustomerDTO;
import com.bootcamp.ehs.dto.ProductDTO;
import com.bootcamp.ehs.service.IProductWebClientService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductWebClientServiceImpl implements IProductWebClientService {

    @Qualifier("productWebClient")
    private final WebClient productWebClient;

    @Qualifier("gatewayServiceUrl")
    private final WebClient webClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerWebClientServiceImpl.class);

    @Override
    public Mono<ProductDTO> findProductById(String id) {
        LOGGER.info("Gateway -> En findProductById: el id= "+ id);
        return webClient.get()
                .uri("/api/product/list/{id}", id)
                .retrieve()
                .bodyToMono(ProductDTO.class);

      /* return productWebClient.get()
                .uri("/api/product/list/{id}", id)
                .retrieve()
                .bodyToMono(ProductDTO.class);*/
    }

    @Override
    public Mono<ProductDTO> findProductByCode(String codeAccount) {
        LOGGER.info("Gateway -> En findProductByCode: el codeAccount= "+ codeAccount);
        return webClient.get()
                .uri("/api/product/bycode/{codeAccount}", codeAccount)
                .retrieve()
                .bodyToMono(ProductDTO.class);

       /*return productWebClient.get()
                .uri("/api/product/list/bycode/{codeAccount}", codeAccount)
                .retrieve()
                .bodyToMono(ProductDTO.class);*/
    }
}
