package ru.yandex.practicum.filmorate.storage.dal.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaRowMapper implements RowMapper<MpaRating> {
    @Override
    public MpaRating mapRow(ResultSet rs, int rowNum) throws SQLException {
        MpaRating mpa = new MpaRating();
        mpa.setId(rs.getInt("id"));
        mpa.setRating(rs.getString("rating"));
        return mpa;
    }
}