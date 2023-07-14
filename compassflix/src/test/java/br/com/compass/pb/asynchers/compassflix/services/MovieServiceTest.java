package br.com.compass.pb.asynchers.compassflix.services;

import br.com.compass.pb.asynchers.compassflix.dto.request.MovieRequestDto;
import br.com.compass.pb.asynchers.compassflix.dto.response.MovieResponseDto;
import br.com.compass.pb.asynchers.compassflix.entities.Movie;
import br.com.compass.pb.asynchers.compassflix.repositories.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class MovieServiceTest {

    private static final String ID = "1234";
    private static final Integer INDEX = 0;
    private static final String NAME = "Movie 3";
    private static final String DESCRIPTION = "blablabla";
    private static final String GENRE = "Action";
    private static final Long DURATION = 120L;
    private static final LocalDate RELEASE_DATE = LocalDate.parse("2001-01-01");
    private static final String PG_RATING = "pg-2";
    private static final Instant REGISTRATION_DATE = Instant.now();

    private static final String OBJECT_NOT_FOUND = "Movie not found!";


    @InjectMocks
    private MovieService service;

    @Mock
    private MovieRepository repository;

    @Mock
    private ModelMapper mapper;

    private Movie movie;

    private MovieRequestDto movieRequestDto;
    private MovieResponseDto movieResponseDto;
    private Optional<Movie> optionalMovie;


    @BeforeEach
    void setUp() {
        startMovie();
    }

    private void startMovie() {
        movie = new Movie(ID, NAME, DESCRIPTION, GENRE, DURATION, RELEASE_DATE, PG_RATING, REGISTRATION_DATE);
        movieResponseDto = new MovieResponseDto(ID, NAME, DESCRIPTION, GENRE, DURATION, RELEASE_DATE,
                PG_RATING, REGISTRATION_DATE);
        movieRequestDto = new MovieRequestDto(NAME, DESCRIPTION, GENRE, DURATION, RELEASE_DATE, PG_RATING);
        optionalMovie = Optional.of(new Movie(ID, NAME, DESCRIPTION, GENRE, DURATION, RELEASE_DATE,
                PG_RATING, REGISTRATION_DATE));
    }

    @Test
    void findAllMovies() {
        when(repository.findAll()).thenReturn(List.of(movie));

        List<Movie> response = service.findAllMovies();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(Movie.class, response.get(INDEX).getClass());

        assertEquals(ID, response.get(INDEX).getId());
        assertEquals(NAME, response.get(INDEX).getName());
        assertEquals(DESCRIPTION, response.get(INDEX).getDescription());
        assertEquals(GENRE, response.get(INDEX).getGenre());
        assertEquals(DURATION, response.get(INDEX).getDuration());
        assertEquals(RELEASE_DATE, response.get(INDEX).getReleaseDate());
        assertEquals(PG_RATING, response.get(INDEX).getPgRating());
        assertEquals(REGISTRATION_DATE, response.get(INDEX).getRegistrationDate());
    }

    @Test
    void findMovieById() {
        when(repository.findById(anyString())).thenReturn(optionalMovie);

        Movie response = service.findMovieById(ID);

        assertNotNull(response);

        assertEquals(Movie.class, response.getClass());
        assertEquals(ID, response.getId());
        assertEquals(NAME, response.getName());
        assertEquals(DESCRIPTION, response.getDescription());
        assertEquals(GENRE, response.getGenre());
        assertEquals(DURATION, response.getDuration());
        assertEquals(RELEASE_DATE, response.getReleaseDate());
        assertEquals(PG_RATING, response.getPgRating());
        assertEquals(REGISTRATION_DATE, response.getRegistrationDate());
    }

    @Test
    void searchByName() {
        when(repository.findByName(anyString())).thenReturn(movie);

        Movie response = service.searchByName(NAME);

        assertNotNull(response);

        assertEquals(Movie.class, response.getClass());
        assertEquals(ID, response.getId());
        assertEquals(NAME, response.getName());
        assertEquals(DESCRIPTION, response.getDescription());
        assertEquals(GENRE, response.getGenre());
        assertEquals(DURATION, response.getDuration());
        assertEquals(RELEASE_DATE, response.getReleaseDate());
        assertEquals(PG_RATING, response.getPgRating());
        assertEquals(REGISTRATION_DATE, response.getRegistrationDate());
    }

    @Test
    void postMovie() {
        when(repository.save(any())).thenReturn(movie);

        MovieResponseDto response = service.postMovie(movieRequestDto);

        assertNotNull(response);

        assertEquals(MovieResponseDto.class, response.getClass());

        assertEquals(ID, response.id());
        assertEquals(NAME, response.name());
        assertEquals(DESCRIPTION, response.description());
        assertEquals(GENRE, response.genre());
        assertEquals(DURATION, response.duration());
        assertEquals(RELEASE_DATE, response.releaseDate());
        assertEquals(PG_RATING, response.pgRating());
        assertEquals(REGISTRATION_DATE, response.registrationDate());

    }

    @Test
    void updateMovie() {

        // mocking data of existing movie and changes to be updated
        String id = "1";
        MovieRequestDto movieRequestDto1 = new MovieRequestDto("Updated Movie", "Updated description",
                "Drama", 150L,LocalDate.of(2020, 1, 1), "pg-13");

        Movie existingMovie = new Movie();
        existingMovie.setId(id);
        existingMovie.setName("Movie 1");
        existingMovie.setDescription("Original description");
        existingMovie.setGenre("Action");
        existingMovie.setDuration(120L);
        existingMovie.setReleaseDate(LocalDate.of(2015, 12, 12));
        existingMovie.setPgRating("pg-2");

        Movie updatedMovie = new Movie();
        updatedMovie.setId(id);
        updatedMovie.setName(movieRequestDto1.name());
        updatedMovie.setDescription(movieRequestDto1.description());
        updatedMovie.setGenre(movieRequestDto1.genre());
        updatedMovie.setDuration(movieRequestDto1.duration());
        updatedMovie.setReleaseDate(movieRequestDto1.releaseDate());
        updatedMovie.setPgRating(movieRequestDto1.pgRating());

        // find by id and update
        when(repository.findById(id)).thenReturn(Optional.of(existingMovie));
        when(repository.save(existingMovie)).thenReturn(updatedMovie);

        MovieResponseDto result = service.updateMovie(id, movieRequestDto1);

        // verify if update occurs right and if the result is correct
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(existingMovie);

        assertEquals(updatedMovie.getId(), result.id());
        assertEquals(updatedMovie.getName(), result.name());
        assertEquals(updatedMovie.getDescription(), result.description());
        assertEquals(updatedMovie.getGenre(), result.genre());
        assertEquals(updatedMovie.getDuration(), result.duration());
        assertEquals(updatedMovie.getReleaseDate(), result.releaseDate());
        assertEquals(updatedMovie.getPgRating(), result.pgRating());
    }

//
//    @Test
//    void delete() {
//    }
}