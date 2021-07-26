package implementation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;

public class DeleteMovie extends DeleteService {

    @Override
    public ResponseEntity<String> delete(long movieId) throws SQLException {

        String sql = "delete from movies where movie_id = "+movieId;
        try{
            statement.executeUpdate(sql);
        }catch (SQLException sqlException) {
            String errorMsg = sqlException.getErrorCode() + " " + sqlException.getMessage();
            return new ResponseEntity<String>("Error on delete movie: " + errorMsg, HttpStatus.BAD_REQUEST);
        } finally {
            closeConn();
        }
        return new ResponseEntity<String>("Successfully deleted movie", HttpStatus.OK);
    }
}