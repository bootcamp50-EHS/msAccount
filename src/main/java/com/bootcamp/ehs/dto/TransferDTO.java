package com.bootcamp.ehs.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferDTO {

    private String codeOperation;
    private String description;
    private String dateTransfer;
    private String fromAccountId;
    private String toAccountId;
    private BigDecimal amount;
}
