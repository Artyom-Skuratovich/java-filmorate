package ru.yandex.practicum.filmorate.storage.dal.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setDuration(rs.getInt("duration"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setMpaRatingId(rs.getInt("mpa_rating_id"));

        Timestamp releaseDate = rs.getTimestamp("release_date");
        film.setReleaseDate(releaseDate.toLocalDateTime().toLocalDate());

        return film;
    }
}