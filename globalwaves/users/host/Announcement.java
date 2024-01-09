package globalwaves.users.host;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Announcement {
    private final String name;
    private final String description;
    public Announcement(final String name, final String description) {
        this.name = name;
        this.description = description;
    }
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Announcement announcement) {
            return this.name.equals(announcement.name);
        }
        return false;
    }
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
