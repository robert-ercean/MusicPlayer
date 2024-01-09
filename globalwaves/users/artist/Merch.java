package globalwaves.users.artist;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Merch {
    private String name;
    private String description;
    private int price;

    public Merch(final String name, final String description, final int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Merch merch)) {
            return false;
        }
        return merch.getName().equals(this.getName());
    }
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
}
