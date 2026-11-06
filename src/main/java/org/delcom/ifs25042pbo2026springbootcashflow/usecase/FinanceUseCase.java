package org.delcom.ifs25042pbo2026springbootcashflow.usecase;

import org.delcom.ifs25042pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25042pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25042pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25042pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25042pbo2026springbootcashflow.domain.repository.ITransactionRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Use case keuangan. Tanpa I/O UI: hanya memproses data dan melempar
 * checked exception domain bila operasi tidak valid.
 */
public class FinanceUseCase {
    private final ITransactionRepository repository;

    public FinanceUseCase(ITransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    public Transaction addIncome(String description, BigDecimal amount) throws ValidationException {
        validate(description, amount);
        return repository.saveIncome(description.trim(), amount);
    }

    public Transaction addExpense(String description, BigDecimal amount) throws ValidationException {
        validate(description, amount);
        return repository.saveExpense(description.trim(), amount);
    }

    public void removeTransaction(int id) throws EntityNotFoundException, ValidationException {
        if (id <= 0) {
            throw new ValidationException("ID tidak valid");
        }
        if (!repository.deleteById(id)) {
            throw new EntityNotFoundException(id);
        }
    }

    /** Cari berdasarkan keterangan (case-insensitive). */
    public List<Transaction> searchTransactions(String keyword) {
        String key = keyword.toLowerCase();
        return repository.findBy(t -> t.getDescription().toLowerCase().contains(key));
    }

    public List<Transaction> sortTransactions(SortOption option) {
        return repository.findAll().stream().sorted(option.comparator()).toList();
    }

    public BigDecimal getTotalIncome() {
        return sum(true);
    }

    public BigDecimal getTotalExpense() {
        return sum(false);
    }

    /** Saldo = total pemasukan - total pengeluaran. */
    public BigDecimal getBalance() {
        return repository.findAll().stream()
                .map(Transaction::signedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sum(boolean income) {
        return repository.findAll().stream()
                .filter(t -> t.isIncome() == income)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validate(String description, BigDecimal amount) throws ValidationException {
        if (description == null || description.isBlank()) {
            throw new ValidationException("Keterangan tidak boleh kosong");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new ValidationException("Jumlah harus berupa angka lebih dari 0");
        }
    }
}
