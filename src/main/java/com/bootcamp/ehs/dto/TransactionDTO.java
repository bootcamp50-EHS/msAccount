package com.bootcamp.ehs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {

    private String id;
    // Codigo de la operacion
    private String codeOperation;
    //Descripcion de la transaccion
    private String description;
    // Fecha de la transaccion
    private String dateTransaction;
    //Monto de la transaccion
    private BigDecimal amount;
    //Tipo de Transaccion (Deposito o Retiro)
    private String typeTransaction;
    //Cuenta a la cual pertenece la transaccion
    private String accountId;

}
