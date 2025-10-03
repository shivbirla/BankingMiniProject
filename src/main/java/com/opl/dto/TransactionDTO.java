package com.opl.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class TransactionDTO {
    private Long id;
    private Long accountId;
    private String type;
    private BigDecimal amount;
    private Instant transactionTime;
    private String description;

}
