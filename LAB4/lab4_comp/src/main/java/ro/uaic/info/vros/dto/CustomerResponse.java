package ro.uaic.info.vros.dto;

import ro.uaic.info.vros.entity.Customer;

// Returneaza datele clientului fara sa incarce lista de livrari.
public record CustomerResponse(Long id, String name, String email, String phone,
                               String address, String coordinates) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getName(), customer.getEmail(),
                customer.getPhone(), customer.getAddress(), customer.getCoordinates());
    }
}
