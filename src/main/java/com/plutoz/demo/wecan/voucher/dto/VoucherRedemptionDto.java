package com.plutoz.demo.wecan.voucher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(name = "VoucherRedemption")
public class VoucherRedemptionDto {
    @Schema(description = """
            Case insensitive code a user must provide during redemption process.
            Can contain only letters, numbers, digits and underscore.
            This value can not be updated.""",
            minLength = 4,
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Pattern(regexp = "\\w{4,100}", message = "4...100 word characters")
    private String code;
}
