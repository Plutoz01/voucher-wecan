package com.plutoz.demo.wecan.voucher.converter;

import com.plutoz.demo.wecan.voucher.domain.Voucher;
import com.plutoz.demo.wecan.voucher.dto.VoucherDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class VoucherConverter {
    private final ModelMapper modelMapper;

    public VoucherConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public VoucherDto toDto(Voucher v) {
        return modelMapper.map(v, VoucherDto.class);
    }

    public Voucher toModel(VoucherDto dto) {
        return modelMapper.map(dto, Voucher.class);
    }
}
