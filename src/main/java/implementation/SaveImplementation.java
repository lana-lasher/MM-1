package implementation;

import dto.MovieCastDto;
import dto.MovieStudioDto;
import dto.MoviesDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import utilities.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class SaveImplementation {

        public ResponseEntity<String> addStudios(List<MovieStudioDto> studioList) throws Exception {
        //remove any possible duplicates
        Set<MovieStudioDto> noDupsStudioList = new HashSet<>();
        noDupsStudioList.addAll(studioList);

        String errorMsg="successfully inserted studio";
        Connection  conn = DbConnection.getConnection();

        try {

            String sql = "insert into movie_studio (studio_id,studio_name) values (movies_studio_seq.nextval, ?)";

            PreparedStatement  createStudio = conn.prepareStatement(sql);
            createStudio.getConnection();

            for(MovieStudioDto s : noDupsStudioList) {
                StudioExists studioExists = new StudioExists();
                if (studioExists.exists(s.getStudioName()))
                    return new ResponseEntity<String>("Error: Studio Name "+s.getStudioName()+" already exists",HttpStatus.BAD_REQUEST);
                createStudio.setString(1,s.getStudioName());
                createStudio.addBatch();
            }
              createStudio.executeBatch();
              createStudio.close();
        }catch (SQLException sqlException) {
             errorMsg = sqlException.getErrorCode() + " " + sqlException.getMessage();
            conn.rollback();
            return new ResponseEntity<String>("Error inserting studio: " + errorMsg, HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            errorMsg = "Generic exception on inserting studio: "+e.getMessage();
            return new ResponseEntity<String>("Error inserting studio: " + errorMsg, HttpStatus.BAD_REQUEST);
        }finally {
            conn.close();
        }

        return new ResponseEntity<String>(errorMsg, HttpStatus.OK);

    }

    public ResponseEntity<String> addMovieCast(List<MovieCastDto> castList, long movieId, Connection conn) throws Exception {
        log.info("in addMovieCast");

        Set<MovieCastDto> noDupsCastList = new HashSet<>();
        noDupsCastList.addAll(castList);

        if (conn == null) {
            log.info("getting new connection");
            conn = DbConnection.getConnection();
        }

         String sql = "insert into movie_cast(movie_cast_id, movie_id,actor_name,character_name) values (movie_cast_seq.nextval,?, ?, ?)";

        PreparedStatement  createMovieCast = conn.prepareStatement(sql);
        createMovieCast.getConnection();

        try {
            for(MovieCastDto c : noDupsCastList) {

                MovieCastExists castExists = new MovieCastExists();
                 if (castExists.exists(c.getActorName().trim()+c.getCharacterName().trim()))
                     return new ResponseEntity<String>("Error: Cast "+c.getActorName()+" / "+c.getCharacterName()+ " already exists",HttpStatus.BAD_REQUEST);

                    createMovieCast.setLong(1,movieId);
                    createMovieCast.setString(2, c.getActorName());
                    createMovieCast.setString(3, c.getCharacterName());

                   createMovieCast.addBatch();

                }
                createMovieCast.executeBatch();
                createMovieCast.close();

        }catch (SQLException sqlException) {
            String errorMsg = sqlException.getErrorCode() + " " + sqlException.getMessage();
            conn.rollback();
            return new ResponseEntity<String>("Error inserting movie cast: " + errorMsg, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<String>("successfully added movie cast", HttpStatus.OK);

    }

    public ResponseEntity<String> addMoviesToStudio(Long studioId, List<MoviesDto> movieList,boolean addCast) throws Exception{
        log.info("in addMoviesToStudio");
        long movieIdSeq=0;
        Connection conn = DbConnection.getConnection();
        conn.setAutoCommit(false);
        String sql = "insert into movies(movie_id,movie_name,release_date,studio_id) values (?, ?, ?,?)";
        String getSeqValue = "select movies_seq.nextval from dual";

        PreparedStatement getSeqVal = conn.prepareStatement(getSeqValue);
        ResultSet movieIdSeqResult =  getSeqVal.executeQuery();

        while (movieIdSeqResult.next()){
            movieIdSeq = movieIdSeqResult.getLong(1);
        }
        getSeqVal.close();
        PreparedStatement addMovies = conn.prepareStatement(sql);

        Set<MoviesDto> noDupsList = new HashSet<>();
        noDupsList.addAll(movieList);
        ResponseEntity<String> addCastResp = null;

        try {
            for(MoviesDto movie : noDupsList) {

                    ResponseEntity<String> validMovieRec = isMovieValid(movie);
                    if (validMovieRec.getStatusCode().value() !=200 && !validMovieRec.getBody().contains("success"))
                       return validMovieRec;

                    addMovies.setLong(1,movieIdSeq);
                    addMovies.setString(2, movie.getMovieName());
                    addMovies.setDate(3, java.sql.Date.valueOf(movie.getReleaseDate()));
                    addMovies.setLong(4, studioId);

                    int addMovieSuccess = addMovies.executeUpdate();
                    addMovies.close();
                    log.info("inserted "+addMovieSuccess+" into db");
                    if (addMovieSuccess>0 && addCast) {
                         addCastResp = addMovieCast(movie.getMovieCastDtoList(), movieIdSeq, conn);

                        if (addCastResp.getStatusCode().value() != 200 && !addCastResp.getBody().contains("success"))
                            return addCastResp;
                    }

                   conn.commit();

            }

        }catch (SQLException sqlException){
            String errorMsg = sqlException.getErrorCode()+" "+ sqlException.getMessage();
            conn.rollback();
            return new ResponseEntity<String>("error"+errorMsg, HttpStatus.EXPECTATION_FAILED);

        }catch (Exception e){
            conn.rollback();
           log.info("Generic exception error: "+ e.getMessage());
        }finally {
            conn.close();
        }

        return new ResponseEntity<String> ("successfully inserted movie(s)",HttpStatus.OK);
    }

    private ResponseEntity<String> isMovieValid(MoviesDto movie) throws Exception {
        String error = "success";

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            inputFormat.parse(movie.getReleaseDate());
        }catch (Exception e){
            error = "Error on Movie Release Date. Invalid Date format. Must be yyyy-MM-dd";
        }

        MovieExists movieExists = new MovieExists();
        if (movieExists.exists(movie.getMovieName()))
            error = "Movie is already in the database";

        return new ResponseEntity<String>(error, HttpStatus.BAD_REQUEST);

    }

}
