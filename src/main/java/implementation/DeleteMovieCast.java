package implementation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;

public class DeleteMovieCast extends DeleteService{
    @Override
    public ResponseEntity<String> delete(long movieCastId) throws SQLException {

        String sql = "delete from movie_cast where movie_cast_id = "+movieCastId;

        try{
              statement.executeUpdate(sql);
        }catch (SQLException sqlException) {
            String errorMsg = sqlException.getErrorCode() + " " + sqlException.getMessage();
            return new ResponseEntity<String>("Error on delete movie cast: " + errorMsg, HttpStatus.BAD_REQUEST);
        } finally {
            closeConn();
        }
        return new ResponseEntity<String>("Successfully deleted movie cast", HttpStatus.OK);
    }
}
