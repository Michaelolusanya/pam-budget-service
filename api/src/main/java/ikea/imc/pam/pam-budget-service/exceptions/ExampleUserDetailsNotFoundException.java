package org.imc.pam.boilerplate.exceptions;

public class ExampleUserDetailsNotFoundException extends RuntimeException {

    public ExampleUserDetailsNotFoundException(Long id) {
        super(String.format("Example user details with id: %d does not exist", id));
    }
}
