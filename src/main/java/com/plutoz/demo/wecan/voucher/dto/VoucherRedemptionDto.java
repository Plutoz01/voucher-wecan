package com.plutoz.demo.wecan.voucher.dto;

import jakarta.validation.constraints.NotBlank;

public record VoucherRedemptionDto(@NotBlank String code) {
}
