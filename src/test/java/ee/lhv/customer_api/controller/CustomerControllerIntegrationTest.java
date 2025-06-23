package ee.lhv.customer_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.lhv.customer_api.dto.CustomerRequest;
import ee.lhv.customer_api.entity.Customer;
import ee.lhv.customer_api.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@Transactional
@ActiveProfiles("test")
class CustomerControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        customerRepository.deleteAll();
    }

    @Test
    void createCustomer_Success() throws Exception {
        CustomerRequest request = new CustomerRequest("John", "Doe", "john.doe@example.com");

        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdDtime").exists())
                .andExpect(jsonPath("$.modifiedDtime").exists());
    }

    @Test
    void createCustomer_ValidationError() throws Exception {
        CustomerRequest request = new CustomerRequest("", "", "invalid-email");

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.firstName").value("First name is required"))
                .andExpect(jsonPath("$.errors.lastName").value("Last name is required"))
                .andExpect(jsonPath("$.errors.email").value("Email should be valid"));
    }

    @Test
    void createCustomer_EmailAlreadyExists() throws Exception {
        // Create first customer
        Customer existingCustomer = new Customer("Jane", "Smith", "test@example.com");
        customerRepository.save(existingCustomer);

        // Try to create another customer with same email
        CustomerRequest request = new CustomerRequest("John", "Doe", "test@example.com");

        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Customer with email 'test@example.com' already exists"));
    }

    @Test
    void getCustomerById_Success() throws Exception {
        Customer customer = new Customer("John", "Doe", "john.doe@example.com");
        Customer savedCustomer = customerRepository.save(customer);

        mockMvc.perform(get("/customers/{id}", savedCustomer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedCustomer.getId()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void getCustomerById_NotFound() throws Exception {
        mockMvc.perform(get("/customers/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found with id: 999"));
    }

    @Test
    void getAllCustomers_Success() throws Exception {
        Customer customer1 = new Customer("John", "Doe", "john.doe@example.com");
        Customer customer2 = new Customer("Jane", "Smith", "jane.smith@example.com");
        customerRepository.save(customer1);
        customerRepository.save(customer2);

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    @Test
    void updateCustomer_Success() throws Exception {
        Customer customer = new Customer("John", "Doe", "john.doe@example.com");
        Customer savedCustomer = customerRepository.save(customer);

        CustomerRequest updateRequest = new CustomerRequest("John", "Updated", "john.updated@example.com");

        mockMvc.perform(put("/customers/{id}", savedCustomer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedCustomer.getId()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Updated"))
                .andExpect(jsonPath("$.email").value("john.updated@example.com"));
    }

    @Test
    void updateCustomer_NotFound() throws Exception {
        CustomerRequest updateRequest = new CustomerRequest("John", "Updated", "john.updated@example.com");

        mockMvc.perform(put("/customers/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found with id: 999"));
    }

    @Test
    void deleteCustomer_Success() throws Exception {
        Customer customer = new Customer("John", "Doe", "john.doe@example.com");
        Customer savedCustomer = customerRepository.save(customer);

        mockMvc.perform(delete("/customers/{id}", savedCustomer.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCustomer_NotFound() throws Exception {
        mockMvc.perform(delete("/customers/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found with id: 999"));
    }
}
