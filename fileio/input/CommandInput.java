package fileio.input;

import globalwaves.audiofiles.Song;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties
public class CommandInput {
    private String command;
    private String username;
    private Integer timestamp;

    private String playlistName;
    private Integer playlistId;

    private String type;
    private Filter filters;
    //
    private Integer itemNumber; // for select
    //
    private Integer seed; // for shuffle
    private Integer age;
    private String city;
    private Integer releaseYear;
    private String description;
    private String name; // album name
    private List<Song> songs;
    private String date; // event date
    private Integer price; // merch price
    private ArrayList<EpisodeInput> episodes;
    private String nextPage;
    public CommandInput() {
    }
}
