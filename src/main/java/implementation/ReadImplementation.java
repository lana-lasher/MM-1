package implementation;

import dto.MovieCastDto;
import dto.MoviesDto;
import lombok.extern.slf4j.Slf4j;
import utilities.DbConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ReadImplementation  {
    private List<MovieCastDto> getMovieCast(Long movieId) throws Exception {
        //get movie cast
        log.info("getting movie cast");
        List<MovieCastDto> movieCastList = new ArrayList<>();
        Connection conn = DbConnection.getConnection();
        Statement castStatement = conn.createStatement();
        String castSql = "select movie_id, actor_name, character_name " +
                "from movie_cast where movie_id ="+movieId;
        ResultSet castResult = castStatement.executeQuery(castSql);
        while (castResult.next()) {
            MovieCastDto.MovieCastDtoBuilder movieCast = null;
            movieCast = MovieCastDto.builder()
                    .movieCastId(castResult.getLong("movie_cast_id"))
                    .movieId(castResult.getLong("movie_Id"))
                    .actorName(castResult.getString("actor_name"))
                    .characterName(castResult.getString("character_name"));

            movieCastList.add(movieCast.build());
        }
        conn.close();
        castStatement.close();
        return movieCastList;

    }

    private List<MoviesDto> getMovies(Long studioId,boolean addCast) throws Exception {
        log.info("inside get all movie data");
        List<MoviesDto> movieList = new ArrayList<>();
        //get movie list
        Connection conn = DbConnection.getConnection();
        Statement moviesStatement = conn.createStatement();
        String moviesSql = "select movie_id,movie_name,release_date from movies where studio_id=" + studioId;
        ResultSet moviesResult = moviesStatement.executeQuery(moviesSql);

        while (moviesResult.next()) {
            MoviesDto.MoviesDtoBuilder movies ;
            movies = MoviesDto.builder()
                    .movieId(moviesResult.getLong("movie_id"))
                    .movieName(moviesResult.getString("movie_name"))
                    .releaseDate(moviesResult.getString("release_date"));
                  //  .movieCastDtoList(getMovieCast(moviesResult.getLong("movie_id")));

            if (addCast)
            movies.movieCastDtoList(getMovieCast(moviesResult.getLong("movie_id")));

            movieList.add(movies.build());
        }
        conn.close();
        moviesStatement.close();
        return movieList;
    }

    /*private List<MovieStudioDto> getStudio(Long studioId) throws Exception {
        List<MovieStudioDto> studioList = new ArrayList<>();
        //get studios

        Connection conn = DbConnection.getConnection();
        Statement studioStatement = conn.createStatement();
        String studioSql = "select studio_id,studio_name from movie_studio where studio_id=" + studioId;
        ResultSet studioResult = studioStatement.executeQuery(studioSql);

        while(studioResult.next()) {
            log.info("getting studio data");
            MovieStudioDto.MovieStudioDtoBuilder studioMovies ;
            studioMovies = MovieStudioDto.builder()
                    .studioId(studioResult.getLong("studio_id"))
                    .studioName(studioResult.getString("studio_name"))
                    .movies(getMovies(studioResult.getLong("studio_id")));

                     studioList.add(studioMovies.build());
                 }
        conn.close();
        studioStatement.close();
        return studioList;
    }*/

    public List<MovieCastDto> getCastInfo(Long movieId) throws Exception{
        List<MovieCastDto> cList = getMovieCast(movieId);
         log.info("studio data size "+cList.size());
        return cList;
    }

    public List<MoviesDto> getMovieData(Long studioId,boolean addCast) throws Exception{
        List<MoviesDto> mList = getMovies(studioId,true);
        log.info("movie data size "+mList.size());
        return mList;
    }


}
