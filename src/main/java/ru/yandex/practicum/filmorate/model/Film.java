package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private int mpaId;

    public Mpa getMpa() {
        Mpa m = new Mpa();
        m.setId(mpaId);
        return m;
    }

    public void setMpa(Mpa mpa) {
        if (mpa != null) {
            this.mpaId = mpa.getId();
        }
    }
}