package com.bootcamp.ehs.service.impl;

import com.bootcamp.ehs.dto.*;
import com.bootcamp.ehs.models.BankAccount;
import com.bootcamp.ehs.repo.IBankAccountRepo;
import com.bootcamp.ehs.service.IBankAccountService;
import com.bootcamp.ehs.service.ICustomerWebClientService;
import com.bootcamp.ehs.service.IProductWebClientService;
import com.bootcamp.ehs.service.ITransactionWebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements IBankAccountService {

    private final IBankAccountRepo bankAccountRepo;

    private final ICustomerWebClientService customerService;

    private final IProductWebClientService productService;

    private final ITransactionWebClientService transactionService;


    // Metodo para guardar las cuentas bancarias
    @Override
    public Mono<BankAccount> saveAccount(BankAccount bankAccount) {
        log.info("Procesando registro de Cuenta Bancaria");
        return customerService.findCustomerById(bankAccount.getCustomerId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Cliente no existe")))
                .flatMap(customer ->
                        customer.getTypeCustomer().equals("Empresarial") ? saveBusinessAccount(bankAccount) :
                                customer.getTypeCustomer().equals("Personal") ? savePersonalAccount(bankAccount) :
                                        Mono.error(new IllegalArgumentException("Tipo no Soportado"))
                )
                .switchIfEmpty(Mono.empty());

    }



    private Mono<BankAccount> saveBusinessAccount(BankAccount bankAccount) {
        log.info("Proceso de Cuenta Empresarial");
        return hasLeastOneOwner(bankAccount)
                .filter(hasTitular -> hasTitular)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("La cuenta debe tener al menos un titular")))
                .then(productService.findProductById(bankAccount.getProductId()))
                .filter(product -> product.getTypeProduct().equals("Pasivo") && product.getCodeAccount().equals("02"))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Producto no disponible para Empresarial")))
                .doOnNext(account -> log.info("Grabando Cuenta Empresarial"))
                .flatMap(account -> bankAccountRepo.save(bankAccount));
    }

    private Mono<BankAccount> savePersonalAccount(BankAccount bankAccount) {
        log.info("Proceso de Cuenta Personal");
        return bankAccountRepo.countByCustomerIdAndProductIdAndBank(bankAccount)
                .doOnNext(count -> log.info("Cliente ya cuenta con una cuenta de este tipo en este Banco"))
                .filter(count -> count == 0)
                .doOnNext(account -> log.info("Grabando Cuenta Personal"))
                .flatMap(account -> bankAccountRepo.save(bankAccount));
    }

    @Override
    public Mono<BankAccount> updateAccount(BankAccount bankAccount) {
        return bankAccountRepo.findById(bankAccount.getId())
                .flatMap(account -> bankAccountRepo.save(bankAccount));

    }

    @Override
    public Flux<BankAccount> findAllByCustomer(String customerId) {
        return bankAccountRepo.findAll()
                .filter(account -> account.getCustomerId().equals(customerId));
    }

    @Override
    public Mono<BankAccount> findByAccountNumber(String accountNumber) {

        return null;
    }

    @Override
    public Mono<Long> countByCustomerId(String customerId) {
        return bankAccountRepo.findAll()
                .filter(account -> account.getCustomerId().equals(customerId)).count();
     }


     //metodo para verificar que el cliente empresarial tiene al menos una titular
    @Override
    public Mono<Boolean> hasLeastOneOwner(BankAccount bankAccount) {
        return Mono.just(!bankAccount.getHolder().isEmpty());
    }

    @Override
    public Mono<BankAccount> getByAccountId(String accountId){
        return bankAccountRepo.findById(accountId);
    }


    // metodos de apoyo

    private Mono<Long> countByCustomerIdAndTypeProduct(BankAccount bankAccount) {

        return findAllByCustomer(bankAccount.getCustomerId())
                .doOnNext(account -> log.info("Obteniendo todas las cuentas por cliente"))
                .filter(account -> account.getProductId().equals(bankAccount.getProductId()) )
                .count();

    }

}
