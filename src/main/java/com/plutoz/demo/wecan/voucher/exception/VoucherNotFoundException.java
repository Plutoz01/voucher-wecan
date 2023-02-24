package com.plutoz.demo.wecan.voucher.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class VoucherNotFoundException extends RuntimeException {
}
