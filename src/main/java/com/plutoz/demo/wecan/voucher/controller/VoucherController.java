package com.plutoz.demo.wecan.voucher.controller;

import com.plutoz.demo.wecan.voucher.domain.Voucher;
import com.plutoz.demo.wecan.voucher.dto.VoucherDto;
import com.plutoz.demo.wecan.voucher.dto.VoucherRedemptionDto;
import com.plutoz.demo.wecan.voucher.service.VoucherService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/voucher")
public class VoucherController {
    private final VoucherService voucherService;
    private final ModelMapper modelMapper;

    @Autowired
    public VoucherController(VoucherService voucherService, ModelMapper modelMapper) {
        this.voucherService = voucherService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<VoucherDto> getAll() {
        return StreamSupport.stream(voucherService.getAll().spliterator(), false)
                .map(this::toDto)
                .toList();
    }

    @PostMapping
    public VoucherDto create(@Valid @RequestBody VoucherDto dto) {
        return toDto(voucherService.create(toModel(dto)));
    }

    @PutMapping("/{id}")
    public VoucherDto update(@PathVariable("id") Long id, @Valid @RequestBody VoucherDto dto) {
        if(!Objects.equals(id, dto.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Path variable id and request body id differs");
        }
        return toDto(voucherService.update(toModel(dto)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        voucherService.delete(id);
    }

    @PostMapping("/redeem")
    public void redeem(@RequestBody VoucherRedemptionDto dto) {
        this.voucherService.redeem(dto.code());
    }

    private VoucherDto toDto(Voucher v) {
        return modelMapper.map(v, VoucherDto.class);
    }

    private Voucher toModel(VoucherDto dto) {
        return modelMapper.map(dto, Voucher.class);
    }
}
