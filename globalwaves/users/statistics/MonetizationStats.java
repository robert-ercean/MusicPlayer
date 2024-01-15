package globalwaves.users.statistics;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonetizationStats {
    private double songRevenue;
    public double merchRevenue;
    private int ranking;
    private String mostProfitableSong;
}
