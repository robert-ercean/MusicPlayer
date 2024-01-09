package globalwaves.users.listener.search;

import fileio.input.CommandInput;
import globalwaves.GlobalWaves;
import globalwaves.users.listener.Listener;
import output.Output;

import java.util.ArrayList;
import java.util.List;

import static constants.Constants.MAX;

public final class HostSearchStrategy implements SearchStrategy {
    @Override
    public Output search(final CommandInput command) {
        Listener user = GlobalWaves.getInstance().getUsers().get(command.getUsername());
        List<String> hosts = new ArrayList<>();
        for (String host : GlobalWaves.getInstance().getHosts().keySet()) {
            if (host.toLowerCase().startsWith(command.getFilters().getName().toLowerCase())) {
                hosts.add(host);
            }
            if (hosts.size() == MAX) {
                break;
            }
        }
        user.setSearchedHosts(hosts);
        user.setSelectedItemType("host");
        Output searchOutputHost = Output.getOutputTemplate(command);
        searchOutputHost.setResults(hosts);
        searchOutputHost.setMessage("Search returned " + hosts.size() + " results");
        return searchOutputHost;
    }
}
