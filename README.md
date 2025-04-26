# java-filmorate  
### Краткое описание
Проект выполнен в рамках обучения в Яндекс Практикуме, курс "Java-разработчик".
Проект позволяет управлять базой фильмов, пользователей и лайков, реализуя функциональность похожую на рекомендательную систему.  

### Технологии и стек
- Java 22
- Spring Boot
- Spring JDBC
- H2
- Maven
- JUnit
- REST API

### API-эндпоинты
#### Фильмы (`/films`)
- `GET /films` - получить все фильмы
- `GET /films/{filmId}` — получить фильм по ID
- `GET /films/popular?count={n}&genreId={id}&year={yyyy}` — топ популярных фильмов
- `GET /films/director/{directorId}?sortBy={likes|year}` — фильмы режиссёра с сортировкой
- `GET /films/common?userId={id}&friendId={id}` — общие фильмы двух пользователей
- `GET /films/search?query={text}&by=title,director`  — поиск по названию/режиссёру
- `PUT /films` — обновить фильм
- `PUT /films/{filmId}/like/{userId}` — поставить лайк фильму
- `DELETE /films/{filmId}` — удалить фильм
- `DELETE /films/{filmId}/like/{userId}` — удалить лайк фильма

#### Пользователи (`/users`)
- `GET /users` — получить всех пользователей
- `GET /users/{userId}` — получить пользователя по ID
- `GET /users/{userId}/friends` — получить список друзей
- `GET /users/{userId}/friends/common/{friendId}` — общие друзья
- `GET /users/{userId}/feed` — лента активности пользователя
- `GET /users/{userId}/recommendations` — рекомендации фильмов
- `POST /users` — создать пользователя
- `PUT /users` — обновить пользователя
- `PUT /users/{userId}/friends/{friendId}` — добавить в друзья
- `DELETE /users/{userId}` — удалить пользователя
- `DELETE /users/{userId}/friends/{friendId}` — удалить из друзей

#### Отзывы (`/reviews`)
- `GET /reviews?filmId={id}&count={n}` — получить список рецензий
- `GET /reviews/{reviewId}` — получить рецензию по ID
- `POST /reviews` — добавить рецензию
- `PUT /reviews` — обновить рецензию
- `DELETE /reviews/{reviewId}` — удалить рецензию
- `PUT /reviews/{reviewId}/like/{userId}` — лайк рецензии
- `PUT /reviews/{reviewId}/dislike/{userId}` — дизлайк рецензии
- `DELETE /reviews/{reviewId}/like/{userId}` — удалить лайк
- `DELETE /reviews/{reviewId}/dislike/{userId}` — удалить дизлайк

#### Жанры (`/genres`)
- `GET /genres` — получить все жанры
- `GET /genres/{genreId}` — получить жанр по ID

#### Рейтинг MPA (`/mpa`)
- `GET /mpa` — получить все рейтинги
- `GET /mpa/{ratingId}` — получить рейтинг по ID

#### Режиссёры (`/directors`)
- `GET /directors` — получить всех режиссёров
- `GET /directors/{directorId}` — получить режиссёра по ID
- `POST /directors` — создать режиссёра
- `PUT /directors` — обновить режиссёра
- `DELETE /directors/{directorId}` — удалить режиссёра

### Диаграмма БД
![Диаграмма базы данных](images/db-diagram.png)  


### Авторы
- [Jalecti](https://github.com/Jalecti)
- [isakovleonid](https://github.com/isakovleonid)
- [n5n1m2](https://github.com/n5n1m2)
- [Borgex14](https://github.com/Borgex14)