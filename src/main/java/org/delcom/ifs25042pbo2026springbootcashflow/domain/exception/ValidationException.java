package org.delcom.ifs25042pbo2026springbootcashflow.domain.exception;

/** Dilempar saat input atau aturan bisnis domain tidak valid. */
public class ValidationException extends DomainException {
    public ValidationException(String message) {
        super(message);
    }
}
