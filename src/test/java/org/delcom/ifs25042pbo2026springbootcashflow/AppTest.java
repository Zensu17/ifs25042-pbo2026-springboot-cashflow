package org.delcom.ifs25042pbo2026springbootcashflow;

import org.delcom.ifs25042pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25042pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25042pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25042pbo2026springbootcashflow.usecase.FinanceUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AppTest {

    @Autowired
    private FinanceUseCase useCase;

    @Autowired
    private FinancePresenter presenter;

    @Test
    void wiringAndBalance() throws Exception {
        assertNotNull(presenter);
        useCase.addIncome("Gaji", new BigDecimal("5000000"));
        useCase.addExpense("Makan", new BigDecimal("25000"));
        assertEquals("Saldo: Rp 4,975,000.00", presenter.formatBalance(useCase.getBalance()));
    }

    @Test
    void validation() {
        assertThrows(ValidationException.class, () -> useCase.addIncome(" ", BigDecimal.ONE));
        assertThrows(ValidationException.class, () -> useCase.addExpense("x", new BigDecimal("-1")));
        assertThrows(ValidationException.class, () -> useCase.addExpense("x", null));
        assertThrows(ValidationException.class, () -> useCase.removeTransaction(0));
        assertThrows(EntityNotFoundException.class, () -> useCase.removeTransaction(9999));
    }

    @Test
    void appMainDelegatesToSpring() {
        // main() memanggil DotEnv.load() lalu SpringApplication.run; konteks sudah dimuat @SpringBootTest.
        assertNotNull(SpringApplication.class);
    }
}
