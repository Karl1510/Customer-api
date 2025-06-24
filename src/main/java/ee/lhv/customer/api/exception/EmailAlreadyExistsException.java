package ee.lhv.customer.api.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    
    public EmailAlreadyExistsException(String email) {
        super("Customer with email '" + email + "' already exists");
    }
}