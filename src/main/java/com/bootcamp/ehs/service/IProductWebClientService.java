package com.bootcamp.ehs.service;

import com.bootcamp.ehs.dto.CustomerDTO;
import com.bootcamp.ehs.dto.ProductDTO;
import reactor.core.publisher.Mono;

public interface IProductWebClientService {

    Mono<ProductDTO> findProductById(String id);

    Mono<ProductDTO> findProductByCode(String codeAccount);
}
