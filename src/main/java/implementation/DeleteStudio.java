package implementation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;

public class DeleteStudio extends DeleteService {

    @Override
    public ResponseEntity<String> delete(long studioId) throws SQLException {

            String sql = "delete from movie_studio where studio_id = "+studioId;
            try{
                statement.executeUpdate(sql);
            }catch (SQLException sqlException) {
                String errorMsg = sqlException.getErrorCode() + " " + sqlException.getMessage();
                return new ResponseEntity<String>("Error delete studio: " + errorMsg, HttpStatus.BAD_REQUEST);
            } finally {
                closeConn();
            }
            return new ResponseEntity<String>("Successfully deleted studio", HttpStatus.OK);
        }
}
