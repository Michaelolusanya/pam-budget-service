package org.imc.pam.boilerplate.exceptions;

public class ExampleUserNotFoundException extends RuntimeException {

    public ExampleUserNotFoundException(Long id) {
        super(String.format("Example user with id: %d does not exist", id));
    }
}
