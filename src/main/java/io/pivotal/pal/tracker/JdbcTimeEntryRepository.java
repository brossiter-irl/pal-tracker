package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcTimeEntryRepository implements TimeEntryRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(c -> {
            PreparedStatement s = c.prepareStatement(
                    "INSERT INTO time_entries (project_id, user_id, date, hours) "
                            + "VALUES (?, ?, ?, ?)", RETURN_GENERATED_KEYS);
            s.setLong(1, timeEntry.getProjectId());
            s.setLong(2, timeEntry.getUserId());
            s.setDate(3, Date.valueOf(timeEntry.getDate()));
            s.setLong(4, timeEntry.getHours());

            return s;

        }, generatedKeyHolder);

        return find(generatedKeyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(Long id) {
        return jdbcTemplate.query("SELECT id, project_id, user_id, date, hours FROM time_entries WHERE id = ?",
                new Object[]{id}, extractor);
    }

    @Override
    public List<TimeEntry> list() {
        return jdbcTemplate.query("SELECT id, project_id, user_id, date, hours FROM time_entries", mapper);
    }

    @Override
    public TimeEntry update(Long id, TimeEntry timeEntry) {
        jdbcTemplate.update("UPDATE time_entries " +
                        "SET project_id = ?, user_id = ?, date = ?, hours = ? " +
                        "WHERE id = ?",
                timeEntry.getProjectId(),
                timeEntry.getUserId(),
                Date.valueOf(timeEntry.getDate()),
                timeEntry.getHours(),
                id);

        return find(id);
    }

    @Override
    public void delete(Long timeEntryId) {
        jdbcTemplate.update("DELETE from time_entries WHERE id = ?", timeEntryId);
    }

    private final RowMapper<TimeEntry> mapper = (rs, rowNum) -> new TimeEntry(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("user_id"),
            rs.getDate("date").toLocalDate(),
            rs.getInt("hours"));

    private final ResultSetExtractor<TimeEntry> extractor = (rs) ->
            rs.next() ? mapper.mapRow(rs, 1) : null;
}
