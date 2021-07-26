package implementation;

import java.sql.ResultSet;

public class StudioExists extends IsRecordInDb{

    public boolean exists (String name) throws Exception {
        String sql = "select studio_name from movie_studio where studio_name = '"+name.trim()+"'";
        ResultSet result = statement.executeQuery(sql);
        if (result.next()) {
            return true;
        }
        closeConn();
        return false;
    }
}