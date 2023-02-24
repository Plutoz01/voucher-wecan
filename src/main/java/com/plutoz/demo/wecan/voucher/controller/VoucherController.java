package com.plutoz.demo.wecan.voucher.controller;

import com.plutoz.demo.wecan.voucher.domain.Voucher;
import com.plutoz.demo.wecan.voucher.dto.VoucherDto;
import com.plutoz.demo.wecan.voucher.dto.VoucherRedemptionDto;
import com.plutoz.demo.wecan.voucher.exception.VoucherIsInvalidException;
import com.plutoz.demo.wecan.voucher.service.VoucherService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

    @Operation(summary = "Returns the collection of existing vouchers.")
    @GetMapping
    public List<VoucherDto> getAll() {
        return StreamSupport.stream(voucherService.getAll().spliterator(), false)
                .map(this::toDto)
                .toList();
    }

    @Operation(summary = "Creates new voucher.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VoucherDto create(@NotNull @Valid @RequestBody final VoucherDto dto) {
        return toDto(voucherService.create(toModel(dto)));
    }

    @Operation(summary = "Updates an existing voucher.")
    @PutMapping("/{id}")
    public VoucherDto update(@PathVariable("id") final Long id,
                             @NotNull @Valid @RequestBody final VoucherDto dto) {
        if (!Objects.equals(id, dto.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Path variable id and request body id differs");
        }
        return toDto(voucherService.update(toModel(dto)));
    }

    @Operation(summary = "Deletes an existing voucher by id.")
    @DeleteMapping("/{id}")
    public void delete(@NotNull @PathVariable("id") final Long id) {
        voucherService.delete(id);
    }

    @Operation(summary = "Redeems a voucher by code.")
    @PostMapping("/redeem")
    public void redeem(@NotNull @Valid @RequestBody final VoucherRedemptionDto dto) {
        this.voucherService.redeem(dto.code());
    }

    @ExceptionHandler(VoucherIsInvalidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String handleVoucherIsInvalid(Throwable ex) {
        return ex.getMessage();
    }

    private VoucherDto toDto(Voucher v) {
        return modelMapper.map(v, VoucherDto.class);
    }

    private Voucher toModel(VoucherDto dto) {
        return modelMapper.map(dto, Voucher.class);
    }
}
