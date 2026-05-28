package ecommerce.domain;

public class Customer extends User {
    public Customer(String userId, String username, String email, String password) {
        super(userId, username, email, password, UserRole.CUSTOMER);
    }
}
