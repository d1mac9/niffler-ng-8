package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class UserdataUserRepositoryHibernate implements UserdataUserRepository {

  private static final Config CFG = Config.getInstance();

  private final EntityManager entityManager = em(CFG.userdataJdbcUrl());

  @Nonnull
  @Override
  public UserEntity create(UserEntity user) {
    entityManager.joinTransaction();
    entityManager.persist(user);
    return user;
  }

  @Nonnull
  @Override
  public UserEntity update(UserEntity user) {
    entityManager.joinTransaction();
    return entityManager.merge(user);
  }

  @Nonnull
  @Override
  public Optional<UserEntity> findById(UUID id) {
    return Optional.ofNullable(
        entityManager.find(UserEntity.class, id)
    );
  }

  @Nonnull
  @Override
  public Optional<UserEntity> findByUsername(String username) {
    try {
      return Optional.of(
          entityManager.createQuery("select u from UserEntity u where u.username =: username", UserEntity.class)
              .setParameter("username", username)
              .getSingleResult()
      );
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

    @Override
    public void sendInvitation(UserEntity requester, UserEntity addressee) {
        entityManager.joinTransaction();
        addressee.addFriends(FriendshipStatus.PENDING, requester);
    }

  @Override
  public void addFriend(UserEntity requester, UserEntity addressee) {
    entityManager.joinTransaction();
    requester.addFriends(FriendshipStatus.ACCEPTED, addressee);
    addressee.addFriends(FriendshipStatus.ACCEPTED, requester);
  }

  @Override
  public void remove(UserEntity user) {
    entityManager.joinTransaction();
    UserEntity managedUser = entityManager.find(UserEntity.class, user.getId());
    if (managedUser != null) {
      entityManager.remove(managedUser);
    }
  }
}
