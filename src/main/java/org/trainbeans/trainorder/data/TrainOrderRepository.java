package org.trainbeans.trainorder.data;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.trainbeans.trainorder.model.TrainOrder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class TrainOrderRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public TrainOrderRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ── Row mapper ─────────────────────────────────────────────────────────────

    static final RowMapper<TrainOrder> MAPPER = (rs, rowNum) -> {
        var ts = rs.getTimestamp("created_at");
        return TrainOrder.builder()
                .id(rs.getLong("id"))
                .orderNumber(rs.getString("order_number"))
                .formType(rs.getString("form_type"))
                .fromLocation(rs.getString("from_location"))
                .orderDate(rs.getString("order_date"))
                .toLine(rs.getString("to_line"))
                .operatorInitials(rs.getString("operator_initials"))
                .timeIssued(rs.getString("time_issued"))
                .atLocation(rs.getString("at_location"))
                .dispatcherInitials(rs.getString("dispatcher_initials"))
                .instructions(rs.getString("instructions"))
                .completeTime(rs.getString("complete_time"))
                .completeOperator(rs.getString("complete_operator"))
                .recopiedBy(rs.getString("recopied_by"))
                .recopyOperator(rs.getString("recopy_operator"))
                .recopyDate(rs.getString("recopy_date"))
                .completed(rs.getBoolean("completed"))
                .createdAt(ts != null ? ts.toLocalDateTime() : null)
                .build();
    };

    // ── Queries ────────────────────────────────────────────────────────────────

    public List<TrainOrder> findAll() {
        return jdbc.query(
                "SELECT * FROM train_orders ORDER BY id DESC",
                MAPPER);
    }

    public Optional<TrainOrder> findById(Long id) {
        List<TrainOrder> rows = jdbc.query(
                "SELECT * FROM train_orders WHERE id = :id",
                new MapSqlParameterSource("id", id),
                MAPPER);
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    // ── Mutations ──────────────────────────────────────────────────────────────

    public TrainOrder save(TrainOrder order) {
        String sql = """
                INSERT INTO train_orders
                  (order_number, form_type, from_location, order_date, to_line,
                   operator_initials, time_issued, at_location, dispatcher_initials,
                   instructions,
                   complete_time, complete_operator,
                   recopied_by, recopy_operator, recopy_date)
                VALUES
                  (:orderNumber, :formType, :fromLocation, :orderDate, :toLine,
                   :operatorInitials, :timeIssued, :atLocation, :dispatcherInitials,
                   :instructions,
                   :completeTime, :completeOperator,
                   :recopiedBy, :recopyOperator, :recopyDate)
                """;
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.update(sql, params(order), keys, new String[]{"id"});
        order.setId(Objects.requireNonNull(keys.getKey()).longValue());
        return order;
    }

    public void update(TrainOrder order) {
        String sql = """
                UPDATE train_orders SET
                  order_number        = :orderNumber,
                  form_type           = :formType,
                  from_location       = :fromLocation,
                  order_date          = :orderDate,
                  to_line             = :toLine,
                  operator_initials   = :operatorInitials,
                  time_issued         = :timeIssued,
                  at_location         = :atLocation,
                  dispatcher_initials = :dispatcherInitials,
                  instructions        = :instructions,
                  complete_time       = :completeTime,
                  complete_operator   = :completeOperator,
                  recopied_by         = :recopiedBy,
                  recopy_operator     = :recopyOperator,
                  recopy_date         = :recopyDate
                WHERE id = :id AND completed = FALSE
                """;
        jdbc.update(sql, params(order).addValue("id", order.getId()));
    }

    public void deleteById(Long id) {
        jdbc.update("DELETE FROM train_orders WHERE id = :id",
                new MapSqlParameterSource("id", id));
    }

    /** Returns the next order number for today (existing today count + 1). */
    public int nextOrderNumberForToday() {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM train_orders WHERE CAST(created_at AS DATE) = CURRENT_DATE",
                new MapSqlParameterSource(),
                Integer.class);
        return (count != null ? count : 0) + 1;
    }


    // ── Helper ─────────────────────────────────────────────────────────────────

    private MapSqlParameterSource params(TrainOrder o) {
        return new MapSqlParameterSource()
                .addValue("orderNumber",        o.getOrderNumber())
                .addValue("formType",           o.getFormType())
                .addValue("fromLocation",       o.getFromLocation())
                .addValue("orderDate",          o.getOrderDate())
                .addValue("toLine",             o.getToLine())
                .addValue("operatorInitials",   o.getOperatorInitials())
                .addValue("timeIssued",         o.getTimeIssued())
                .addValue("atLocation",         o.getAtLocation())
                .addValue("dispatcherInitials", o.getDispatcherInitials())
                .addValue("instructions",       o.getInstructions())
                .addValue("completeTime",       o.getCompleteTime())
                .addValue("completeOperator",   o.getCompleteOperator())
                .addValue("recopiedBy",         o.getRecopiedBy())
                .addValue("recopyOperator",     o.getRecopyOperator())
                .addValue("recopyDate",         o.getRecopyDate());
    }
}
