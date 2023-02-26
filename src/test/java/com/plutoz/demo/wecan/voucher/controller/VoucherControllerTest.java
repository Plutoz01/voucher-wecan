package com.plutoz.demo.wecan.voucher.controller;

import com.plutoz.demo.wecan.voucher.converter.VoucherConverter;
import com.plutoz.demo.wecan.voucher.domain.Voucher;
import com.plutoz.demo.wecan.voucher.exception.VoucherIsInvalidException;
import com.plutoz.demo.wecan.voucher.exception.VoucherNotFoundException;
import com.plutoz.demo.wecan.voucher.helper.VoucherTestHelper;
import com.plutoz.demo.wecan.voucher.service.VoucherService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class VoucherControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private VoucherService mockVoucherService;
    @MockBean
    private VoucherConverter mockVoucherConverter;

    Voucher testVoucher1;
    Voucher testVoucher2;

    @BeforeEach
    void setUp() {
        testVoucher1 = VoucherTestHelper
                .getTestVoucher()
                .id(1L)
                .name("name 1")
                .code("code-1")
                .build();
        testVoucher2 = VoucherTestHelper
                .getTestVoucher()
                .id(2L)
                .name("name 2")
                .code("code-2")
                .build();
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(mockVoucherService);
    }

    @Test
    void getAll_shouldReturnEmptyArray_whenNoExistingVoucherPresent() throws Exception {
        mockGetAllVouchers();

        this.mockMvc.perform(get("/voucher"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(mockVoucherService, times(1)).getAll();
    }

    @Test
    void getAll_shouldReturnVouchersConvertedAsDto_whenThereAreExistingVouchers() throws Exception {
        mockGetAllVouchers(testVoucher1, testVoucher2);
        mockEntityToDtoConversion(testVoucher1);
        mockEntityToDtoConversion(testVoucher2);

        this.mockMvc.perform(get("/voucher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(testVoucher1.getId().intValue())))
                .andExpect(jsonPath("$.[0].name", is(testVoucher1.getName())))
                .andExpect(jsonPath("$.[0].code", is(testVoucher1.getCode())))
                .andExpect(jsonPath("$.[1].id", is(testVoucher2.getId().intValue())))
                .andExpect(jsonPath("$.[1].name", is(testVoucher2.getName())))
                .andExpect(jsonPath("$.[1].code", is(testVoucher2.getCode())));
        verify(mockVoucherService, times(1)).getAll();
    }

    @Test
    void getById_shouldReturnResponseStatus404_whenNoVoucherFondById() throws Exception {
        final Long nonExistingId = 123L;
        doThrow(VoucherNotFoundException.class).when(mockVoucherService).getById(nonExistingId);

        this.mockMvc.perform(get(String.format("/voucher/%d", nonExistingId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(is(emptyOrNullString())));
        verify(mockVoucherService, times(1)).getById(nonExistingId);
    }

    @Test
    void getById_shouldReturnExpectedVoucher_whenExpectedVoucherExists() throws Exception {
        doReturn(testVoucher1).when(mockVoucherService).getById(testVoucher1.getId());
        mockEntityToDtoConversion(testVoucher1);

        this.mockMvc.perform(get(String.format("/voucher/%d", testVoucher1.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testVoucher1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(testVoucher1.getName())))
                .andExpect(jsonPath("$.code", is(testVoucher1.getCode())));
        verify(mockVoucherService, times(1)).getById(testVoucher1.getId());
    }

    @Test
    void create_shouldCreateAndReturnNewVoucher_whenVoucherCreationWasSuccess() throws Exception {
        final String postBody = """
                {
                    "name": "%s",
                    "code": "%s"
                }
                """.formatted(testVoucher1.getName(), testVoucher1.getCode());
        doReturn(testVoucher1).when(mockVoucherConverter).toModel(any());
        doReturn(testVoucher2).when(mockVoucherService).create(testVoucher1);
        mockEntityToDtoConversion(testVoucher2);

        this.mockMvc.perform(post("/voucher")
                        .content(postBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(testVoucher2.getId().intValue())))
                .andExpect(jsonPath("$.name", is(testVoucher2.getName())))
                .andExpect(jsonPath("$.code", is(testVoucher2.getCode())));
        verify(mockVoucherService, times(1)).create(testVoucher1);
    }

    @Test
    void create_shouldReturn400BadRequest_whenServiceThrowsException() throws Exception {
        final String expectedError = "test error message";
        final String postBody = """
                {
                    "name": "%s",
                    "code": "%s"
                }
                """.formatted(testVoucher1.getName(), testVoucher1.getCode());
        doReturn(testVoucher1).when(mockVoucherConverter).toModel(any());
        mockDtoToEntityConversion(testVoucher1);
        doThrow(new VoucherIsInvalidException(expectedError)).when(mockVoucherService).create(testVoucher1);

        this.mockMvc.perform(post("/voucher")
                        .content(postBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedError));
        verify(mockVoucherService, times(1)).create(testVoucher1);
    }

    @Test
    void update_shouldReturnUpdatedVoucher_whenVoucherUpdateWasSuccess() throws Exception {
        final String putBody = """
                {
                    "id": "%d",
                    "name": "%s",
                    "code": "%s"
                }
                """.formatted(testVoucher1.getId(), testVoucher1.getName(), testVoucher1.getCode());
        mockDtoToEntityConversion(testVoucher1);
        doReturn(testVoucher2).when(mockVoucherService).update(testVoucher1);
        mockEntityToDtoConversion(testVoucher2);

        this.mockMvc.perform(put(String.format("/voucher/%d", testVoucher1.getId()))
                        .content(putBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testVoucher2.getId().intValue())))
                .andExpect(jsonPath("$.name", is(testVoucher2.getName())))
                .andExpect(jsonPath("$.code", is(testVoucher2.getCode())));
        verify(mockVoucherService, times(1)).update(testVoucher1);
    }

    @Test
    void update_shouldReturn400BadRequest_whenIdDiffersInPathAndRequestBody() throws Exception {
        final String putBody = """
                {
                    "id": "%d",
                    "name": "%s",
                    "code": "%s"
                }
                """.formatted(testVoucher1.getId(), testVoucher1.getName(), testVoucher1.getCode());
        final Long differentId = testVoucher1.getId() + 1L;
        mockDtoToEntityConversion(testVoucher1);
        doReturn(testVoucher2).when(mockVoucherService).update(testVoucher1);
        mockEntityToDtoConversion(testVoucher2);

        this.mockMvc.perform(put(String.format("/voucher/%d", differentId))
                        .content(putBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(is("Identifier differs in path and request body.")));
    }

    @Test
    void update_shouldReturn400BadRequest_whenServiceThrowsVoucherIsInvalidException() throws Exception {
        final String expectedError = "test error message";
        final String putBody = """
                {
                    "id": "%d",
                    "name": "%s",
                    "code": "%s"
                }
                """.formatted(testVoucher1.getId(), testVoucher1.getName(), testVoucher1.getCode());
        mockDtoToEntityConversion(testVoucher1);
        doThrow(new VoucherIsInvalidException(expectedError)).when(mockVoucherService).update(testVoucher1);

        this.mockMvc.perform(put(String.format("/voucher/%d", testVoucher1.getId()))
                        .content(putBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedError));
        verify(mockVoucherService, times(1)).update(testVoucher1);
    }

    @Test
    void update_shouldReturn404NotFound_whenServiceThrowsVoucherNotFoundException() throws Exception {
        final String putBody = """
                {
                    "id": "%d",
                    "name": "%s",
                    "code": "%s"
                }
                """.formatted(testVoucher1.getId(), testVoucher1.getName(), testVoucher1.getCode());
        mockDtoToEntityConversion(testVoucher1);
        doThrow(VoucherNotFoundException.class).when(mockVoucherService).update(testVoucher1);

        this.mockMvc.perform(put(String.format("/voucher/%d", testVoucher1.getId()))
                        .content(putBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(is(emptyOrNullString())));
        verify(mockVoucherService, times(1)).update(testVoucher1);
    }

    @Test
    void delete_shouldReturn204NoContent_whenDeleteWasSuccess() throws Exception {
        final Long idToDelete = 123L;

        this.mockMvc.perform(delete(String.format("/voucher/%d", idToDelete)))
                .andExpect(status().isNoContent());
        verify(mockVoucherService, times(1)).delete(idToDelete);
    }

    @Test
    void redeem_shouldReturn200Ok_whenRedemptionWasSuccess() throws Exception {
        final String testCode = "code-123";
        final String postBody = """
                { "code": "%s" }
                """.formatted(testCode);


        this.mockMvc.perform(post("/voucher/redeem")
                        .content(postBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(is(emptyOrNullString())));
        verify(mockVoucherService, times(1)).redeem(testCode);
    }

    @Test
    void redeem_shouldReturn400BadRequest_whenServiceThrowsVoucherIsInvalidException() throws Exception {
        final String expectedError = "test error message";
        final String testCode = "code-123";
        final String postBody = """
                { "code": "%s" }
                """.formatted(testCode);
        doThrow(new VoucherIsInvalidException(expectedError)).when(mockVoucherService).redeem(testCode);

        this.mockMvc.perform(post("/voucher/redeem")
                        .content(postBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(is(expectedError)));
        verify(mockVoucherService, times(1)).redeem(testCode);
    }

    @Test
    void redeem_shouldReturn400BadRequest_whenServiceThrowsVoucherNotFoundException() throws Exception {
        final String testCode = "code-123";
        final String postBody = """
                { "code": "%s" }
                """.formatted(testCode);
        doThrow(VoucherNotFoundException.class).when(mockVoucherService).redeem(testCode);

        this.mockMvc.perform(post("/voucher/redeem")
                        .content(postBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(is(emptyOrNullString())));
        verify(mockVoucherService, times(1)).redeem(testCode);
    }


    private void mockGetAllVouchers(Voucher... expected) {
        doReturn(List.of(expected)).when(mockVoucherService).getAll();
    }

    private void mockEntityToDtoConversion(Voucher voucher) {
        doReturn(VoucherTestHelper.getTestVoucherDto(voucher).build())
                .when(mockVoucherConverter).toDto(voucher);
    }

    private void mockDtoToEntityConversion(Voucher expected) {
        doReturn(expected).when(mockVoucherConverter).toModel(any());
    }
}