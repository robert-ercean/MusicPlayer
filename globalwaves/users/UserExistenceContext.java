package globalwaves.users;

import fileio.input.CommandInput;
import output.Output;

public class UserExistenceContext {
    private UserExistenceStrategy strategy;
    /**
     * @param strategy the strategy to be used
     */
    public void setStrategy(final UserExistenceStrategy strategy) {
        this.strategy = strategy;
    }
    /**
     * @param command the command to be executed
     * @param output  the place where the result of the command is stored
     * @return true if the user exists, false otherwise
     */
    public boolean exists(final CommandInput command, final Output output) {
        return strategy.exists(command, output);
    }
}
