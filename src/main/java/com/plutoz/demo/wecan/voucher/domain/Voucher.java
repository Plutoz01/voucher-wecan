package com.plutoz.demo.wecan.voucher.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true, updatable = false)
    private String code;
    @Column
    private Instant expiry;
    @Column(nullable = false)
    private Long redemptionCount = 0L;
    @Column
    private Long redemptionLimit;
}
