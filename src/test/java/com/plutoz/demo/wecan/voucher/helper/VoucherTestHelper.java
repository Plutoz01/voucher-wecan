package com.plutoz.demo.wecan.voucher.helper;

import com.plutoz.demo.wecan.voucher.domain.Voucher;
import com.plutoz.demo.wecan.voucher.dto.VoucherDto;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class VoucherTestHelper {
    public static Voucher.VoucherBuilder getTestVoucher() {
        return Voucher.builder()
                .id(100L)
                .name("Test voucher")
                .code("20-OFF")
                .expiry(Instant.now().plus(1, ChronoUnit.DAYS))
                .redemptionCount(3L)
                .redemptionLimit(5L);
    }

    public static VoucherDto.VoucherDtoBuilder<?,?> getTestVoucherDto(Voucher voucher) {
        return VoucherDto.builder()
                .id(voucher.getId())
                .name(voucher.getName())
                .code(voucher.getCode())
                .expiry(voucher.getExpiry())
                .redemptionCount(voucher.getRedemptionCount())
                .redemptionLimit(voucher.getRedemptionLimit());
    }
}
