package globalwaves.users.artist;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Event {
    private String name;
    private String date;
    private String description;

    private static final int MIN_YEAR = 1900;
    private static final int MAX_YEAR = 2023;
    private static final int MIN_MONTH = 1;
    private static final int MAX_MONTH = 12;
    private static final int MIN_DAY = 1;
    private static final int MAX_DAY = 31;
    private static final int FEBRUARY = 2;
    private static final int MAX_DAY_IN_FEBRUARY = 28;
    @Override
    public final boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Event event)) {
            return false;
        }
        return event.getName().equals(this.getName());
    }
    @Override
    public final int hashCode() {
        return this.getName().hashCode();
    }
    public Event(final String name, final String date, final String description) {
        this.name = name;
        this.date = date;
        this.description = description;
    }
    /**
     * Checks if the date is valid, meaning that it is in the format dd-mm-yyyy
     * with the year being between 1900 and 2023, the month between 1 and 12 and the day
     * between 1 and 31 (or 28 if the month is February)
     * @param date the date to be checked
     * @return true if the date is valid, false otherwise
     */
    public static boolean checkDateValidity(final String date) {
        // date format: dd-mm-yyyy
        String[] dateParts = date.split("-");
        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int year = Integer.parseInt(dateParts[2]);
        if (year > MAX_YEAR || year < MIN_YEAR) {
            return false;
        } else if (month > MAX_MONTH || month < MIN_MONTH) {
            return false;
        } else if (day > MAX_DAY || day < MIN_DAY) {
            return false;
        } else  {
            return month != FEBRUARY || day <= MAX_DAY_IN_FEBRUARY;
        }
    }
}

