package ee.lhv.customer_api.service;

import ee.lhv.customer_api.dto.CustomerRequest;
import ee.lhv.customer_api.dto.CustomerResponse;
import ee.lhv.customer_api.entity.Customer;
import ee.lhv.customer_api.exception.CustomerNotFoundException;
import ee.lhv.customer_api.exception.EmailAlreadyExistsException;
import ee.lhv.customer_api.repository.CustomerRepository;
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
        testCustomer = new Customer("John", "Doe", "john.doe@example.com");
        testCustomer.setId(1L);
        testCustomer.setCreatedDtime(LocalDateTime.now());
        testCustomer.setModifiedDtime(LocalDateTime.now());

        testRequest = new CustomerRequest("John", "Doe", "john.doe@example.com");
    }

    @Test
    void createCustomer_Success() {
        // Given
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // When
        CustomerResponse response = customerService.createCustomer(testRequest);

        // Then
        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("john.doe@example.com", response.getEmail());
        verify(customerRepository).existsByEmail("john.doe@example.com");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void createCustomer_EmailAlreadyExists_ThrowsException() {
        // Given
        when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThrows(EmailAlreadyExistsException.class, 
            () -> customerService.createCustomer(testRequest));
        verify(customerRepository).existsByEmail("john.doe@example.com");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void getCustomerById_Success() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        // When
        CustomerResponse response = customerService.getCustomerById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John", response.getFirstName());
        verify(customerRepository).findById(1L);
    }

    @Test
    void getCustomerById_NotFound_ThrowsException() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CustomerNotFoundException.class, 
            () -> customerService.getCustomerById(1L));
        verify(customerRepository).findById(1L);
    }

    @Test
    void getAllCustomers_Success() {
        // Given
        Customer customer2 = new Customer("Jane", "Smith", "jane.smith@example.com");
        customer2.setId(2L);
        when(customerRepository.findAll()).thenReturn(Arrays.asList(testCustomer, customer2));

        // When
        List<CustomerResponse> responses = customerService.getAllCustomers();

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(customerRepository).findAll();
    }

    @Test
    void updateCustomer_Success() {
        // Given
        CustomerRequest updateRequest = new CustomerRequest("John", "Updated", "john.updated@example.com");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.existsByEmail("john.updated@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // When
        CustomerResponse response = customerService.updateCustomer(1L, updateRequest);

        // Then
        assertNotNull(response);
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void updateCustomer_NotFound_ThrowsException() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CustomerNotFoundException.class, 
            () -> customerService.updateCustomer(1L, testRequest));
        verify(customerRepository).findById(1L);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_Success() {
        // Given
        when(customerRepository.existsById(1L)).thenReturn(true);

        // When
        customerService.deleteCustomer(1L);

        // Then
        verify(customerRepository).existsById(1L);
        verify(customerRepository).deleteById(1L);
    }

    @Test
    void deleteCustomer_NotFound_ThrowsException() {
        // Given
        when(customerRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThrows(CustomerNotFoundException.class, 
            () -> customerService.deleteCustomer(1L));
        verify(customerRepository).existsById(1L);
        verify(customerRepository, never()).deleteById(1L);
    }
}