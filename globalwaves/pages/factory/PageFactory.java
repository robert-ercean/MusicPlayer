package globalwaves.pages.factory;

import globalwaves.pages.artist.ArtistPage;
import globalwaves.pages.user.HomePage;
import globalwaves.pages.host.HostPage;
import globalwaves.pages.Page;
import globalwaves.pages.user.LikedContentPage;

public final class PageFactory {
    private PageFactory() {
    }
    /**
     * Factory method implementation for creating a page.
     * @param pageType the type of the page to be created, used only for listener pages
     *                 since there are multiple types of listener pages
     * @param userType the type of the user that owns the page
     * @param owner the owner of the page
     * @return the created page
     */
    public static Page createPage(final String pageType,
                                  final String userType, final String owner) {
        if (userType.equals("normal")) {
            return switch (pageType) {
                case "HomePage" -> new HomePage(owner);
                case "LikedContentPage" -> new LikedContentPage(owner);
                default -> {
                    System.out.println("Invalid page type");
                    yield null;
                }
            };
        } else if (userType.equals("artist")) {
            return new ArtistPage(owner);
        } else if (userType.equals("host")) {
            return new HostPage(owner);
        }
        return null;
    }
}
