package implementation;

import java.sql.ResultSet;

public class MovieCastExists extends IsRecordInDb{

    public boolean exists (String name) throws Exception {
        String sql = "select actor_name from movie_cast where actor_name||character_name = '"+name.trim()+"'";
        ResultSet result = statement.executeQuery(sql);
        if (result.next()) {
            return true;
        }
        closeConn();
        return false;
    }
}