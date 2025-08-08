INSERT INTO mpa (name)
SELECT name
FROM (SELECT 'G' AS name UNION ALL
      SELECT 'PG' UNION ALL
      SELECT 'PG-13' UNION ALL
      SELECT 'R' UNION ALL
      SELECT 'NC-17') AS temp
WHERE NOT EXISTS (SELECT 1 FROM mpa);

INSERT INTO genres (name)
SELECT name
FROM (SELECT 'Комедия' AS name UNION ALL
      SELECT 'Драма' UNION ALL
      SELECT 'Мультфильм' UNION ALL
      SELECT 'Триллер' UNION ALL
      SELECT 'Документальный' UNION ALL
      SELECT 'Боевик') AS temp
WHERE NOT EXISTS (SELECT 1 FROM genres);