package com.plutoz.demo.wecan.voucher.controller;

import com.plutoz.demo.wecan.voucher.converter.VoucherConverter;
import com.plutoz.demo.wecan.voucher.dto.VoucherDto;
import com.plutoz.demo.wecan.voucher.dto.VoucherRedemptionDto;
import com.plutoz.demo.wecan.voucher.exception.VoucherIsInvalidException;
import com.plutoz.demo.wecan.voucher.service.VoucherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Tag(name = "Vouchers")
@RestController
@RequestMapping("/voucher")
public class VoucherController {
    private final VoucherService voucherService;
    private final VoucherConverter voucherConverter;

    @Autowired
    public VoucherController(VoucherService voucherService, VoucherConverter voucherConverter) {
        this.voucherService = voucherService;
        this.voucherConverter = voucherConverter;
    }

    @Operation(summary = "Returns the collection of existing vouchers.")
    @GetMapping
    public List<VoucherDto> getAll() {
        return StreamSupport.stream(voucherService.getAll().spliterator(), false)
                .map(voucherConverter::toDto)
                .toList();
    }

    @Operation(summary = "Get existing voucher by id")
    @GetMapping("/{id}")
    public VoucherDto getById(@PathVariable("id") final Long id) {
        return voucherConverter.toDto(voucherService.getById(id));
    }

    @Operation(summary = "Creates new voucher.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VoucherDto create(@NotNull @Valid @RequestBody final VoucherDto dto) {
        return voucherConverter.toDto(voucherService.create(voucherConverter.toModel(dto)));
    }

    @Operation(summary = "Updates an existing voucher.")
    @PutMapping("/{id}")
    public VoucherDto update(@PathVariable("id") final Long id,
                             @NotNull @Valid @RequestBody final VoucherDto dto) {
        if (!Objects.equals(id, dto.getId())) {
            throw new VoucherIsInvalidException("Identifier differs in path and request body.");
        }
        return voucherConverter.toDto(voucherService.update(voucherConverter.toModel(dto)));
    }

    @Operation(summary = "Deletes an existing voucher by id.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@NotNull @PathVariable("id") final Long id) {
        voucherService.delete(id);
    }

    @Operation(summary = "Redeems a voucher by code.")
    @PostMapping("/redeem")
    @ResponseStatus(HttpStatus.OK)
    public void redeem(@NotNull @Valid @RequestBody final VoucherRedemptionDto dto) {
        this.voucherService.redeem(dto.getCode());
    }

    @ExceptionHandler(VoucherIsInvalidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    private String handleVoucherIsInvalid(Throwable ex) {
        return ex.getMessage();
    }
}
