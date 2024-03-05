package com.bootcamp.ehs.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class ProductDTO {

    private String productId;
    private String codeAccount;
    private String nameAccount;
    private Boolean commission;
    private Boolean limitMovement;
    private Integer limitNumberWithdrawal;
    private Integer limitNumberDeposit;
    private Integer limitAccounts;
    private String typeProduct;

}
