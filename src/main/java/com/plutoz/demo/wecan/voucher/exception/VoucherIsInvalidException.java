package com.plutoz.demo.wecan.voucher.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class VoucherIsInvalidException extends RuntimeException {
    public VoucherIsInvalidException(String message) {
        super(message);
    }
}
