package utilities;

import java.sql.Connection;
import java.sql.DriverManager;

 public class DbConnection {
      public static Connection getConnection() throws Exception {
        Connection conn = DriverManager.
                //      getConnection("jdbc:h2:~/test", "sa", "");
                        getConnection("jdbc:h2:tcp://localhost/~/test","sa","");
        return conn;
    }
}
