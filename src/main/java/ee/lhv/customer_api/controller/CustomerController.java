package ee.lhv.customer_api.controller;

import ee.lhv.customer_api.dto.CustomerRequest;
import ee.lhv.customer_api.dto.CustomerResponse;
import ee.lhv.customer_api.service.CustomerService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CustomerRequest request) {
        log.info("Creating new customer with email: {}", request.getEmail());
        CustomerResponse response = customerService.createCustomer(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerResponse> getCustomerById(
            @Parameter(description = "Customer ID", required = true) @PathVariable Long id) {
        log.info("Fetching customer with id: {}", id);
        CustomerResponse response = customerService.getCustomerById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        log.info("Fetching all customers");
        List<CustomerResponse> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerResponse> updateCustomer(
            @Parameter(description = "Customer ID", required = true) @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request) {
        log.info("Updating customer with id: {}", id);
        CustomerResponse response = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "Customer ID", required = true) @PathVariable Long id) {
        log.info("Deleting customer with id: {}", id);
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}