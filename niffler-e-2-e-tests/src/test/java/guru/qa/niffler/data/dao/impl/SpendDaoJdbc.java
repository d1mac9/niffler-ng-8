package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.model.CurrencyValues;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

@ParametersAreNonnullByDefault
public class SpendDaoJdbc implements SpendDao {

  private static final Config CFG = Config.getInstance();
  private final String url = CFG.spendJdbcUrl();

  @SuppressWarnings("resource")
  @Nonnull
  @Override
  public SpendEntity create(SpendEntity spend) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
        """
               INSERT INTO spend (username, spend_date, currency, amount, description, category_id)
               VALUES ( ?, ?, ?, ?, ?, ?)
            """,
        Statement.RETURN_GENERATED_KEYS
    )) {
      ps.setString(1, spend.getUsername());
      ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
      ps.setString(3, spend.getCurrency().name());
      ps.setDouble(4, spend.getAmount());
      ps.setString(5, spend.getDescription());
      ps.setObject(6, spend.getCategory().getId());

            ps.executeUpdate();

      final UUID generatedKey;
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          generatedKey = rs.getObject("id", UUID.class);
        } else {
          throw new SQLException("Can`t find id in ResultSet");
        }
      }
      spend.setId(generatedKey);
      return spend;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("resource")
  @Nonnull
  @Override
  public Optional<SpendEntity> findSpendById(UUID id) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
        "SELECT * FROM spend WHERE id = ?"
    )) {
      ps.setObject(1, id);
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          return Optional.ofNullable(
              SpendEntityRowMapper.instance.mapRow(rs, rs.getRow())
          );
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("resource")
  @Nonnull
  @Override
  public List<SpendEntity> findAll() {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
        "SELECT * FROM spend")) {
      ps.execute();
      List<SpendEntity> result = new ArrayList<>();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          result.add(
              SpendEntityRowMapper.instance.mapRow(rs, rs.getRow())
          );
        }
      }
      return result;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("resource")
  @Nonnull
  @Override
  public SpendEntity update(SpendEntity spend) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
        """
              UPDATE "spend"
                SET spend_date  = ?,
                    currency    = ?,
                    amount      = ?,
                    description = ?
                WHERE id = ?
            """)
    ) {
      ps.setDate(1, new java.sql.Date(spend.getSpendDate().getTime()));
      ps.setString(2, spend.getCurrency().name());
      ps.setDouble(3, spend.getAmount());
      ps.setString(4, spend.getDescription());
      ps.setObject(5, spend.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return spend;
  }


    @NotNull
    @Override
    public Optional<SpendEntity> findByUsernameAndDescription(String username, String description) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT * FROM spend WHERE username = ? AND description = ?"
        )) {
            ps.setString(1, username);
            ps.setString(2, description);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    SpendEntity se = new SpendEntity();
                    CategoryEntity ce = new CategoryEntity();
                    se.setId(rs.getObject("id", UUID.class));
                    se.setUsername(rs.getString("username"));
                    se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    se.setSpendDate(rs.getDate("spend_date"));
                    se.setAmount(rs.getDouble("amount"));
                    se.setDescription(rs.getString("description"));
                    ce.setId(rs.getObject("category_id", UUID.class));
                    se.setCategory(ce);
                    return Optional.of(se);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find spend by username and description", e);
        }
    }

    @Override
    public void deleteSpend(SpendEntity spend) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "DELETE FROM spend WHERE id = ?"
        )) {
            ps.setObject(1, spend.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting spend with Id:  " + spend.getId(), e);
        }
    }
}
