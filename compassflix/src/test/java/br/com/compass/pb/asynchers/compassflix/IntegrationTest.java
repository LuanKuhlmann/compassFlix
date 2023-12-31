package br.com.compass.pb.asynchers.compassflix;

import br.com.compass.pb.asynchers.compassflix.controllers.MovieController;
import br.com.compass.pb.asynchers.compassflix.dto.request.MovieRequestDto;
import br.com.compass.pb.asynchers.compassflix.dto.response.MovieResponseDto;
import br.com.compass.pb.asynchers.compassflix.entities.Movie;
import br.com.compass.pb.asynchers.compassflix.exceptions.ListIsEmptyException;
import br.com.compass.pb.asynchers.compassflix.exceptions.MovieAlreadyExistException;
import br.com.compass.pb.asynchers.compassflix.exceptions.MovieNotFoundException;
import br.com.compass.pb.asynchers.compassflix.services.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieController controller;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MovieService service;

    private ObjectMapper mapper;


    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Test
    void shouldBeAbleToFindAllMovies() throws Exception {

        Movie movie = new Movie();
        movie.setId("64b1e14a36a86833234f6a42");
        movie.setName("Avengers");
        movie.setDescription("Heroes fighting");
        movie.setGenre("Action");
        movie.setDuration(120L);
        movie.setReleaseDate(LocalDate.parse("2022-10-10"));
        movie.setPgRating("pg-17");
        movie.setRegistrationDate(Instant.parse("2023-07-14T19:13:25.465Z"));

        when(service.findAllMovies()).thenReturn(List.of(movie));

        String responseExpected = "[{\"id\":\"64b1e14a36a86833234f6a42\"," +
                "\"name\":\"Avengers\"," +
                "\"description\":\"Heroes fighting\"," +
                "\"genre\":\"Action\"," +
                "\"duration\":120," +
                "\"releaseDate\":[2022,10,10]," +
                "\"pgRating\":\"pg-17\"," +
                "\"registrationDate\":1689362005.465000000}]";

        // Realiza a requisição GET para o endpoint /compassflix/movies
        mockMvc.perform(get("/compassflix/movies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(responseExpected))
                .andExpect(status().isOk())
                .andReturn();

        verify(service, times(1)).findAllMovies();
    }

    @Test
    void shouldReturnAnListIsEmptyException() throws Exception {

        when(service.findAllMovies()).thenThrow(new ListIsEmptyException("No movies found!"));

        mockMvc.perform(get("/compassflix/movies"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ListIsEmptyException))
                .andExpect(result -> assertEquals("No movies found!", result.getResolvedException().getMessage()))
                .andReturn();

        verify(service, times(1)).findAllMovies();
    }

    @Test
    void shouldBeAbleToFindMovieById() throws Exception {

        Movie movie = new Movie();
        movie.setId("64b1e14a36a86833234f6a42");
        movie.setName("Avengers");
        movie.setDescription("Heroes fighting");
        movie.setGenre("Action");
        movie.setDuration(120L);
        movie.setReleaseDate(LocalDate.parse("2022-10-10"));
        movie.setPgRating("pg-17");
        movie.setRegistrationDate(Instant.parse("2023-07-14T19:13:25.465Z"));

        when(service.findMovieById("64b1e14a36a86833234f6a42")).thenReturn((movie));

        String responseExpected = "{\"id\":\"64b1e14a36a86833234f6a42\"," +
                "\"name\":\"Avengers\"," +
                "\"description\":\"Heroes fighting\"," +
                "\"genre\":\"Action\"," +
                "\"duration\":120," +
                "\"releaseDate\":[2022,10,10]," +
                "\"pgRating\":\"pg-17\"," +
                "\"registrationDate\":1689362005.465000000}";

        mockMvc.perform(get("/compassflix/movies/64b1e14a36a86833234f6a42")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(responseExpected))
                .andExpect(status().isOk())
                .andReturn();

        verify(service, times(1)).findMovieById("64b1e14a36a86833234f6a42");
    }

    @Test
    void shouldReturnAnMovieNotFoundException() throws Exception {

        when(service.findMovieById("id-not-found")).thenThrow(new MovieNotFoundException("That movie doesn't exist!"));

        mockMvc.perform(get("/compassflix/movies/id-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MovieNotFoundException))
                .andExpect(result -> assertEquals("That movie doesn't exist!", result.getResolvedException().getMessage()))
                .andReturn();

        verify(service, times(1)).findMovieById("id-not-found");
    }

    @Test
    void shouldBeAbleToFindMovieByName() throws Exception {

        Movie movie = new Movie();
        movie.setId("64b1e14a36a86833234f6a42");
        movie.setName("Avengers");
        movie.setDescription("Heroes fighting");
        movie.setGenre("Action");
        movie.setDuration(120L);
        movie.setReleaseDate(LocalDate.parse("2022-10-10"));
        movie.setPgRating("pg-17");
        movie.setRegistrationDate(Instant.parse("2023-07-14T19:13:25.465Z"));

        List<Movie> movieList = List.of(movie);

        when(service.findByName("Avengers")).thenReturn(movieList);

        String jsonResponse = this.mapper.writeValueAsString(movieList);

        mockMvc.perform(get("/compassflix/movies/search").param("name", "Avengers"))
                .andExpect(content().json(jsonResponse))
                .andExpect(status().isOk()).andExpect(jsonPath("$").isNotEmpty())
                .andReturn();

        verify(service, times(1)).findByName("Avengers");
    }

    @Test
    void shouldBeAbleToCreateAMovie() throws Exception {

        MovieRequestDto movieRequestDto = new MovieRequestDto("Avengers", "Heroes fighting", "Action",
                120L, LocalDate.parse("2022-10-10"), "pg-17");

        Movie movie = new Movie();
        movie.setId("64b1e14a36a86833234f6a42");
        movie.setName("Avengers");
        movie.setDescription("Heroes fighting");
        movie.setGenre("Action");
        movie.setDuration(120L);
        movie.setReleaseDate(LocalDate.parse("2022-10-10"));
        movie.setPgRating("pg-17");
        movie.setRegistrationDate(Instant.parse("2023-07-14T19:13:25.465Z"));

        MovieResponseDto movieResponseDto = new MovieResponseDto(movie);

        when(service.postMovie(movieRequestDto)).thenReturn(movieResponseDto);

        mockMvc.perform(post("/compassflix/movies/", "64b1e14a36a86833234f6a42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieRequestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        verify(service, times(1)).postMovie(movieRequestDto);

    }

    @Test
    void shouldReturnMovieAlreadyExistException() throws Exception {
        MovieRequestDto movieRequestDto = new MovieRequestDto("Avengers", "Heroes fighting", "Action",
                120L, LocalDate.parse("2022-10-10"), "pg-17");

        when(service.postMovie(movieRequestDto))
                .thenThrow(new MovieAlreadyExistException("That movie already exists!"));

        mockMvc.perform(post("/compassflix/movies/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieRequestDto)))
                .andExpect(status().isConflict())
                .andReturn();

        verify(service, times(1)).postMovie(movieRequestDto);
    }

    @Test
    void shouldBeAbleToUpdateAMovie() throws Exception {

        MovieRequestDto movieRequestDto = new MovieRequestDto(
                "Avengers",
                "Heroes fighting",
                "Action",
                120L,
                LocalDate.parse("2022-10-10"),
                "pg-17");


        Movie movie = new Movie();
        movie.setId("64b1e14a36a86833234f6a42");
        movie.setName("Avengers");
        movie.setDescription("Heroes fighting");
        movie.setGenre("Action");
        movie.setDuration(120L);
        movie.setReleaseDate(LocalDate.parse("2022-10-10"));
        movie.setPgRating("pg-17");
        movie.setRegistrationDate(Instant.parse("2023-07-14T19:13:25.465Z"));


        MovieResponseDto movieResponseDto = new MovieResponseDto(movie);

        when(service.updateMovie("64b1e14a36a86833234f6a42", movieRequestDto)).thenReturn(movieResponseDto);

        String jsonResponse = objectMapper.writeValueAsString(movieResponseDto);

        String responseBody = "{\"id\":\"64b1e14a36a86833234f6a42\"," +
                "\"name\":\"Avengers\"," +
                "\"description\":\"Heroes fighting\"," +
                "\"genre\":\"Action\"," +
                "\"duration\":120," +
                "\"releaseDate\":\"2022-10-10\"," +
                "\"pgRating\":\"pg-17\"," +
                "\"registrationDate\":\"2023-07-14T19:13:25.465Z\"}";


        mockMvc.perform(put("/compassflix/movies/{id}", "64b1e14a36a86833234f6a42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieRequestDto)))
                .andExpect(status().isOk())
                .andReturn();


        verify(service, times(1)).updateMovie("64b1e14a36a86833234f6a42", movieRequestDto);

        assertEquals(responseBody, jsonResponse);
    }


    @Test
    void shouldReturnAnExceptionWhenCannotUpdate() throws Exception {
        String invalidId = "invalid_id";

        when(service.updateMovie(eq(invalidId), any())).thenThrow(MovieNotFoundException.class);

        mockMvc.perform(put("/compassflix/movies/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(service, times(1)).updateMovie(eq(invalidId), any());
    }

    @Test
    void shouldBeAbleToDeleteMovie() throws Exception {

        String id = "64b1e14a36a86833234f6a42";

        mockMvc.perform(delete("/compassflix/movies/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(service, times(1)).delete((id));

    }
    @Test
    void shouldReturnAnExceptionWhenCannotDeleteMovie() throws Exception {
        String invalidId = "64b1e14a36a86833234f6a42";

        doThrow(new MovieNotFoundException("That movie doesn't exist!")).when(service).delete(invalidId);

        mockMvc.perform(delete("/compassflix/movies/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals("That movie doesn't exist!",
                        requireNonNull(result.getResolvedException()).getMessage()))
                .andReturn();

        verify(service, times(1)).delete(invalidId);
    }

}
