package com.plutoz.demo.wecan.voucher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;

@Data
@Schema(name = "Voucher")
public class VoucherDto {
    private Long id;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Code is mandatory")
    private String code;

    @Future
    private Instant expiry;

    @Positive
    private Long redemptionLimit;

    private Long redemptionCount;
}
