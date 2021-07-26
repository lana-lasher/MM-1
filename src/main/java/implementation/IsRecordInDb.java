package implementation;

import utilities.DbConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

abstract class IsRecordInDb {

    Connection connection;
    Statement statement;

    IsRecordInDb(){
        try {
            this.connection= DbConnection.getConnection();
            this.statement = connection.createStatement();

        }catch (Exception e){
            e.printStackTrace();
        }
    };

     abstract boolean exists (String name) throws Exception;

     public final void closeConn() throws SQLException {
         this.connection.close();
         this.statement.close();
     }


}
