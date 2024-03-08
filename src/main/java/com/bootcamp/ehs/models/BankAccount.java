package com.bootcamp.ehs.models;

import com.bootcamp.ehs.dto.BankDTO;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection="CustomerAccount")
public class BankAccount {

    @Id
    private String id;
    @NonNull
    private BankDTO bank;
    @NonNull
    private String customerId;
    @NonNull
    private String productId;
    private String accountNumber;
    private String creationDate;
    @Builder.Default
    private BigDecimal amount = BigDecimal.valueOf(0);
    @Builder.Default
    private Integer numberTransactions =0;
    private List<String> holder;
    private List<String> authorizedSsignatories;

}
