package com.plutoz.demo.wecan.voucher.repository;

import com.plutoz.demo.wecan.voucher.domain.Voucher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoucherRepository extends CrudRepository<Voucher, Long> {
    Optional<Voucher> findByCodeIgnoreCase(String code);
}
