package dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
public class MoviesDto {

    private Long movieId;
    @NotNull(message="Movie Name must not be null")
    @Size(min=1,max=100,message="Movie Name must be between 1 and 100 characters long")
    private String movieName;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern="yyyy-MM-dd",timezone = "America/New_York")
    private String releaseDate;

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoviesDto moviesDto = (MoviesDto) o;
        return movieName.equals(moviesDto.movieName) && Objects.equals(releaseDate, moviesDto.releaseDate) && Objects.equals(movieCastDtoList, moviesDto.movieCastDtoList);
    }

    public int hashCode() {
        return Objects.hash(movieName, releaseDate, movieCastDtoList);
    }

    private List<MovieCastDto> movieCastDtoList;
}
