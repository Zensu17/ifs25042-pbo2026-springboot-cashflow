package org.delcom.ifs25042pbo2026springbootcashflow.config;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.page.Meta;

@Meta(name = "description", content = "Cashflow pribadi untuk mencatat pemasukan, pengeluaran, dan saldo.")
@JavaScript("./connection-indicator-landmark.js")
public class AppShell implements AppShellConfigurator {
}