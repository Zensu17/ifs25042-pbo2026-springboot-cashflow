package org.delcom.ifs25042pbo2026springbootcashflow.domain.exception;

/** Checked exception dasar untuk hierarki domain aplikasi keuangan. */
public class DomainException extends Exception {
    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
