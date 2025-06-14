package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface AuthAuthorityDao {

  void create(AuthorityEntity... authority);

    AuthorityEntity create(AuthorityEntity authority);

    Optional<AuthorityEntity> findById(UUID id);

    @Nonnull
    List<AuthorityEntity> findAll();

    @Nonnull
    List<AuthorityEntity> findByUserId(UUID userId);

    void delete(AuthorityEntity authority);
}
