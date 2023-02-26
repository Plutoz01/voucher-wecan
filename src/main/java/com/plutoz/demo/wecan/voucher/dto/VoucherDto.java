package com.plutoz.demo.wecan.voucher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "Voucher")
public class VoucherDto extends VoucherRedemptionDto {
    private Long id;

    @Schema(description = "User friendly name")
    @NotBlank(message = "Name is mandatory")
    @Size(min = 3, max = 100)
    private String name;

    @Schema(description = "Expire datetime. Can be only a future date.",
            nullable = true,
            minimum = "Must be a future date-time.")
    @Future
    private Instant expiry;

    @Schema(description = """
            Maximum number of redemptions.
            Set null to allow infinite redemptions.""",
            nullable = true,
            minimum = "1")
    @Positive
    private Long redemptionLimit;

    @Schema(description = "Number of redemptions already happened.",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long redemptionCount;
}
