package dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Getter
@Setter
@Builder
public class MovieStudioDto {
    private Long studioId;

    @NotNull(message="Studio Name must not be blank")
    @Size(max=100,min=1,message="Studio Name must be between 1 and 100 characters long")
    private String studioName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieStudioDto that = (MovieStudioDto) o;
        return Objects.equals(studioId, that.studioId) && studioName.equals(that.studioName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studioId, studioName);
    }

}
