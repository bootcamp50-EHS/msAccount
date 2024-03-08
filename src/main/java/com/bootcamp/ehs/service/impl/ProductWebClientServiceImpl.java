package com.bootcamp.ehs.service.impl;

import com.bootcamp.ehs.dto.CustomerDTO;
import com.bootcamp.ehs.dto.ProductDTO;
import com.bootcamp.ehs.service.IProductWebClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductWebClientServiceImpl implements IProductWebClientService {

    @Qualifier("productWebClient")
    private final WebClient productWebClient;
    @Override
    public Mono<ProductDTO> findProductById(String id) {
        return productWebClient.get()
                .uri("/product/{id}", id)
                .retrieve()
                .bodyToMono(ProductDTO.class);
    }

    @Override
    public Mono<ProductDTO> findProductByCode(String codeAccount) {
        return productWebClient.get()
                .uri("/product/bycode/{codeAccount}", codeAccount)
                .retrieve()
                .bodyToMono(ProductDTO.class);
    }
}
