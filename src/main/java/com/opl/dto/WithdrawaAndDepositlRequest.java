package com.opl.dto;

import java.math.BigDecimal;

public record WithdrawaAndDepositlRequest(String accountNumber, BigDecimal amount, String type) {}