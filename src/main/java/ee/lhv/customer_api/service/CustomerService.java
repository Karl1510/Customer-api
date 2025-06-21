package ee.lhv.customer_api.service;

import ee.lhv.customer_api.dto.CustomerRequest;
import ee.lhv.customer_api.dto.CustomerResponse;
import ee.lhv.customer_api.entity.Customer;
import ee.lhv.customer_api.exception.CustomerNotFoundException;
import ee.lhv.customer_api.exception.EmailAlreadyExistsException;
import ee.lhv.customer_api.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    
    public CustomerResponse createCustomer(CustomerRequest request) {
        log.debug("Creating customer with email: {}", request.getEmail());
        
        // Check if email already exists
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }
        
        Customer customer = new Customer(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail()
        );
        
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created with id: {}", savedCustomer.getId());
        return new CustomerResponse(savedCustomer);
    }
    
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        log.debug("Fetching customer with id: {}", id);
        
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new CustomerNotFoundException(id));
        return new CustomerResponse(customer);
    }
    
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        log.debug("Fetching all customers");
        
        return customerRepository.findAll()
            .stream()
            .map(CustomerResponse::new)
            .collect(Collectors.toList());
    }
    
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        log.debug("Updating customer with id: {}", id);
        
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new CustomerNotFoundException(id));
        
        // Check if email is being changed and if new email already exists
        if (!customer.getEmail().equals(request.getEmail()) && 
            customerRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }
        
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        
        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Customer updated with id: {}", updatedCustomer.getId());
        return new CustomerResponse(updatedCustomer);
    }
    
    public void deleteCustomer(Long id) {
        log.debug("Deleting customer with id: {}", id);
        
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException(id);
        }
        customerRepository.deleteById(id);
        log.info("Customer deleted with id: {}", id);
    }
}