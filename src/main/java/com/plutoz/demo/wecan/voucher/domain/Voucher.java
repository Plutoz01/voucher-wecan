package com.plutoz.demo.wecan.voucher.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @Builder.Default
    private Long redemptionCount = 0L;
    @Column
    private Long redemptionLimit;
}
