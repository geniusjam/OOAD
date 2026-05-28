package ecommerce.domain;

public abstract class User {
    private final String userId;
    private final String username;
    private final String email;
    private final String password;
    private final UserRole role;

    protected User(String userId, String username, String email, String password, UserRole role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public UserRole getRole() { return role; }
}
