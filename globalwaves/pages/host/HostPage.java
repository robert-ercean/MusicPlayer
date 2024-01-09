package globalwaves.pages.host;

import globalwaves.audiofiles.Podcast;
import fileio.input.EpisodeInput;
import globalwaves.users.host.Announcement;
import lombok.Getter;
import lombok.Setter;
import globalwaves.pages.Page;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public final class HostPage extends Page {
    private final List<Podcast> podcasts;
    private final List<Announcement> announcements;
    public HostPage(final String owner) {
        super(owner);
        this.podcasts = new ArrayList<>();
        this.announcements = new ArrayList<>();
    }

    @Override
    public String display() {
        return "Podcasts:\n\t["
                + podcastsToString() + ']'
                + "\n\nAnnouncements:\n\t["
                + announcementsToString() + ']';
    }
    private String podcastsToString() {
        StringBuilder podcastsNames = new StringBuilder();
        for (Podcast podcast : this.podcasts) {
            podcastsNames.append(podcast.getName());
            podcastsNames.append(":\n\t[");
            for (EpisodeInput episode : podcast.getEpisodes()) {
                podcastsNames.append(episode.getName());
                podcastsNames.append(" - ");
                podcastsNames.append(episode.getDescription());
                podcastsNames.append(", ");
            }
            // remove the last comma and space
            podcastsNames.delete(podcastsNames.length() - 2, podcastsNames.length());
            podcastsNames.append("]\n");
            podcastsNames.append(", ");
        }
        podcastsNames.delete(podcastsNames.length() - 2, podcastsNames.length());
        return podcastsNames.toString();
    }
    private String announcementsToString() {
        StringBuilder announcementsString = new StringBuilder();
        for (Announcement announcement : this.announcements) {
            announcementsString.append(announcement.getName());
            announcementsString.append(":\n\t");
            announcementsString.append(announcement.getDescription());
            announcementsString.append("\n");
        }
        return announcementsString.toString();
    }
}
