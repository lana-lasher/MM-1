package implementation.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter

public class World {

    private Long id;
    private String name;
    private Long parentId;
    private String type;
    private int level;
   // private String actorName;
   //private String characterName;

    private List<World> objectList;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        World world = (World) o;
        return name.equals(world.name) && parentId.equals(world.parentId) && Objects.equals(objectList, world.objectList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parentId, objectList);
    }
}
