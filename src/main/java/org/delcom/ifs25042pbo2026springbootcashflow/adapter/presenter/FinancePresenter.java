package org.delcom.ifs25042pbo2026springbootcashflow.adapter.presenter;

import org.delcom.ifs25042pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25042pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25042pbo2026springbootcashflow.domain.entity.TransactionType;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/** Memformat data domain menjadi teks siap tampil. */
public class FinancePresenter {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** 1000000 -> "Rp 1,000,000.00". */
    public String formatMoney(BigDecimal amount) {
        return String.format(Locale.US, "Rp %,.2f", amount);
    }

    public String formatType(TransactionType type) {
        return type == TransactionType.INCOME ? "Pemasukan" : "Pengeluaran";
    }

    public String formatCreatedAt(Transaction t) {
        return t.getCreatedAt().format(DATE_TIME_FORMAT);
    }

    /** "[1] Gaji | Rp 1,000,000.00 | [Pemasukan]". */
    public String format(Transaction t) {
        return String.format("[%d] %s | %s | [%s]",
                t.getId(), t.getDescription(), formatMoney(t.getAmount()), formatType(t.getType()));
    }

    public String formatBalance(BigDecimal balance) {
        return "Saldo: " + formatMoney(balance);
    }

    public String formatList(List<Transaction> list, String header, String emptyMessage) {
        if (list.isEmpty()) {
            return header + "\n" + emptyMessage;
        }
        return header + "\n" + list.stream().map(this::format).collect(Collectors.joining("\n"));
    }

    public String sortLabel(SortOption option) {
        return switch (option) {
            case AMOUNT_ASC -> "Jumlah terkecil";
            case AMOUNT_DESC -> "Jumlah terbesar";
            case INCOME_FIRST -> "Pemasukan dulu";
            case EXPENSE_FIRST -> "Pengeluaran dulu";
        };
    }

    public String showEmpty() {
        return "- Data transaksi belum tersedia!";
    }

    public String showNotFound() {
        return "- Transaksi tidak ditemukan!";
    }

    public String showAddSuccess(Transaction t) {
        return "Berhasil menambah transaksi: " + format(t);
    }

    public String showRemoveSuccess() {
        return "Berhasil menghapus transaksi.";
    }

    public String showRemoveFailed(int id) {
        return String.format("[!] Gagal menghapus transaksi dengan ID: %d.", id);
    }

    public String showValidationError(String message) {
        return "[!] " + message;
    }
}
