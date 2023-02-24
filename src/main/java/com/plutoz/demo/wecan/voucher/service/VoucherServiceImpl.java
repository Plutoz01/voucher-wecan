package com.plutoz.demo.wecan.voucher.service;

import com.plutoz.demo.wecan.voucher.domain.Voucher;
import com.plutoz.demo.wecan.voucher.repository.VoucherRepository;
import com.plutoz.demo.wecan.voucher.service.exception.VoucherIsInvalidException;
import com.plutoz.demo.wecan.voucher.service.exception.VoucherNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Transactional
public class VoucherServiceImpl implements VoucherService {
    private final VoucherRepository repository;

    public VoucherServiceImpl(VoucherRepository repository) {
        this.repository = repository;
    }

    @Override
    public Iterable<Voucher> getAll() {
        return repository.findAll();
    }

    @Override
    public Voucher create(Voucher newEntity) {
        // TODO: voucher code unique check
        return repository.save(newEntity);
    }

    @Override
    public Voucher update(Voucher updatedEntity) {
        // TODO: voucher code unique check
        return repository.save(updatedEntity);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void redeem(String code) {
        Voucher voucher = repository.findByCodeIgnoreCase(code)
                .orElseThrow(VoucherNotFoundException::new);

        if (isVoucherExpired(voucher)|| isVoucherRedemptionCountExhausted(voucher)) {
            throw new VoucherIsInvalidException();
        }

        voucher.setRedemptionCount(voucher.getRedemptionCount() + 1);
        repository.save(voucher);

        handleVoucherActivation(voucher);
    }

    private boolean isVoucherExpired(Voucher v) {
        return Instant.now().isAfter(v.getExpiry());
    }

    private boolean isVoucherRedemptionCountExhausted(Voucher v) {
        if (v.getRedemptionLimit() == null) {
            return false;
        }
        return v.getRedemptionCount() < v.getRedemptionLimit();
    }

    private void handleVoucherActivation(Voucher voucher) {
        /*
        Voucher post-processing logic comes here, such as some DB operation, external service invocation, messaging, etc..
        Additional params should be extracted from actual request context (such as userId, cartId, etc...)
         */
    }
}
