package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieCastDto {

    private Long movieCastId;
    private Long movieId;
    @NotNull(message="Actor Name must not be null")
    @Size(min=1,max=100,message="Actor Name must be between 1 and 100 characters long")
    private String actorName;
    @NotNull(message="Character Name must not be null")
    @Size(min=1,max=100,message="Character Name must be between 1 and 100 characters long")
    private String characterName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieCastDto that = (MovieCastDto) o;
        return Objects.equals(movieId, that.movieId) && actorName.equals(that.actorName) && characterName.equals(that.characterName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, actorName, characterName);
    }
}
