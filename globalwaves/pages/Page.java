package globalwaves.pages;

import globalwaves.GlobalWaves;
import globalwaves.users.User;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class Page {
    public final String owner;
    /**
     * Inherited method by all pages, displays the page's information
     * in the required format
     */
    public abstract String display();

    /**
     * Method used to get the owner of the page as an instance of the User class
     * used to elegantly get the owner of a listener's current page,
     * be it an artist, host or listener (maybe in future implementations) without
     * typechecking each case
     * @return the user as a "User" class instance
     */
    public abstract User getOwner();

    /**
     * Method used to identify the type of the page in error checking contexts
     * @return the type of the page
     */
    public String getPageType() {
        List<String> listnrs = GlobalWaves.getInstance().getListeners().keySet().stream().toList();
        List<String> artists = GlobalWaves.getInstance().getArtists().keySet().stream().toList();
        List<String> hosts = GlobalWaves.getInstance().getHosts().keySet().stream().toList();
        if (listnrs.contains(owner)) {
            return "listener";
        } else if (artists.contains(owner)) {
            return "artist";
        } else if (hosts.contains(owner)) {
            return "host";
        }
        return null;
    }
    public Page(final String owner) {
        this.owner = owner;
    }
}
