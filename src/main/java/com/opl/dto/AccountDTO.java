package com.opl.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class AccountDTO {
    private String accountType;
    private BigDecimal balance;
}
