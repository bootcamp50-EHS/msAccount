package com.bootcamp.ehs.controller;

import com.bootcamp.ehs.dto.BankAccountDTO;
import com.bootcamp.ehs.dto.CustomerDTO;
import com.bootcamp.ehs.dto.TransactionDTO;
import com.bootcamp.ehs.dto.TransferDTO;
import com.bootcamp.ehs.models.BankAccount;
import com.bootcamp.ehs.service.ICustomerWebClientService;
import com.bootcamp.ehs.service.IBankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class BankAccountController {

    private final IBankAccountService bankAccountService;

    private final ICustomerWebClientService customerWebClient;

    // Metodo para registrar una cuenta bancaria
    @PostMapping
    public Mono<ResponseEntity<BankAccount>> crearCuentaBancaria(@RequestBody BankAccount bankAccount){

        return bankAccountService.saveAccount(bankAccount)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/bycustomer/{customerId}")
    public Flux<BankAccount> obtenerCuentas(@PathVariable String customerId){
        return bankAccountService.findAllByCustomer(customerId);
    }

    // Metodo que obtiene el cliente desde el ms cliente utilizando webclient
    @GetMapping("/customer/{id}")
    public Mono<CustomerDTO> buscarClienteID(@PathVariable String id){
        return customerWebClient.findCustomerById(id);
    }

    // Metodo para registrar una transaccion de tipo Deposito
    @PostMapping("/deposit")
    public Mono<BankAccount> registrarDesposito(@RequestBody TransactionDTO transactiion){
        return bankAccountService.doDeposit(transactiion);
    }

    // Metodo para registrar una Transaccion de Retiro
    @PostMapping("/withdrawal")
    public  Mono<BankAccount> registrarRetiro(@RequestBody TransactionDTO transaction){
        return bankAccountService.doWithdrawal(transaction);
    }

    @PostMapping("/transfer")
    public Mono<BankAccount> registrarTransferencia(@RequestBody TransferDTO transfer){
        return bankAccountService.doTransferBetweenAccounts(transfer);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }


}
