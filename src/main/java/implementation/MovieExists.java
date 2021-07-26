package implementation;

import java.sql.ResultSet;

public class MovieExists extends IsRecordInDb{

    public boolean exists (String movie) throws Exception {
        String sql = "select movie_name from movies where movie_name = '"+movie.trim()+"'";
        ResultSet result = statement.executeQuery(sql);
        if (result.next()) {
            return true;
        }

        closeConn();
        return false;
    }
}
