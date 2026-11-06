package org.delcom.ifs25042pbo2026springbootcashflow.framework.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.delcom.ifs25042pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25042pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25042pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25042pbo2026springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25042pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25042pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25042pbo2026springbootcashflow.usecase.FinanceUseCase;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/** Tampilan web Vaadin aplikasi keuangan. Exception domain ditangani di sini lewat Notification. */
@Route("")
@PageTitle("Cashflow")
public class FinanceView extends VerticalLayout {

    private final FinanceUseCase useCase;
    private final FinancePresenter presenter;

    private final Grid<Transaction> grid = new Grid<>(Transaction.class, false);
    private final TextField searchField = new TextField();
    private final Select<SortOption> sortSelect = new Select<>();
    private final Paragraph statusLabel = new Paragraph();
    private final Span emptyState = new Span();
    private final Span totalCount = new Span();
    private final Span totalIncome = new Span();
    private final Span totalExpense = new Span();
    private final Span balance = new Span();

    public FinanceView(FinanceUseCase useCase, FinancePresenter presenter) {
        this.useCase = useCase;
        this.presenter = presenter;

        getElement().setAttribute("role", "main");
        getElement().setAttribute("aria-label", "Cashflow pribadi");
        setSizeFull();
        setPadding(true);
        getStyle().set("background", "#0F172A").set("box-sizing", "border-box");
        configureGrid();
        configureToolbar();
        add(buildHeader(), buildSummaryCards(), buildToolbar(), statusLabel, grid);
        styleGridPanel();
        setFlexGrow(1, grid);
        refreshAll();
    }

    // ---------- komponen ----------

    private Div buildHeader() {
        H1 title = new H1("Cashflow");
        title.getStyle().set("margin", "0").set("color", "#F8FAFC").set("font-size", "1.75rem");
        Span sub = new Span("Catatan pemasukan & pengeluaran pribadi");
        sub.getStyle().set("color", "#94A3B8").set("font-size", "0.9rem");
        return new Div(title, sub);
    }

    private HorizontalLayout buildSummaryCards() {
        // Kartu saldo menonjol (hero); tiga kartu lain lebih kecil.
        Div hero = card("Saldo Saat Ini", balance, "linear-gradient(135deg,#6366F1,#8B5CF6)", "#E0E7FF", "#FFFFFF", 2.2, 2);
        HorizontalLayout cards = new HorizontalLayout(
                hero,
                card("Pemasukan", totalIncome, "#064E3B", "#6EE7B7", "#ECFDF5", 1.35, 1),
                card("Pengeluaran", totalExpense, "#7F1D1D", "#FCA5A5", "#FEF2F2", 1.35, 1),
                card("Transaksi", totalCount, "#1E293B", "#94A3B8", "#F1F5F9", 1.35, 1)
        );
        cards.setWidthFull();
        cards.getStyle().set("align-items", "stretch").set("flex-wrap", "wrap").set("gap", "1rem");
        return cards;
    }

    private Div card(String label, Span value, String background, String captionColor, String valueColor,
                     double valueSize, int grow) {
        Span caption = new Span(label);
        caption.getStyle().set("display", "block").set("font-size", "0.8rem")
                .set("letter-spacing", "0.06em").set("text-transform", "uppercase").set("color", captionColor);
        value.getStyle().set("display", "block").set("margin-top", "0.4rem")
                .set("font-size", valueSize + "rem").set("font-weight", "700").set("color", valueColor);

        Div card = new Div(caption, value);
        card.getStyle()
                .set("flex", String.valueOf(grow)).set("min-width", "0")
                .set("background", background)
                .set("border-radius", "16px")
                .set("padding", "1.25rem 1.4rem")
                .set("box-shadow", "0 8px 24px rgba(0,0,0,0.25)");
        return card;
    }

    private void styleGridPanel() {
        grid.getStyle().set("border-radius", "16px").set("border", "none").set("overflow", "hidden");
        statusLabel.getStyle().set("color", "#94A3B8").set("margin", "0");
    }

