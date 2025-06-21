package ee.lhv.customer_api.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    
    public EmailAlreadyExistsException(String email) {
        super("Customer with email '" + email + "' already exists");
    }
}