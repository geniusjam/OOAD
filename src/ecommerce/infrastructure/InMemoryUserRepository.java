package ecommerce.infrastructure;

import ecommerce.domain.User;

import java.util.HashMap;
import java.util.Map;

public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> byId = new HashMap<>();
    private final Map<String, User> byUsername = new HashMap<>();

    @Override
    public User findById(String userId) {
        return byId.get(userId);
    }

    @Override
    public User findByUsername(String username) {
        return byUsername.get(username);
    }

    @Override
    public void save(User user) {
        byId.put(user.getUserId(), user);
        byUsername.put(user.getUsername(), user);
    }
}
