package com.plutoz.demo.wecan.voucher.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class VoucherIsInvalidException extends RuntimeException {
}
