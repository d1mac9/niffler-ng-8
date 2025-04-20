package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.spend.CategoryEntity;

import java.util.Optional;
import java.util.UUID;

public interface AuthUserDao {
    AuthUserEntity create(AuthUserEntity user);
    Optional<AuthUserEntity> findById(UUID id);
    Optional<AuthUserEntity> findByUsername(String username);
    void delete(AuthUserEntity user);
}
