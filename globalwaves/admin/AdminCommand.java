package globalwaves.admin;

import fileio.input.CommandInput;
import output.Output;

public interface AdminCommand {
    /**
     * Command design pattern implementation used for executing admin-related commands
     * @return the output of the command
     */
    Output execute(CommandInput command);
}
