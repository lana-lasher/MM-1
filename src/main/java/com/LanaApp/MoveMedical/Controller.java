package com.LanaApp.MoveMedical;

import dto.MovieCastDto;
import dto.MovieStudioDto;
import dto.MoviesDto;
import implementation.DeleteMovie;
import implementation.DeleteMovieCast;
import implementation.DeleteStudio;
import implementation.ReadImplementation;
import implementation.SaveImplementation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequestMapping(value = {"/LanaMoveMedicalApp"})
@Slf4j
public class Controller {

    @RequestMapping(value = "/getCastForMovie/{movieId}", method = RequestMethod.GET, produces = "application/json")
    public List<MovieCastDto> getAllMoviesInStudio(@PathVariable Long movieId) throws Exception {
        log.info("Inside get all movies");
        ReadImplementation readService = new ReadImplementation();
       return readService.getCastInfo(movieId);
    }

    @RequestMapping(value = "/getAllMoviesWithCastForStudio/{studioId}", method = RequestMethod.GET, produces = "application/json")
    public List<MoviesDto> getMovieWithCast(@PathVariable Long studioId)  throws Exception {
        log.info("Inside get all movies");
        ReadImplementation readService = new ReadImplementation();
        return readService.getMovieData(studioId,true);
    }

    @RequestMapping(value = "/getAllMoviesForStudio/{studioId}", method = RequestMethod.GET, produces = "application/json")
    public List<MoviesDto> getMovieInfo(@PathVariable Long studioId)  throws Exception {
        log.info("Inside get all movies");
        ReadImplementation readService = new ReadImplementation();
        return readService.getMovieData(studioId,false);
    }


    @RequestMapping(value = "/addMovieCast/{movieId}", method = RequestMethod.POST)
    public ResponseEntity<String> addMovieCast(@Valid @RequestBody List<MovieCastDto> movieCastDtoList,@PathVariable long movieId)  throws Exception {
        log.info("adding movie cast to movie "+movieId);
        SaveImplementation saveService = new SaveImplementation();
        return saveService.addMovieCast(movieCastDtoList,movieId, null);
    }

    @RequestMapping(value = "/addMoviesWithCastToStudio/{studioId}", method = RequestMethod.POST)
    public ResponseEntity<String> addMoviesWithCast(@PathVariable Long studioId,
                                            @Valid @RequestBody List<MoviesDto> movieList)  throws Exception {
        log.info("controller: adding movies with cast to studio"+studioId);
        SaveImplementation saveService = new SaveImplementation();
        return saveService.addMoviesToStudio(studioId,movieList,true);
    }

    @RequestMapping(value = "/addMoviesToStudio/{studioId}", method = RequestMethod.POST)
    public ResponseEntity<String> addMovies(@PathVariable Long studioId,
                                            @Valid @RequestBody List<MoviesDto> movieList)  throws Exception {
        log.info("controller: adding movies to studio "+studioId);
        SaveImplementation saveService = new SaveImplementation();
        return saveService.addMoviesToStudio(studioId,movieList,false);
    }

    @RequestMapping(value = "/addStudios", method = RequestMethod.POST)
    public ResponseEntity<String> addStudio(@RequestBody @Valid List<MovieStudioDto> studioList) throws Exception {
        log.info("controller: adding studios");
        SaveImplementation saveService = new SaveImplementation();
        return saveService.addStudios(studioList);

    }

    @RequestMapping(value = "/deleteStudio/{studioId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteStudio(@PathVariable long studioId)  throws Exception {
        log.info("controller: delete studio "+studioId);
        DeleteStudio deleteStudio = new DeleteStudio();
        return deleteStudio.delete(studioId);
    }

    @RequestMapping(value = "/deleteMovie/{movieId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteMovie(@PathVariable long movieId)  throws Exception {
        log.info("controller: delete movie "+movieId);
        DeleteMovie deleteMovie = new DeleteMovie();
        return deleteMovie.delete(movieId);
    }

    @RequestMapping(value = "/deleteMovieCast/{movieCastId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteMovieCast(@PathVariable long movieCastId)  throws Exception {
        log.info("controller: delete movie cast "+movieCastId);
        DeleteMovieCast deleteCast = new DeleteMovieCast();
        return deleteCast.delete(movieCastId);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {

        return  new ResponseEntity<String>(ex.getMessage(),HttpStatus.BAD_REQUEST);
    }




}