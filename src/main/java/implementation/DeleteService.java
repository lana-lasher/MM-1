package implementation;

import org.springframework.http.ResponseEntity;
import utilities.DbConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

abstract class DeleteService {
    Connection connection;
    Statement statement;

    DeleteService(){
        try {
            this.connection= DbConnection.getConnection();
            this.statement = connection.createStatement();

        }catch (Exception e){
            e.printStackTrace();
        }
    };

    abstract ResponseEntity<String> delete (long id) throws SQLException;

    public final void closeConn() throws SQLException {
        this.connection.close();
        this.statement.close();
    }


}
