package org.poo.game;

class InsufficientManaException extends Exception {
    InsufficientManaException(final String message) {
        super(message);
    }
}

class RowFullException extends Exception {
    RowFullException(final String message) {
        super(message);
    }
}

class InvalidRowSelectionException extends Exception {
    public InvalidRowSelectionException(String message) {
        super(message);
    }
}

class CardNotFoundException extends Exception {
    CardNotFoundException(final String message) {
        super(message);
    }
}

class SameSideAttackException extends Exception {
    SameSideAttackException(final String message) {
        super(message);
    }
}

class CardAlreadyAttackedException extends Exception {
    CardAlreadyAttackedException(final String message) {
        super(message);
    }
}

class HeroAlreadyAttackedException extends Exception {
    public HeroAlreadyAttackedException(String message) {
        super(message);
    }
}

class CardFrozenException extends Exception {
    CardFrozenException(final String message) {
        super(message);
    }
}

class InvalidTargetException extends Exception {
    InvalidTargetException(final String message) {
        super(message);
    }
}

public class ExceptionHandler {
}
