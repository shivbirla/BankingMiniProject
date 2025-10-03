package com.opl.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "CUSTOMER_ADDRESSES",schema = "BANKING_SCHEMA")
@Data
@NoArgsConstructor
public class CustomerAddress {

    // id (BIGINT, PK, AUTO_INCREMENT)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // user_id (BIGINT, FK → users(id), NOT NULL)
    // Many-to-One relationship back to the Customer entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Customer_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Customer customer;

    // address_line1 (VARCHAR(100))
    @Column(name = "address_line1", length = 100)
    private String addressLine1;

    // address_line2 (VARCHAR(100))
    @Column(name = "address_line2", length = 100)
    private String addressLine2;

    // city (VARCHAR(50))
    @Column(name = "city", length = 50)
    private String city;

    // state (VARCHAR(50))
    @Column(name = "state", length = 50)
    private String state;

    // postal_code (VARCHAR(10))
    @Column(name = "postal_code", length = 10)
    private String postalCode;

    // country (VARCHAR(50))
    @Column(name = "country", length = 50)
    private String country;

    // address_type (VARCHAR(20)) e.g., 'HOME', 'OFFICE'
    @Column(name = "address_type", length = 20)
    private String addressType;
}
