INSERT INTO mpa_ratings (rating) SELECT * FROM (
    SELECT 'G' UNION
    SELECT 'PG' UNION
    SELECT 'PG-13' UNION
    SELECT 'R' UNION
    SELECT 'NC-17'
) WHERE NOT EXISTS (SELECT * FROM mpa_ratings);

INSERT INTO genres (name) SELECT * FROM (
    SELECT 'Комедия' UNION
    SELECT 'Драма' UNION
	SELECT 'Мультфильм' UNION
	SELECT 'Триллер' UNION
	SELECT 'Документальный' UNION
	SELECT 'Боевик'
) WHERE NOT EXISTS (SELECT * FROM genres);