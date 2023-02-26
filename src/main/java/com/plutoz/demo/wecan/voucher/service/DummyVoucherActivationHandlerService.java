package com.plutoz.demo.wecan.voucher.service;

import com.plutoz.demo.wecan.voucher.domain.Voucher;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Log
@Service
public class DummyVoucherActivationHandlerService implements VoucherActivationHandlerService {
    @Override
    public void voucherActivated(Voucher voucher) {
        /*
        Voucher post-processing logic comes here, such as some DB operation, external service invocation, messaging, etc..
        Additional params should be extracted from actual request context (such as userId, cartId, etc...)
         */
        log.info(String.format("Voucher '%s' redeemed.", voucher.getCode()));
    }
}
