package fileio.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties
public class Filter {
    private String artist;
    private String album;
    private String name;
    private String lyrics;
    private String owner;
    private String genre;
    private String releaseYear;
    private List<String> tags;
    private String description;
}
