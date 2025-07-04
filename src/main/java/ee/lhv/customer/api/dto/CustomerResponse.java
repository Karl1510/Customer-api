package ee.lhv.customer.api.dto;

import ee.lhv.customer.api.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime createdDtime;
    private LocalDateTime modifiedDtime;

    public CustomerResponse(Customer customer) {
        this.id = customer.getId();
        this.firstName = customer.getFirstName();
        this.lastName = customer.getLastName();
        this.email = customer.getEmail();
        this.createdDtime = customer.getCreatedDtime();
        this.modifiedDtime = customer.getModifiedDtime();
    }
}