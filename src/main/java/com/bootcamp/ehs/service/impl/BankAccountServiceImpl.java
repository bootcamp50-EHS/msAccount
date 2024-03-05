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

        return customerService.findCustomerById(bankAccount.getCustomerId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Cliente no existe")))
                .flatMap(customer ->
                        customer.getTypeCustomer().equals("Empresarial") ? saveBusinessAccount(bankAccount) :
                                customer.getTypeCustomer().equals("Personal") ? savePersonalAccount(bankAccount) :
                                        Mono.error(new IllegalArgumentException("Tipo no Soportado"))
                )
                .switchIfEmpty(Mono.empty());

        //-- TAMBIEN FUNCIONA
        /*return customerService.findCustomerById(bankAccount.getCustomerId())
                .flatMap(customer -> {
                   if (customer.getTypeCustomer().equals("Empresarial")){
                       return productService.findProductById(bankAccount.getProductId())
                               .filter(product -> product.getCodeAccount().equals("02") )
                               .doOnNext(account -> log.info("grabando Empresarial"))
                               .flatMap(account -> bankAccountRepo.save(bankAccount));
                   }else{
                       return  countByCustomerIdAndTypeProduct(bankAccount)
                               .doOnNext(count -> log.info("Numero de cuentas"+count))
                               .filter(count -> count == 0)
                               .doOnNext(account -> log.info("grabando Personal"))
                               .flatMap(account -> bankAccountRepo.save(bankAccount));
                   }
                })
                .switchIfEmpty(Mono.empty());*/

    }

    private Mono<BankAccount> saveBusinessAccount(BankAccount bankAccount) {
        log.info("Proceso de Cuenta Empresarial");
        return hasLeastOneOwner(bankAccount)
                .filter(hasTitular -> hasTitular)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("La cuenta debe tener al menos un titular")))
                .then(productService.findProductById(bankAccount.getProductId()))
                .filter(product -> product.getTypeProduct().equals("Pasivo") && product.getCodeAccount().equals("02"))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Producto no existe")))
                .doOnNext(account -> log.info("Grabando Cuenta Empresarial"))
                .flatMap(account -> bankAccountRepo.save(bankAccount));
    }

    private Mono<BankAccount> savePersonalAccount(BankAccount bankAccount) {
        return countByCustomerIdAndTypeProduct(bankAccount)
                .doOnNext(count -> log.info("Cliente ya cuenta con una cuenta de este tipo"))
                .filter(count -> count == 0)
                .doOnNext(account -> log.info("Grabando Cuenta Personal"))
                .flatMap(account -> bankAccountRepo.save(bankAccount));
    }

    @Override
    public Mono<BankAccount> updateAccount(String id, BankAccount bankAccount) {
        return null;
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


    @Override
    public Mono<Boolean> hasLeastOneOwner(BankAccount bankAccount) {
        return Mono.just(!bankAccount.getHolder().isEmpty());
    }

    @Override
    public Mono<BankAccount> doDeposit(TransactionDTO transaction) {
        log.info("Procesando Deposito");
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new IllegalArgumentException("El importe de la transacción debe ser mayor a 0"));
        }

        return bankAccountRepo.findById(transaction.getAccountId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("La cuenta ingresada no existe")))
                .flatMap(account -> {
                    account.setAmount(account.getAmount().add(transaction.getAmount()));
                    return bankAccountRepo.save(account)
                            .doOnNext(accountUpdate -> log.info("Cuenta Bancaria Actualizada"))
                            .flatMap(accountUpdate -> {
                                return transactionService.saveTransaction(transaction)
                                        .thenReturn(accountUpdate);
                            });

                })
                .onErrorResume(e -> {
                    return Mono.error(new RuntimeException("Ocurrió un error al registrar el depósito: " + e.getMessage(), e));
                });

    }

    @Override
    public Mono<BankAccount> doWithdrawal(TransactionDTO transaction) {
        log.info("Procesando Retiro");
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new IllegalArgumentException("El importe de la transacción debe ser mayor a 0"));
        }

        return bankAccountRepo.findById(transaction.getAccountId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("La cuenta ingresada no existe")))
                .filter(account -> account.getAmount().compareTo(transaction.getAmount()) >= 0)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Saldo insuficiente para realizar el retiro")))
                .flatMap(account -> {
                    account.setAmount(account.getAmount().subtract(transaction.getAmount()));
                    return bankAccountRepo.save(account)
                            .doOnNext(accountUpdate -> log.info("Cuenta Bancaria Actualizada"))
                            .flatMap(accountUpdate -> {
                                return transactionService.saveTransaction(transaction)
                                        .thenReturn(accountUpdate);
                            });

                })
                .onErrorResume(e -> {
                    return Mono.error(new RuntimeException("Ocurrió un error al registrar el depósito: " + e.getMessage(), e));
                });
    }

    @Override
    public Mono<BankAccount> doTransferBetweenAccounts(TransferDTO transfer) {

        return bankAccountRepo.findById(transfer.getFromAccountId())
                .filter(accountFrom -> accountFrom.getAmount().compareTo(transfer.getAmount())> 0)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Fondos insuficientes en la cuenta origen")))
                .flatMap(accountFrom -> bankAccountRepo.findById(transfer.getToAccountId())
                        .flatMap(accountTo -> {
                            accountFrom.setAmount(accountFrom.getAmount().subtract(transfer.getAmount()));
                            accountTo.setAmount(accountTo.getAmount().add(transfer.getAmount()));
                            TransactionDTO retiro = new TransactionDTO(null,transfer.getCodeOperation(),transfer.getDescription(),transfer.getDateTransfer(),
                                    transfer.getAmount(),"Retiro",transfer.getFromAccountId());
                            TransactionDTO deposito = new TransactionDTO(null,transfer.getCodeOperation(),transfer.getDescription(),transfer.getDateTransfer(),
                                    transfer.getAmount(),"Deposito",transfer.getToAccountId());
                            return transactionService.saveTransaction(retiro)
                                    .then(transactionService.saveTransaction(deposito))
                                    .then(bankAccountRepo.save(accountFrom))
                                    .then(bankAccountRepo.save(accountTo))
                                    .thenReturn(accountFrom);
                        }));

    }


    // metodos de apoyo

    private Mono<Long> countByCustomerIdAndTypeProduct(BankAccount bankAccount) {

        return findAllByCustomer(bankAccount.getCustomerId())
                .doOnNext(account -> log.info("Obteniendo todas las cuentas por cliente"))
                .filter(account -> account.getProductId().equals(bankAccount.getProductId()) )
                .count();

    }

}
