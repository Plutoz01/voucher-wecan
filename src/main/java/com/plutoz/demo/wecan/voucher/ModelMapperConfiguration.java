package com.plutoz.demo.wecan.voucher;

import com.plutoz.demo.wecan.voucher.domain.Voucher;
import com.plutoz.demo.wecan.voucher.dto.VoucherDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        var modelMapper = new ModelMapper();

        TypeMap<VoucherDto, Voucher> propertyMapper = modelMapper.createTypeMap(VoucherDto.class, Voucher.class);
        propertyMapper.addMappings(mapper -> mapper.skip(Voucher::setRedemptionCount));

        return modelMapper;
    }
}
