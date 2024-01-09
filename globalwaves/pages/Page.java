package globalwaves.pages;

import lombok.Getter;

@Getter
public abstract class Page {
    private final String owner;
    /**
     * Inherited method by all pages, displays the page's information
     */
    public abstract String display();
    public Page(final String owner) {
        this.owner = owner;
    }
}
