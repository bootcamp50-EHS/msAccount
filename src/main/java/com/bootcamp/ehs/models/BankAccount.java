package com.bootcamp.ehs.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="CustomerAccount")
public class BankAccount {

    @Id
    private String id;
    @NonNull
    private String customerId;
    @NonNull
    private String productId;
    private String accountNumber;
    private String creationDate;
    private BigDecimal amount;
    private List<Holder> holder;
    private List<String> authorizedSsignatories;

}
