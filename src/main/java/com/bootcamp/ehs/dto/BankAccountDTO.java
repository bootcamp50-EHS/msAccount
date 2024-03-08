package com.bootcamp.ehs.dto;

import com.bootcamp.ehs.models.Holder;
import lombok.Data;

import java.util.List;

@Data
public class BankAccountDTO {

    private String id;
    private BankDTO bank;
    private CustomerDTO customer;
    private ProductDTO product;
    private String accountNumber;
    private String creationDate;
    private Float amount;
    private List<Holder> holder;
    private List<String> authorizedSsignatories;
}
