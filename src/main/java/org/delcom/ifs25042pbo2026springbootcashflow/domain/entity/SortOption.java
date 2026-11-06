package org.delcom.ifs25042pbo2026springbootcashflow.domain.entity;

import java.util.Comparator;

/** Kriteria pengurutan transaksi; setiap opsi membawa comparator-nya sendiri. */
public enum SortOption {
    /** Jumlah terkecil dulu. */
    AMOUNT_ASC(Comparator.comparing(Transaction::getAmount)),

    /** Jumlah terbesar dulu. */
    AMOUNT_DESC(Comparator.comparing(Transaction::getAmount).reversed()),

    /** Pemasukan dulu (urutan enum: INCOME sebelum EXPENSE). */
    INCOME_FIRST(Comparator.comparing(Transaction::getType)),

    /** Pengeluaran dulu. */
    EXPENSE_FIRST(Comparator.comparing(Transaction::getType).reversed());

    private final Comparator<Transaction> comparator;

    SortOption(Comparator<Transaction> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Transaction> comparator() {
        return comparator;
    }
}
