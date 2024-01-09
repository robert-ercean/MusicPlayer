package globalwaves.users.listener.search;

import fileio.input.CommandInput;
import output.Output;

public final class SearchContext {
    private SearchStrategy strategy;

    public void setStrategy(final SearchStrategy strategy) {
        this.strategy = strategy;
    }
    /**
     * Executes the search command
     */
    public Output executeSearch(final CommandInput command) {
        return strategy.search(command);
    }
}
