package com.opl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class TransactionWithCustomerDTO {
    private Long transactionId;
    private String type;
    private BigDecimal amount;
    private OffsetDateTime transactionTime;
    private String description;
    private String accountNumber;

    // Embedded Customer Info
    private String customerFullName;
    private String customerUsername;
    private String customerEmail;
    private String customerPhone;
    private List<CustomerAddressDTO> customerAddresses;
}
