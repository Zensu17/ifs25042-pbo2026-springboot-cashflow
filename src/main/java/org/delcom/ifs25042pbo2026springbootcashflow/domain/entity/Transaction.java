package org.delcom.ifs25042pbo2026springbootcashflow.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity inti: satu transaksi keuangan.
 * Berada di layer domain — bebas dari urusan tampilan maupun penyimpanan.
 */
public class Transaction {
    /** ID unik, tidak boleh diubah setelah dibuat. */
    private final int id;

    private String description;

    /** Selalu positif; arah (tambah/kurang saldo) ditentukan oleh {@link #type}. */
    private BigDecimal amount;

    private final TransactionType type;

    private final LocalDateTime createdAt;

    public Transaction(int id, String description, BigDecimal amount, TransactionType type) {
        this(id, description, amount, type, LocalDateTime.now());
    }

    public Transaction(int id, String description, BigDecimal amount, TransactionType type, LocalDateTime createdAt) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }

    /** Pengaruh transaksi ke saldo: +amount untuk pemasukan, -amount untuk pengeluaran. */
    public BigDecimal signedAmount() {
        return isIncome() ? amount : amount.negate();
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changeAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
