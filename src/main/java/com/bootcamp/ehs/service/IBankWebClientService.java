package com.bootcamp.ehs.service;

import com.bootcamp.ehs.dto.BankDTO;
import reactor.core.publisher.Mono;

public interface IBankWebClientService {

    Mono<BankDTO> findBankByCode(String codeBank);
}
