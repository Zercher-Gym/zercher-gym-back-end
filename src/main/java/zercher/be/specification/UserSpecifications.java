package zercher.be.specification;

import org.springframework.data.jpa.domain.Specification;

import zercher.be.model.entity.User;
import zercher.be.model.entity.User_;

import java.util.UUID;

public class UserSpecifications {
    public static Specification<User> usernameContains(String partialUsername) {
        return (root, _, cb) -> cb.like(
                cb.lower(root.get(User_.USERNAME)), "%" + partialUsername.toLowerCase() + "%");
    }

    public static Specification<User> emailContains(String partialEmail) {
        return (root, _, cb)
                -> cb.like(cb.lower(root.get(User_.EMAIL)), "%" + partialEmail.toLowerCase() + "%");
    }

    public static Specification<User> idEquals(UUID id) {
        return (root, _, cb) -> cb.equal(root.get(User_.ID), id);
    }

    public static Specification<User> enabledEquals(Boolean enabled) {
        return (root, _, cb) -> cb.equal(root.get(User_.ENABLED), enabled);
    }

    public static Specification<User> lockedEquals(Boolean locked) {
        return (root, _, cb) -> cb.equal(root.get(User_.LOCKED), locked);
    }
}
