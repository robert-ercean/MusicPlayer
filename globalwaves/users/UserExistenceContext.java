package globalwaves.users;

import fileio.input.CommandInput;
import output.Output;

public class UserExistenceContext {
    private UserExistenceStrategy strategy;
    public void setStrategy(final UserExistenceStrategy strategy) {
        this.strategy = strategy;
    }
    public boolean exists(final CommandInput command, final Output output) {
        return strategy.exists(command, output);
    }
}
