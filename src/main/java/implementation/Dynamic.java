package implementation;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import implementation.dto.World;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import utilities.DbConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Slf4j
public class Dynamic {

    LinkedHashSet<World> storageSet = new LinkedHashSet<>();

    public void parseInput(String json) throws JsonProcessingException {
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);

        JsonNode rootNode = mapper.readTree(json);

        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode.fields();

            while (fieldsIterator.hasNext()) {
                parseNodeArray(fieldsIterator.next());
            }


        try {
            addToDb(storageSet);
        } catch (Exception e) {
            log.info("error with db insert " + e.getMessage());
        }

    }

        public String parseNodeArray(Map.Entry<String, JsonNode> j)  {

        try {
            for (JsonNode n : j.getValue()) {
                String type = j.getKey();
                log.info("key " + type);
                World w = new World();
                Iterator<Map.Entry<String, JsonNode>> a = n.fields();
                while (a.hasNext()) {
                    Map.Entry<String, JsonNode> node = a.next();
                    if (node.getValue().isArray()) {
                        parseNodeArray(node);

                    } else {
                        if (n.get("id") != null)
                            w.setId(n.get("id").asLong());
                        if(n.get("type") !=null)
                            w.setType(n.get("type").toString());
                        if (n.get("name") != null)
                            w.setName(n.get("name").toString());
                        if (n.get("parentId") != null)
                            w.setParentId(n.get("parentId").asLong());

                           storageSet.add(w);
                    }

                }
            }

        } catch (Exception e){
            e.printStackTrace();
            log.info("generic exception caught: "+e.getMessage());
        }


        return "ok";
    }


    private ResponseEntity<String> addToDb(LinkedHashSet<World> data) throws Exception {
        String errorMsg = "successfully inserted records to database";
        Connection conn = DbConnection.getConnection();

        try {

            String sql = "insert into world (id,type,name,parent_id) values (?,?,?,?)";

            PreparedStatement createStudio = conn.prepareStatement(sql);
            createStudio.getConnection();

            for (World w : data) {
                log.info("type " + w.getType() + " name " + w.getName() + " parentId " + w.getParentId());

                createStudio.setLong(1, w.getId());
                createStudio.setString(2, w.getType());
                createStudio.setString(3, w.getName());
                if (w.getParentId() != null)
                    createStudio.setLong(4, w.getParentId());
                else
                    createStudio.setBigDecimal(4, null);

                createStudio.addBatch();
            }

            createStudio.executeBatch();
            createStudio.close();
        } catch (
                SQLException sqlException) {

            errorMsg = sqlException.getErrorCode() + " " + sqlException.getMessage();
            log.info("error " + errorMsg);
            conn.rollback();
            return new ResponseEntity<String>("Error inserting studio: " + errorMsg, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.info("generic exception " + e.getMessage());
            errorMsg = "Generic exception on inserting studio: " + e.getMessage();
            return new ResponseEntity<String>("Error inserting studio: " + errorMsg, HttpStatus.BAD_REQUEST);
        } finally {
            conn.close();
        }

        return new ResponseEntity<String>(errorMsg, HttpStatus.OK);

    }

    public  LinkedHashMap<String,List<World>> getAllByParentId(BigDecimal parentId) throws Exception {
        Connection conn = DbConnection.getConnection();
        Statement s = conn.createStatement();
        String getRootString ;
        if (parentId == null)
            getRootString = "where parent_id is null";
        else
            getRootString =" where parent_id = "+parentId ;

        String getAllByParentIdSql = "with " +
                "  world_h ( id, parent_id,type,name,lvl) as ( " +
                "    select  id, parent_id,type,name, 0 as lvl" +
                "      from world " + getRootString +
                "    union all" +
                "    select  w.id, w.parent_id, w.type, w.name, lvl + 1 as lvl " +
                "      from  world w , world_h where w.parent_id = world_h.id " +
                "  ) " +
                "select * " +
                "from   world_h order by lvl desc;";
        ResultSet resultSet = s.executeQuery(getAllByParentIdSql);

        int level = 0;

        LinkedHashMap<String,List<World>> innerTree = new LinkedHashMap<>();

        List<World> worldList = new ArrayList<>();
        while (resultSet.next()) {
            level = resultSet.getInt("lvl");
            World w = new World();
            w.setId(resultSet.getLong("id"));
            w.setParentId(resultSet.getLong("parent_id"));
            w.setName(resultSet.getString("name"));
            w.setType(resultSet.getString("type"));
            w.setLevel(level);

            worldList = buildTreeReverse(w,worldList);
        }

        return innerTree;

       }

       private List<World> buildTreeReverse(World w,List<World> worldList){
           if (worldList.isEmpty()) {
               log.info("empty");
               worldList.add(w);
               return worldList;
           }

           for (World listObj : worldList) {

               if (listObj.getLevel() == w.getLevel()) {
                   if (listObj.getParentId() == w.getParentId())
                   worldList.add(w);
                   return worldList;
               }

               if (w.getLevel() < listObj.getLevel()) {
                   log.info("new parent id "+w.getParentId() + " vs "+ listObj.getParentId());
                   if (w.getId() == listObj.getParentId()) {
                       log.info("parent level ");
                       w.setObjectList(worldList);
                       List<World> newList = new ArrayList<>();
                       newList.add(w);
                       return newList;
                   }
                   else {
                       log.info("parent id didn't match the id");
                   }

               }
           }

             return worldList;
       }


    public ResponseEntity<String> deleteAllByParentId(long parentId) throws Exception {
        Connection conn = DbConnection.getConnection();
        Statement s = conn.createStatement();
        String getAllByParentIdSql = "with " +
                "  world_h ( id, parent_id,type,name,lvl) as ( " +
                "    select  id, parent_id,type,name, 0 as lvl" +
                "      from world " +
                "      where parent_id =" + parentId +
                "    union all" +
                "    select  w.id, w.parent_id, w.type, w.name, lvl + 1 as lvl " +
                "      from  world w , world_h where w.parent_id = world_h.id " +
                "  ) " +
                "select id " +
                "from   world_h  ;";
        try {
            ResultSet r = s.executeQuery(getAllByParentIdSql);
            while(r.next()){
                Statement s2 = conn.createStatement();
                String deleteSql = "delete from world where id = "+r.getLong("id");
                s2.executeUpdate(deleteSql);
            }
        }catch (SQLException sql){

        }finally {
            s.close();
            conn.close();
        }

        return new ResponseEntity<String>("deleted records",HttpStatus.OK);
    }
}
