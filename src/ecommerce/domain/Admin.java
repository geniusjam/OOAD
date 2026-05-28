package ecommerce.domain;

public class Admin extends User {
    public Admin(String userId, String username, String email, String password) {
        super(userId, username, email, password, UserRole.ADMIN);
    }
}
