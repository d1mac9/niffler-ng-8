package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.jdbc.DataSources;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;
import java.util.UUID;

public class UserdataUserRepositorySpringJdbc implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();
    private final String url = CFG.userdataJdbcUrl();

    private final UdUserDao udUserDao = new UdUserDaoSpringJdbc();

  @Override
  public UserEntity create(UserEntity user) {
    return udUserDao.create(user);
  }

  @Override
  public UserEntity update(UserEntity user) {
    return udUserDao.update(user);
  }

  @Override
  public Optional<UserEntity> findById(UUID id) {
    return udUserDao.findById(id);
  }

  @Override
  public Optional<UserEntity> findByUsername(String username) {
    return udUserDao.findByUsername(username);
  }

  @Override
  public void sendInvitation(UserEntity requester, UserEntity addressee) {
    requester.addFriends(FriendshipStatus.PENDING, addressee);
    udUserDao.update(requester);
  }

  @Override
  public void addFriend(UserEntity requester, UserEntity addressee) {
    requester.addFriends(FriendshipStatus.ACCEPTED, addressee);
    addressee.addFriends(FriendshipStatus.ACCEPTED, requester);
    udUserDao.update(requester);
    udUserDao.update(addressee);
  }
    @Override
    public void remove(UserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
        jdbcTemplate.update(
                "DELETE FROM friendship WHERE requester_id = ? OR addressee_id = ?",
                user.getId(),
                user.getId()
        );
        udUserDao.delete(user);
    }
}
