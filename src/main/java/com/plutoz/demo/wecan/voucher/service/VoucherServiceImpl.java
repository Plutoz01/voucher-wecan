package com.plutoz.demo.wecan.voucher.service;

import com.plutoz.demo.wecan.voucher.domain.Voucher;
import com.plutoz.demo.wecan.voucher.exception.VoucherIsInvalidException;
import com.plutoz.demo.wecan.voucher.exception.VoucherNotFoundException;
import com.plutoz.demo.wecan.voucher.repository.VoucherRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Service
@Transactional
public class VoucherServiceImpl implements VoucherService {
    private final VoucherRepository repository;
    private final VoucherActivationHandlerService activationHandlerService;

    public VoucherServiceImpl(VoucherRepository repository, VoucherActivationHandlerService activationHandler) {
        this.repository = repository;
        this.activationHandlerService = activationHandler;
    }

    @Override
    public Iterable<Voucher> getAll() {
        return repository.findAll();
    }

    @Override
    public Voucher getById(Long id) {
        return repository.findById(id).orElseThrow(VoucherNotFoundException::new);
    }

    @Override
    public Voucher create(Voucher newEntity) {
        newEntity.setId(null);

        if(repository.existsByCodeIgnoreCase(newEntity.getCode())) {
            throw new VoucherIsInvalidException("Voucher code must be unique.");
        }

        return repository.save(newEntity);
    }

    @Override
    public Voucher update(Voucher updatedEntity) {
        Voucher existing = repository.findById(updatedEntity.getId())
                .orElseThrow(VoucherNotFoundException::new);

        if(!Objects.equals(existing.getCode(), updatedEntity.getCode())) {
            throw new VoucherIsInvalidException("Existing voucher's code is not modifiable.");
        }

        updatedEntity.setRedemptionCount(existing.getRedemptionCount());

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
            throw new VoucherIsInvalidException("Voucher is expired.");
        }

        voucher.setRedemptionCount(voucher.getRedemptionCount() + 1);
        repository.save(voucher);

        this.activationHandlerService.voucherActivated(voucher);
    }

    private boolean isVoucherExpired(Voucher v) {
        return Instant.now().isAfter(v.getExpiry());
    }

    private boolean isVoucherRedemptionCountExhausted(Voucher v) {
        if (v.getRedemptionLimit() == null) {
            return false;
        }
        return v.getRedemptionCount() >= v.getRedemptionLimit();
    }
}