    private void configureGrid() {
        grid.addColumn(Transaction::getId).setHeader("ID").setAutoWidth(true).setFlexGrow(0)
                .setSortable(true).setComparator(Comparator.comparing(Transaction::getId));

        grid.addColumn(Transaction::getDescription).setHeader("Keterangan").setFlexGrow(1)
                .setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getDescription, String.CASE_INSENSITIVE_ORDER));

        // Comparator kolom diambil dari domain (SortOption), bukan dibuat ulang di view.
        grid.addComponentColumn(this::amountCell).setHeader("Jumlah").setAutoWidth(true)
                .setSortable(true).setComparator(SortOption.AMOUNT_ASC.comparator());

        grid.addComponentColumn(this::typeBadge).setHeader("Tipe").setAutoWidth(true)
                .setSortable(true).setComparator(SortOption.INCOME_FIRST.comparator());

        grid.addColumn(presenter::formatCreatedAt).setHeader("Waktu").setAutoWidth(true)
                .setSortable(true).setComparator(Comparator.comparing(Transaction::getCreatedAt));

        grid.addComponentColumn(this::deleteButton).setHeader("Tindakan").setAutoWidth(true).setFlexGrow(0);

        grid.setEmptyStateComponent(emptyState);
        grid.setSizeFull();
    }

    private Span amountCell(Transaction t) {
        Span span = new Span((t.isIncome() ? "+ " : "− ") + presenter.formatMoney(t.getAmount()));
        span.getStyle().set("font-weight", "600").set("color", t.isIncome() ? "#16A34A" : "#DC2626");
        return span;
    }

    private Span typeBadge(Transaction t) {
        Span badge = new Span(presenter.formatType(t.getType()));
        badge.getElement().getThemeList().add("badge pill " + (t.isIncome() ? "success" : "error"));
        return badge;
    }

    private Button deleteButton(Transaction t) {
        Button b = new Button("Hapus", VaadinIcon.TRASH.create(), e -> openDeleteDialog(t));
        b.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY_INLINE);
        return b;
    }

    private void configureToolbar() {
        searchField.setPlaceholder("Cari keterangan...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);

        sortSelect.setPlaceholder("Urutkan...");
        sortSelect.setItems(SortOption.values());
        sortSelect.setItemLabelGenerator(presenter::sortLabel);
        sortSelect.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                refreshGrid(useCase.sortTransactions(e.getValue()), "Daftar Transaksi (Terurut)");
            }
        });
    }

    private HorizontalLayout buildToolbar() {
        Button income = new Button("Tambah Pemasukan", VaadinIcon.PLUS.create(),
                e -> openAddDialog(TransactionType.INCOME));
        income.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        Button expense = new Button("Tambah Pengeluaran", VaadinIcon.MINUS.create(),
                e -> openAddDialog(TransactionType.EXPENSE));
        expense.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout toolbar = new HorizontalLayout(
                searchField,
                new Button("Cari", VaadinIcon.SEARCH.create(), e -> search()),
                sortSelect,
                new Button("Muat Ulang", VaadinIcon.REFRESH.create(), e -> refreshAll()),
                income, expense);
        toolbar.setWidthFull();
        toolbar.setFlexGrow(1, searchField);
        toolbar.setAlignItems(FlexComponent.Alignment.END);
        toolbar.getStyle().set("flex-wrap", "wrap").set("gap", "0.75rem")
            .set("background", "#F8FAFC").set("border-radius", "16px").set("padding", "0.75rem 1rem")
                .set("box-sizing", "border-box");
        return toolbar;
    }

    // ---------- dialog ----------

    private void openAddDialog(TransactionType type) {
        TextField description = new TextField("Keterangan");
        description.setWidthFull();
        description.setRequired(true);
        BigDecimalField amount = new BigDecimalField("Jumlah (Rp)");
        amount.setWidthFull();
        amount.setRequired(true);

        Dialog dialog = new Dialog();
        dialog.setWidth("420px");
        dialog.setHeaderTitle(type == TransactionType.INCOME ? "Tambah Pemasukan" : "Tambah Pengeluaran");
        dialog.add(new VerticalLayout(description, amount));

        Button save = new Button("Simpan", VaadinIcon.CHECK.create(), e -> {
            try {
                // BigDecimalField mengembalikan null bila input bukan angka -> ditolak use case.
                Transaction created = type == TransactionType.INCOME
                        ? useCase.addIncome(description.getValue(), amount.getValue())
                        : useCase.addExpense(description.getValue(), amount.getValue());
                showSuccess(presenter.showAddSuccess(created));
                dialog.close();
                refreshAll();
            } catch (ValidationException ex) {
                showError(presenter.showValidationError(ex.getMessage()));
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialog.getFooter().add(new Button("Batal", e -> dialog.close()), save);
        dialog.open();
    }

    private void openDeleteDialog(Transaction t) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Menghapus Transaksi");
        dialog.add(new Paragraph("Yakin ingin menghapus " + presenter.format(t) + "?"));

        Button confirm = new Button("Hapus", VaadinIcon.TRASH.create(), e -> {
            delete(t);
            dialog.close();
        });
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        dialog.getFooter().add(new Button("Batal", e -> dialog.close()), confirm);
        dialog.open();
    }

    // ---------- aksi ----------

    private void delete(Transaction t) {
        try {
            useCase.removeTransaction(t.getId());
            showSuccess(presenter.showRemoveSuccess());
        } catch (EntityNotFoundException ex) {
            showError(presenter.showRemoveFailed(ex.getId()));
        } catch (ValidationException ex) {
            showError(presenter.showValidationError(ex.getMessage()));
        }
        refreshAll();
    }

    private void search() {
        String keyword = searchField.getValue();
        if (keyword.isBlank()) {
            refreshAll();
            return;
        }
        List<Transaction> results = useCase.searchTransactions(keyword);
        emptyState.setText(presenter.showNotFound());
        refreshGrid(results, "Hasil Pencarian: \"" + keyword + "\"");
    }

    private void refreshAll() {
        emptyState.setText(presenter.showEmpty());
        sortSelect.clear();
        searchField.clear();
        refreshGrid(useCase.getAllTransactions(), "Daftar Transaksi");
    }

    private void refreshGrid(List<Transaction> list, String title) {
        grid.setItems(list);
        statusLabel.setText(list.isEmpty() ? title + " — " + emptyState.getText().replaceFirst("^- ", "") : title);
        updateSummary();
    }

    private void updateSummary() {
        totalCount.setText(String.valueOf(useCase.getAllTransactions().size()));
        totalIncome.setText(presenter.formatMoney(useCase.getTotalIncome()));
        totalExpense.setText(presenter.formatMoney(useCase.getTotalExpense()));
        BigDecimal bal = useCase.getBalance();
        balance.setText(presenter.formatMoney(bal));
    }

    private void showSuccess(String message) {
        Notification.show(message, 3000, Notification.Position.BOTTOM_START)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String message) {
        Notification.show(message, 4000, Notification.Position.BOTTOM_START)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
