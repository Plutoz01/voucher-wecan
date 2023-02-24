package com.plutoz.demo.wecan.voucher.service;

import com.plutoz.demo.wecan.voucher.domain.Voucher;

public interface VoucherService {
    Iterable<Voucher> getAll();
    Voucher create(Voucher newEntity);
    Voucher update(Voucher updatedEntity);
    void delete(Long id);
    void redeem(String code);
}
