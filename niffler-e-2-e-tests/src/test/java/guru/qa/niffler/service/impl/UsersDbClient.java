package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.jdbc.DataSources;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.*;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;


public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
  private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryHibernate();
//    private final AuthUserRepository authUserRepository = new AuthUserRepositoryJdbc();
//    private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryJdbc();
//    private final AuthUserRepository authUserRepository = new AuthUserRepositorySpringJdbc();
//    private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositorySpringJdbc();

  private final TransactionTemplate txTemplate = new TransactionTemplate(
      new JdbcTransactionManager(
          DataSources.dataSource(CFG.authJdbcUrl())
      )
  );

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

  public UserJson createUser(String username, String password) {
    return xaTransactionTemplate.execute(() -> {
          AuthUserEntity authUser = authUserEntity(username, password);
          authUserRepository.create(authUser);
          return UserJson.fromEntity(
              userdataUserRepository.create(userEntity(username)),
              null
          );
        }
    );
  }
    @Override
    public void createFriends(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = randomUsername();
                            AuthUserEntity authUser = authUserEntity(username, "12345");
                            authUserRepository.create(authUser);
                            UserEntity friend = userEntity(username);
                            userdataUserRepository.create(friend);
                            userdataUserRepository.addFriend(targetEntity, friend);
                            return null;
                        }
                );
            }
        }
    }
@Override
public void addIncomeInvitation(UserJson targetUser, int count) {
    if (count > 0) {
      UserEntity targetEntity = userdataUserRepository.findById(
          targetUser.id()
      ).orElseThrow();

      for (int i = 0; i < count; i++) {
        xaTransactionTemplate.execute(() -> {
              String username = randomUsername();
              AuthUserEntity authUser = authUserEntity(username, "12345");
              authUserRepository.create(authUser);
              UserEntity adressee = userdataUserRepository.create(userEntity(username));
              userdataUserRepository.sendInvitation(targetEntity, adressee);
              return null;
            }
        );
      }
    }
  }
@Override
public void addOutcomeInvitation(UserJson targetUser, int count) {
    if (count > 0) {
      UserEntity targetEntity = userdataUserRepository.findById(
          targetUser.id()
      ).orElseThrow();

      for (int i = 0; i < count; i++) {
        xaTransactionTemplate.execute(() -> {
              String username = randomUsername();
              AuthUserEntity authUser = authUserEntity(username, "12345");
              authUserRepository.create(authUser);
              UserEntity adressee = userdataUserRepository.create(userEntity(username));
              userdataUserRepository.sendInvitation(targetEntity, adressee);
              return null;
            }
        );
      }
    }
  }

  void addFriend(UserJson targetUser, int count) {

  }

  private UserEntity userEntity(String username) {
    UserEntity ue = new UserEntity();
    ue.setUsername(username);
    ue.setCurrency(CurrencyValues.RUB);
    return ue;
  }

  private AuthUserEntity authUserEntity(String username, String password) {
    AuthUserEntity authUser = new AuthUserEntity();
    authUser.setUsername(username);
    authUser.setPassword(pe.encode(password));
    authUser.setEnabled(true);
    authUser.setAccountNonExpired(true);
    authUser.setAccountNonLocked(true);
    authUser.setCredentialsNonExpired(true);
    authUser.setAuthorities(
        Arrays.stream(Authority.values()).map(
            e -> {
              AuthorityEntity ae = new AuthorityEntity();
              ae.setUser(authUser);
              ae.setAuthority(e);
              return ae;
            }
        ).toList()
    );
    return authUser;
  }
}
