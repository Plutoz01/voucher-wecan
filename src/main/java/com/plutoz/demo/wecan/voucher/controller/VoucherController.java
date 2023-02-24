package com.plutoz.demo.wecan.voucher.controller;

import com.plutoz.demo.wecan.voucher.domain.Voucher;
import com.plutoz.demo.wecan.voucher.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/voucher")
public class VoucherController {
    private record RedemptionRequest(String code){}

    private final VoucherService voucherService;

    @Autowired
    public VoucherController( VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public Iterable<Voucher> getAll() {
        return voucherService.getAll();
    }

    @PostMapping
    public Voucher create(@RequestBody Voucher newEntity) {
        return voucherService.create(newEntity);
    }

    @PutMapping("/{id}")
    public Voucher update(@PathVariable("id") Long id, @RequestBody Voucher updatedEntity) {
        // TODO: validate updatedEntity.id !== pathVariable.id
        return voucherService.update(updatedEntity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        voucherService.delete(id);
    }

    @PostMapping("/redeem")
    public void redeem(@RequestBody RedemptionRequest requestBody) {
        this.voucherService.redeem(requestBody.code);
    }
}
