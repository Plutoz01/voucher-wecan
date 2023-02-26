package com.plutoz.demo.wecan.voucher.service;

import com.plutoz.demo.wecan.voucher.domain.Voucher;
import com.plutoz.demo.wecan.voucher.exception.VoucherIsInvalidException;
import com.plutoz.demo.wecan.voucher.exception.VoucherNotFoundException;
import com.plutoz.demo.wecan.voucher.helper.VoucherTestHelper;
import com.plutoz.demo.wecan.voucher.repository.VoucherRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoucherServiceImplTest {
    @Mock
    private VoucherRepository mockRepository;
    @Mock
    private VoucherActivationHandlerService mockVoucherActivationHandlerService;
    @InjectMocks
    private VoucherServiceImpl service;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(mockRepository);
        verifyNoMoreInteractions(mockVoucherActivationHandlerService);
    }


    @Nested
    class GetAll {
        @Test
        void shouldReturnAllVouchersProvidedByRepository() {
            final List<Voucher> testVouchers = List.of(
                    VoucherTestHelper.getTestVoucher().id(1L).build(),
                    VoucherTestHelper.getTestVoucher().id(2L).build());
            doReturn(testVouchers).when(mockRepository).findAll();

            final var actual = service.getAll();

            assertEquals(testVouchers, actual);
            verify(mockRepository, times(1)).findAll();
        }
    }

    @Nested
    class GetById {
        final Long voucherId = 123L;

        @Test
        void shouldReturnVoucher_whenVoucherIsPresentInRepository() {
            Voucher voucher = VoucherTestHelper.getTestVoucher().id(voucherId).build();
            mockFindById(voucher);

            final var actual = service.getById(voucherId);

            assertEquals(voucher, actual);
            verify(mockRepository, times(1)).findById(voucherId);
        }

        @Test
        void shouldThrowVoucherNotFoundException_whenVoucherIsNotPresentInRepository() {
            mockFindById(voucherId, null);

            assertThrows(VoucherNotFoundException.class, () -> service.getById(voucherId));
        }
    }

    @Nested
    class Create {
        Voucher newVoucher;
        Voucher persisted;

        @BeforeEach
        void setUp() {
            var builder = VoucherTestHelper.getTestVoucher();
            newVoucher = builder.code("NEW").build();
            persisted = builder.redemptionCount(123L).build();
        }

        @Test
        void shouldPersistPassedVoucherAndReturnSavedEntity_whenSaveWasSuccess() {
            mockRepositoryExistsByCodeIgnoreCase(newVoucher.getCode(), false);
            doReturn(persisted).when(mockRepository).save(newVoucher);

            final var returned = service.create(newVoucher);

            assertEquals(persisted, returned);
            verify(mockRepository, times(1)).existsByCodeIgnoreCase(newVoucher.getCode());
            verify(mockRepository, times(1)).save(newVoucher);
        }

        @Test
        void shouldNullifyIdOfPassedEntityBeforeSave() {
            mockRepositoryExistsByCodeIgnoreCase(newVoucher.getCode(), false);
            doReturn(persisted).when(mockRepository).save(newVoucher);
            assertNotNull(newVoucher.getId());
            final ArgumentCaptor<Voucher> argumentCaptor = ArgumentCaptor.forClass(Voucher.class);

            final var returned = service.create(newVoucher);

            assertNotNull(returned.getId());
            verify(mockRepository, times(1)).existsByCodeIgnoreCase(newVoucher.getCode());
            verify(mockRepository, times(1)).save(argumentCaptor.capture());
            assertNull(argumentCaptor.getValue().getId());
        }

        @Test
        void shouldThrowVoucherIsInvalidException_whenRepositoryHasAlreadyContainAVoucherWithSameCode() {
            mockRepositoryExistsByCodeIgnoreCase(newVoucher.getCode(), true);

            final var thrown = assertThrows(VoucherIsInvalidException.class, () -> service.create(newVoucher));
            assertEquals("Voucher code must be unique.", thrown.getMessage());
        }

        private void mockRepositoryExistsByCodeIgnoreCase(String code, boolean returned) {
            doReturn(returned).when(mockRepository).existsByCodeIgnoreCase(code);
        }
    }

    @Nested
    class Update {
        Voucher.VoucherBuilder builder;
        Voucher existing;
        Voucher modified;
        Voucher persisted;

        @BeforeEach
        void setUp() {
            builder = VoucherTestHelper.getTestVoucher();
            existing = builder.name("existing").build();
            modified = builder.name("modified").build();
            persisted = builder.name("persisted").build();
        }

        @Test
        void shouldPersistPassedVoucherAndReturnSavedEntity_whenUpdateWasSuccess() {
            mockFindById(existing);
            doReturn(persisted).when(mockRepository).save(modified);

            final var returned = service.update(modified);

            assertEquals(persisted, returned);
            verify(mockRepository, times(1)).findById(existing.getId());
            verify(mockRepository, times(1)).save(modified);
        }

        @Test
        void shouldThrowVoucherNotFoundException_whenNoVoucherFoundById() {
            final Long nonExistingId = 321L;
            modified.setId(nonExistingId);
            mockFindById(nonExistingId, null);

            assertThrows(VoucherNotFoundException.class, () -> service.update(modified));
        }

        @Test
        void shouldThrowVoucherIsInvalidException_whenExistingAndNewCodeDiffers() {
            existing.setCode("original");
            modified.setCode("modified");
            mockFindById(existing);

            final var thrown = assertThrows(VoucherIsInvalidException.class, () -> service.update(modified));

            assertEquals("Existing voucher's code is not modifiable.", thrown.getMessage());
        }

        @Test
        void shouldPreserveRedemptionCount_whenVoucherUpdated() {
            final Long expectedRedemptionCount = 123L;
            final Voucher.VoucherBuilder builder = VoucherTestHelper.getTestVoucher();
            final Voucher existing = builder.name("existing").redemptionCount(expectedRedemptionCount).build();
            final Voucher modified = builder.name("modified").redemptionCount(null).build();
            doReturn(Optional.of(existing)).when(mockRepository).findById(existing.getId());
            doReturn(modified).when(mockRepository).save(modified);

            final var returned = service.update(modified);

            assertEquals(expectedRedemptionCount, returned.getRedemptionCount());
            verify(mockRepository, times(1)).findById(existing.getId());
            verify(mockRepository, times(1)).save(modified);
        }
    }

    @Nested
    class Delete {
        @Test
        void shouldDeleteVoucherFromRepositoryById() {
            final Long idToDelete = 123L;

            service.delete(idToDelete);

            verify(mockRepository, times(1)).deleteById(idToDelete);
        }
    }

    @Nested
    class Redeem {
        final String voucherCode = "testCode";

        @Test
        void shouldIncreaseRedemptionCounterAndInvokeVoucherActivationHandler_whenRedemptionWasSuccessful() {
            final Voucher voucher = getValidVoucher()
                    .redemptionCount(99L)
                    .build();
            mockFindByCode(voucher);
            final ArgumentCaptor<Voucher> argumentCaptor = ArgumentCaptor.forClass(Voucher.class);

            service.redeem(voucherCode);

            verify(mockRepository, times(1)).findByCodeIgnoreCase(voucherCode);
            verify(mockRepository, times(1)).save(argumentCaptor.capture());
            assertEquals(argumentCaptor.getValue().getRedemptionCount(), 100L);
            verify(mockVoucherActivationHandlerService, times(1)).voucherActivated(voucher);
        }

        @Test
        void shouldThrowVoucherNotFoundException_whenNoVoucherFoundByCode() {
            mockFindByCode(null);

            assertThrows(VoucherNotFoundException.class, () -> service.redeem(voucherCode));

            verify(mockRepository, times(1)).findByCodeIgnoreCase(voucherCode);
        }

        @Test
        void shouldThrowVoucherIsInvalidException_whenVoucherHasExpiryDateAndExpired() {
            final Voucher expiredVoucher = getValidVoucher()
                    .expiry(Instant.now().minus(1, ChronoUnit.SECONDS))
                    .build();
            mockFindByCode(expiredVoucher);

            final var exception = assertThrows(VoucherIsInvalidException.class, () -> service.redeem(voucherCode));
            assertEquals("Voucher is expired.", exception.getMessage());
        }

        @Test
        void shouldThrowVoucherIsInvalidException_whenVoucherHasRedemptionLimitAndExceeded() {
            final Voucher expiredVoucher = getValidVoucher()
                    .redemptionLimit(10L)
                    .redemptionCount(10L)
                    .build();
            mockFindByCode(expiredVoucher);

            final var exception = assertThrows(VoucherIsInvalidException.class, () -> service.redeem(voucherCode));
            assertEquals("Voucher is expired.", exception.getMessage());
        }

        private Voucher.VoucherBuilder getValidVoucher() {
            return VoucherTestHelper.getTestVoucher()
                    .redemptionLimit(100L)
                    .redemptionCount(99L)
                    .expiry(Instant.now().plus(1, ChronoUnit.SECONDS));
        }

        private void mockFindByCode(Voucher returned) {
            doReturn(Optional.ofNullable(returned)).when(mockRepository).findByCodeIgnoreCase(anyString());
        }
    }

    private void mockFindById(Long id, Voucher returned) {
        doReturn(Optional.ofNullable(returned)).when(mockRepository).findById(id);
    }

    private void mockFindById(Voucher returned) {
        mockFindById(returned.getId(), returned);
    }
}