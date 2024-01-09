package globalwaves.users.host;

import globalwaves.audiofiles.Podcast;
import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import globalwaves.GlobalWaves;
import globalwaves.users.User;
import globalwaves.users.statistics.HostStats;
import globalwaves.users.listener.Listener;
import globalwaves.users.listener.player.Player;
import lombok.Getter;
import lombok.Setter;
import globalwaves.pages.Page;
import globalwaves.pages.factory.PageFactory;
import globalwaves.pages.host.HostPage;
import fileio.input.CommandInput;
import output.Output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public final class Host extends User {
    private final Page hostpage;
    private final HostStats stats;
    /**
     * Constructor for the host
     */
    public Host(final CommandInput command) {
        super.setUsername(command.getUsername());
        super.setCity(command.getCity());
        super.setAge(command.getAge());
        this.hostpage = PageFactory.createPage(null, "host", super.getUsername());
        this.stats = new HostStats(super.getUsername());
    }
    public HostPage getHostpage() {
        return (HostPage) this.hostpage;
    }
    public Output wrapped(CommandInput command) {
        return this.stats.display(command);
    }
    /**
     * Adds a new podcast to the host page and implicitly to the library
     */
    public String addPodcast(final CommandInput command) {
        String name = command.getName();
        String owner = command.getUsername();
        ArrayList<EpisodeInput> episodes = command.getEpisodes();
        HostPage hostPage = (HostPage) this.hostpage;
        for (Podcast podcast : hostPage.getPodcasts()) {
            if (podcast.getName().equals(name)) {
                return (command.getUsername() + " has another podcast with the same name.");
            }
        }
        // check if the podcast contains two episodes with the same name
        List<String> episodeNames = new ArrayList<>();
        for (EpisodeInput episode : episodes) {
            if (episodeNames.contains(episode.getName())) {
                return (command.getUsername() + " has the same episode in this podcast.");
            }
            episodeNames.add(episode.getName());
        }
        Podcast podcast = new Podcast(name, owner, episodes);
        hostPage.getPodcasts().add(podcast);
        GlobalWaves.getInstance().getLibrary().getPodcasts().add(new PodcastInput(podcast));
        return (command.getUsername() + " has added new podcast successfully.");
    }
    /**
     * Removes a podcast from the host page and implicitly from the library
     */
    public String removePodcast(final CommandInput command) {
        String name = command.getName();
        String owner = command.getUsername();
        HostPage hostPage = (HostPage) this.hostpage;
        // check if the user has a podcast with the same name
        Podcast podcast = new Podcast(name, owner, null);
        if (!hostPage.getPodcasts().contains(podcast)) {
            return (command.getUsername() + " doesn't have a podcast with the given name.");
        }
        podcast = hostPage.getPodcasts().get(hostPage.getPodcasts().indexOf(podcast));
        if (podcast.isLoaded()) {
            return (command.getUsername() + " can't delete this podcast.");
        }
        hostPage.getPodcasts().remove(podcast);
        ArrayList<PodcastInput> libPodcasts = GlobalWaves.getInstance().getLibrary().getPodcasts();
        for (PodcastInput libPodcast : libPodcasts) {
            if (libPodcast.getName().equals(name)) {
                libPodcasts.remove(libPodcast);
                break;
            }
        }
        return (command.getUsername() + " deleted the podcast successfully.");
    }
    /**
     * Shows the podcasts of the host
     * Using a list of maps to format the output message result
     * in the required format
     */
    public List<Map<String, Object>> showPodcasts() {
        List<Podcast> podcasts = this.getHostpage().getPodcasts();
        // create a list of maps to store the podcast information
        List<Map<String, Object>> podcastMaps = new ArrayList<>();
        for (Podcast podcast : podcasts) {
            // Create a map to store the podcast name and episode names
            Map<String, Object> podcastMap = new HashMap<>();
            podcastMap.put("name", podcast.getName());
            // Create a list to store the episode names
            List<String> episodeNames = new ArrayList<>();
            for (EpisodeInput episode : podcast.getEpisodes()) {
                episodeNames.add(episode.getName());
            }
            // Add the episode names to the podcast map
            podcastMap.put("episodes", episodeNames);
            // Add the podcast map to the list of podcast maps
            podcastMaps.add(podcastMap);
        }
        // Set the result of the output to the list of podcast maps
        return podcastMaps;
    }
    /**
     * Adds a new announcement to the host page
     */
    public String addAnnouncement(final CommandInput command) {
        String name = command.getName();
        String description = command.getDescription();

        Announcement announcement = new Announcement(name, description);

        HostPage hostPage = (HostPage) this.hostpage;

        if (hostPage.getAnnouncements().contains(announcement)) {
            return (command.getUsername() + " has already added an announcement with this name.");
        }
        hostPage.getAnnouncements().add(announcement);
        return (command.getUsername() + " has successfully added new announcement.");
    }

    /**
     * Removes an announcement from the host page
     */
    public String removeAnnouncement(final CommandInput command) {
        String name = command.getName();

        Announcement announcement = new Announcement(name, null);

        HostPage hostPage = (HostPage) this.hostpage;

        if (!hostPage.getAnnouncements().contains(announcement)) {
            return (command.getUsername() + " has no announcement with the given name.");
        }
        hostPage.getAnnouncements().remove(announcement);
        return (command.getUsername() + " has successfully deleted the announcement.");
    }
    @Override
    public boolean isInteracting() {
        for (Listener user : GlobalWaves.getInstance().getUsers().values()) {
            // cant delete if a user's current page is the host page
            if (user.getCurrentPage().getOwner().equals(super.getUsername())) {
                return true;
            }
            Player player = user.getUserPlayer();
            if (Player.isEmpty(player)) {
                continue;
            }
            if (player.getCurrentPodcast() != null
                    && player.getCurrentPodcast().getOwner().equals(super.getUsername())) {
                return true;
            }
        }
        return false;
    }
}
