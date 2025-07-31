# java-filmorate

## Диаграмма базы данных _filmorate_
![Database diagram](docs/filmorate-diagram.png)

## Примеры запросов

### Получение 10 самых популярных фильмов
```sql
SELECT f.Id,
       f.Name,
       f.Description,
       CAST(f.ReleaseDate AS DATE) AS ReleaseDate,
       f.Duration,
       r.Name AS Rating
FROM Films AS f
INNER JOIN AgeRatings AS r ON f.AgeRatingId = r.Id
WHERE f.Id IN (SELECT FilmId
	       FROM Likes
	       GROUP BY FilmId
	       ORDER BY COUNT(FilmId) DESC
	       LIMIT 10);
```

### Получение друзей пользователя с _Id_ = 1
```sql
SELECT u.*
FROM Users AS u
INNER JOIN Friends AS f ON u.Id = f.FriendId
WHERE f.UserId = 1 AND f.Confirmed = True;
```

### Получение общих друзей для пользователей с _Id_ = 1 и _Id_ = 2
```sql
SELECT u.*
	FROM Users AS u
	INNER JOIN Friends AS f ON u.Id = f.FriendId
	WHERE f.UserId = 1 AND f.Confirmed = True
INTERSECT
SELECT u.*
	FROM Users AS u
	INNER JOIN Friends AS f ON u.Id = f.FriendId
	WHERE f.UserId = 2 AND f.Confirmed = True;
```
