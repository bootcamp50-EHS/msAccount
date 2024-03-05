package com.bootcamp.ehs.service;

import com.bootcamp.ehs.dto.CustomerDTO;
import reactor.core.publisher.Mono;

public interface ICustomerWebClientService {

    Mono<CustomerDTO> findCustomerById(String id);

}
