package com.plutoz.demo.wecan.voucher.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;

@Data
public class VoucherDto {
    private Long id;
    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Code is mandatory")
    private String code;
    // TODO: additional validation needed
    private Instant expiry;
    // TODO: need to check if this would make field mandatory or not
    @Min(value = 0, message = "Redemption limit can not be smaller than 0")
    private Long redemptionLimit;
}
