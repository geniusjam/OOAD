package ecommerce.service;

import ecommerce.domain.Admin;
import ecommerce.domain.Customer;
import ecommerce.domain.User;
import ecommerce.domain.UserRole;
import ecommerce.infrastructure.InMemoryUserRepository;
import ecommerce.infrastructure.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private AuthService authService;

    @BeforeEach
    void setUp() {
        UserRepository repo = new InMemoryUserRepository();
        authService = new AuthService(repo);
    }

    @Test
    void registerCustomerCreatesCustomerInstance() {
        User user = authService.register("alice", "alice@test.com", "secret", UserRole.CUSTOMER);
        assertNotNull(user);
        assertInstanceOf(Customer.class, user);
        assertEquals("alice", user.getUsername());
        assertEquals(UserRole.CUSTOMER, user.getRole());
    }

    @Test
    void registerAdminCreatesAdminInstance() {
        User user = authService.register("bob", "bob@test.com", "secret", UserRole.ADMIN);
        assertNotNull(user);
        assertInstanceOf(Admin.class, user);
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    void registerAssignsUniqueIds() {
        User u1 = authService.register("u1", "u1@test.com", "pw", UserRole.CUSTOMER);
        User u2 = authService.register("u2", "u2@test.com", "pw", UserRole.CUSTOMER);
        assertNotNull(u1);
        assertNotNull(u2);
        assertNotEquals(u1.getUserId(), u2.getUserId());
    }

    @Test
    void registerDuplicateUsernameReturnsNull() {
        authService.register("alice", "a@test.com", "pw", UserRole.CUSTOMER);
        User second = authService.register("alice", "b@test.com", "pw2", UserRole.CUSTOMER);
        assertNull(second);
    }

    @Test
    void registerEmptyUsernameReturnsNull() {
        User user = authService.register("", "e@test.com", "pw", UserRole.CUSTOMER);
        assertNull(user);
    }

    @Test
    void registerEmptyPasswordReturnsNull() {
        User user = authService.register("alice", "a@test.com", "", UserRole.CUSTOMER);
        assertNull(user);
    }

    @Test
    void passwordIsStoredHashed() {
        User user = authService.register("alice", "a@test.com", "secret", UserRole.CUSTOMER);
        assertNotNull(user);
        assertNotEquals("secret", user.getPassword());
        assertEquals(authService.hashPassword("secret"), user.getPassword());
    }

    @Test
    void loginWithCorrectCredentialsReturnsUser() {
        authService.register("alice", "a@test.com", "secret", UserRole.CUSTOMER);
        User user = authService.login("alice", "secret");
        assertNotNull(user);
        assertEquals("alice", user.getUsername());
    }

    @Test
    void loginWithWrongPasswordReturnsNull() {
        authService.register("alice", "a@test.com", "secret", UserRole.CUSTOMER);
        User user = authService.login("alice", "wrong");
        assertNull(user);
    }

    @Test
    void loginWithUnknownUsernameReturnsNull() {
        User user = authService.login("nobody", "pw");
        assertNull(user);
    }

    @Test
    void loginAfterRegisterAdminReturnsAdminRole() {
        authService.register("admin", "admin@test.com", "adminpw", UserRole.ADMIN);
        User user = authService.login("admin", "adminpw");
        assertNotNull(user);
        assertEquals(UserRole.ADMIN, user.getRole());
    }
}
