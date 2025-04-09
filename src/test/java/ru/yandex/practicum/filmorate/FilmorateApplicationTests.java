package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mappers.*;
import ru.yandex.practicum.filmorate.dal.repositories.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
        UserRepository.class, UserRowMapper.class,
        FilmRepository.class, FilmRowMapper.class,
        FriendshipRepository.class, FriendshipRowMapper.class,
        RatingRepository.class, RatingRowMapper.class,
        GenreRepository.class, GenreRowMapper.class
})
class FilmorateApplicationTests {
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final FriendshipRepository friendshipRepository;
    private final RatingRepository ratingRepository;
    private final GenreRepository genreRepository;

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
        filmRepository.deleteAll();
        filmRepository.deleteAllFilmLikes();
        friendshipRepository.deleteAll();
        genreRepository.deleteAllFilmGenres();
    }

    @Test
    public void testCreateUser() {
        User user1 = new User();
        user1.setEmail("email1");
        user1.setLogin("login1");
        user1.setName("name1");
        LocalDate now = LocalDate.now();
        user1.setBirthday(now);

        userRepository.create(user1);
        Optional<User> userOptional = userRepository.getUserById(1L);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "email1")
                                .hasFieldOrPropertyWithValue("login", "login1")
                                .hasFieldOrPropertyWithValue("name", "name1")
                                .hasFieldOrPropertyWithValue("birthday", now)
                );
    }

    @Test
    public void testFindUserById() {
        User user1 = new User();
        user1.setEmail("email1");
        user1.setLogin("login1");
        user1.setName("name1");
        user1.setBirthday(LocalDate.now());

        userRepository.create(user1);
        Optional<User> userOptional = userRepository.getUserById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testUpdateUser() {
        User user1 = new User();
        user1.setEmail("email1");
        user1.setLogin("login1");
        user1.setName("name1");
        LocalDate now = LocalDate.now();
        user1.setBirthday(now);

        userRepository.create(user1);

        User user2 = new User();
        user2.setEmail("email2");
        user2.setLogin("login2");
        user2.setName("name2");
        user2.setBirthday(now);
        user2.setId(1L);

        userRepository.update(user2);

        Optional<User> userOptional = userRepository.getUserById(1L);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "email2")
                                .hasFieldOrPropertyWithValue("login", "login2")
                                .hasFieldOrPropertyWithValue("name", "name2")
                                .hasFieldOrPropertyWithValue("birthday", now)
                );
    }

    @Test
    public void testFindAllUsers() {
        User user1 = new User();
        user1.setEmail("email1");
        user1.setLogin("login1");
        user1.setName("name1");
        user1.setBirthday(LocalDate.now());

        User user2 = new User();
        user2.setEmail("email2");
        user2.setLogin("login2");
        user2.setName("name2");
        user2.setBirthday(LocalDate.now());

        userRepository.create(user1);
        userRepository.create(user2);
        Collection<User> users = userRepository.findAll();

        assertThat(users)
                .hasSize(2)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    public void testDeleteUser() {
        User user1 = new User();
        user1.setEmail("email1");
        user1.setLogin("login1");
        user1.setName("name1");
        user1.setBirthday(LocalDate.now());

        User user2 = new User();
        user2.setEmail("email2");
        user2.setLogin("login2");
        user2.setName("name2");
        user2.setBirthday(LocalDate.now());

        userRepository.create(user1);
        userRepository.create(user2);
        Collection<User> users = userRepository.findAll();

        assertThat(users).hasSize(2);
        userRepository.delete(2L);

        users = userRepository.findAll();
        assertThat(users).hasSize(1);
    }

    @Test
    public void testCreateFilm() {
        Film film1 = new Film();
        film1.setName("name1");
        film1.setDescription("description1");
        film1.setDuration(100);
        LocalDate now = LocalDate.now();
        film1.setReleaseDate(now);

        filmRepository.create(film1);
        Optional<Film> filmOptional = filmRepository.getFilmById(1L);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "name1")
                                .hasFieldOrPropertyWithValue("description", "description1")
                                .hasFieldOrPropertyWithValue("duration", 100)
                                .hasFieldOrPropertyWithValue("releaseDate", now)
                );
    }

    @Test
    public void testFindFilmById() {
        Film film1 = new Film();
        film1.setName("name1");
        film1.setDescription("description1");
        film1.setDuration(100);
        LocalDate now = LocalDate.now();
        film1.setReleaseDate(now);

        filmRepository.create(film1);
        Optional<Film> filmOptional = filmRepository.getFilmById(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testUpdateFilm() {
        Film film1 = new Film();
        film1.setName("name1");
        film1.setDescription("description1");
        film1.setDuration(100);
        LocalDate now = LocalDate.now();
        film1.setReleaseDate(now);

        filmRepository.create(film1);

        Film film2 = new Film();
        film2.setName("name2");
        film2.setDescription("description2");
        film2.setDuration(200);
        film2.setReleaseDate(now);
        film2.setId(1L);
        film2.setMpa(new Rating(1L, "G"));

        filmRepository.update(film2);

        Optional<Film> filmOptional = filmRepository.getFilmById(1L);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "name2")
                                .hasFieldOrPropertyWithValue("description", "description2")
                                .hasFieldOrPropertyWithValue("duration", 200)
                                .hasFieldOrPropertyWithValue("releaseDate", now)
                );
    }

    @Test
    public void testFindAllFilms() {
        Film film1 = new Film();
        film1.setName("name1");
        film1.setDescription("description1");
        film1.setDuration(100);
        LocalDate now = LocalDate.now();
        film1.setReleaseDate(now);

        Film film2 = new Film();
        film2.setName("name2");
        film2.setDescription("description2");
        film2.setDuration(200);
        film2.setReleaseDate(now);

        filmRepository.create(film1);
        filmRepository.create(film2);
        Collection<Film> films = filmRepository.findAll();

        assertThat(films)
                .hasSize(2)
                .extracting(Film::getId)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    public void testDeleteFilm() {
        Film film1 = new Film();
        film1.setName("name1");
        film1.setDescription("description1");
        film1.setDuration(100);
        LocalDate now = LocalDate.now();
        film1.setReleaseDate(now);

        Film film2 = new Film();
        film2.setName("name2");
        film2.setDescription("description2");
        film2.setDuration(200);
        film2.setReleaseDate(now);

        filmRepository.create(film1);
        filmRepository.create(film2);
        Collection<Film> films = filmRepository.findAll();

        assertThat(films).hasSize(2);
        filmRepository.delete(2L);

        films = filmRepository.findAll();
        assertThat(films).hasSize(1);
    }
}