package com.plutoz.demo.wecan.voucher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "VoucherRedemption")
public record VoucherRedemptionDto(@NotBlank String code) {
}
