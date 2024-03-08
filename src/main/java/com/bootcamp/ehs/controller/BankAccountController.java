package com.bootcamp.ehs.controller;

import com.bootcamp.ehs.dto.BankAccountDTO;
import com.bootcamp.ehs.dto.CustomerDTO;
import com.bootcamp.ehs.dto.TransactionDTO;
import com.bootcamp.ehs.dto.TransferDTO;
import com.bootcamp.ehs.models.BankAccount;
import com.bootcamp.ehs.repo.IBankAccountRepo;
import com.bootcamp.ehs.service.IBankWebClientService;
import com.bootcamp.ehs.service.ICustomerWebClientService;
import com.bootcamp.ehs.service.IBankAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class BankAccountController {

    private final IBankAccountService bankAccountService;

    private final ICustomerWebClientService customerWebClient;

    private final IBankWebClientService bankWebClient;

    // Metodo para actualizar una cuenta bancaria (monto y numero de transacciones)
    @PutMapping
    public Mono<ResponseEntity<BankAccount>> actualizarCuentaBancaria(@RequestBody BankAccount bankAccount){
        return bankAccountService.getByAccountId(bankAccount.getId())
                .flatMap(accountExist -> {
                    accountExist.setAmount(bankAccount.getAmount());
                    accountExist.setNumberTransactions(accountExist.getNumberTransactions()+1);
                    log.info("ActualizaCuentabancaria: ", accountExist);
                    return bankAccountService.updateAccount(accountExist);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Metodo para grabar una cuenta bancaria en un determinado banco
    @PostMapping("/withbank/{codeBank}")
    public Mono<ResponseEntity<BankAccount>> crearCuentaBancaria(@PathVariable("codeBank") String codebank,@RequestBody BankAccount bankAccount){
                return bankWebClient.findBankByCode(codebank)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Banco no existe")))
                        .flatMap(bankExist -> {
                            bankAccount.setBank(bankExist);
                            return bankAccountService.saveAccount(bankAccount)
                                    .map(ResponseEntity::ok)
                                    .defaultIfEmpty(ResponseEntity.notFound().build());
                        });
    }

    //Metodo que obtiene las cuentas bancarias de un cliente
    /*@GetMapping("/bycustomer/{customerId}")
    public Flux<ResponseEntity<BankAccount>> obtenerCuentas(@PathVariable String customerId){
        return bankAccountService.findAllByCustomer(customerId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }*/

    @GetMapping("/bycustomer/{customerId}")
    public Mono<ResponseEntity<List<BankAccount>>> obtenerCuentas(@PathVariable String customerId){
        return bankAccountService.findAllByCustomer(customerId)
                .collectList()
                .flatMap(accounts -> accounts.isEmpty() ?
                        Mono.just(ResponseEntity.notFound().build()) :
                        Mono.just(ResponseEntity.ok(accounts)));
    }

    // Metodo para obteber la cuenta con el id.
    @GetMapping("/{accountId}")
    public Mono<BankAccount> getAccountById(@PathVariable("accountId") String accountId){
        return bankAccountService.getByAccountId(accountId);
    }

    // Metodo que obtiene el cliente desde el ms cliente utilizando webclient
    @GetMapping("/customer/{id}")
    public Mono<CustomerDTO> buscarClienteID(@PathVariable String id){
        return customerWebClient.findCustomerById(id);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }


}
