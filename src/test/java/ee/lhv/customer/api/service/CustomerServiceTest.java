package ee.lhv.customer.api.service;

import ee.lhv.customer.api.dto.CustomerRequest;
import ee.lhv.customer.api.dto.CustomerResponse;
import ee.lhv.customer.api.entity.Customer;
import ee.lhv.customer.api.exception.CustomerNotFoundException;
import ee.lhv.customer.api.exception.EmailAlreadyExistsException;
import ee.lhv.customer.api.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private CustomerRequest testRequest;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer("Test", "Kasutaja", "test.kasutaja@example.com");
        testCustomer.setId(1L);
        testCustomer.setCreatedDtime(LocalDateTime.now());
        testCustomer.setModifiedDtime(LocalDateTime.now());

        testRequest = new CustomerRequest("Test", "Kasutaja", "test.kasutaja@example.com");
    }

    @Test
    void createCustomer_Success() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        CustomerResponse response = customerService.createCustomer(testRequest);

        assertNotNull(response);
        assertEquals("Test", response.getFirstName());
        assertEquals("Kasutaja", response.getLastName());
        assertEquals("test.kasutaja@example.com", response.getEmail());
        verify(customerRepository).existsByEmail("test.kasutaja@example.com");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void createCustomer_EmailAlreadyExists_ThrowsException() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, 
            () -> customerService.createCustomer(testRequest));
        verify(customerRepository).existsByEmail("test.kasutaja@example.com");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void getCustomerById_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        CustomerResponse response = customerService.getCustomerById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test", response.getFirstName());
        verify(customerRepository).findById(1L);
    }

    @Test
    void getCustomerById_NotFound_ThrowsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, 
            () -> customerService.getCustomerById(1L));
        verify(customerRepository).findById(1L);
    }

    @Test
    void getAllCustomers_Success() {
        Customer customer2 = new Customer("Test2", "Kasutaja2", "test2.kasutaja2@example.com");
        customer2.setId(2L);
        when(customerRepository.findAll()).thenReturn(Arrays.asList(testCustomer, customer2));

        List<CustomerResponse> responses = customerService.getAllCustomers();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(customerRepository).findAll();
    }

    @Test
    void updateCustomer_Success() {
        CustomerRequest updateRequest = new CustomerRequest("Test", "Updated", "test.updated@example.com");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.existsByEmail("test.updated@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        CustomerResponse response = customerService.updateCustomer(1L, updateRequest);

        assertNotNull(response);
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void updateCustomer_NotFound_ThrowsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, 
            () -> customerService.updateCustomer(1L, testRequest));
        verify(customerRepository).findById(1L);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_Success() {
        when(customerRepository.existsById(1L)).thenReturn(true);

        customerService.deleteCustomer(1L);

        verify(customerRepository).existsById(1L);
        verify(customerRepository).deleteById(1L);
    }

    @Test
    void deleteCustomer_NotFound_ThrowsException() {
        when(customerRepository.existsById(1L)).thenReturn(false);

        assertThrows(CustomerNotFoundException.class, 
            () -> customerService.deleteCustomer(1L));
        verify(customerRepository).existsById(1L);
        verify(customerRepository, never()).deleteById(1L);
    }
}