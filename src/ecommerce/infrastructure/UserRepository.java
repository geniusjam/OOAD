package ecommerce.infrastructure;

import ecommerce.domain.User;

public interface UserRepository {
    User findById(String userId);
    User findByUsername(String username);
    void save(User user);
}
