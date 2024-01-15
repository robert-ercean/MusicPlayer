package globalwaves.process;

import fileio.input.CommandInput;
import output.Output;

public interface Command {
    /**
     * @param commandInput to parse the command
     * @return the output of the command
     */
    Output execute(CommandInput commandInput);
}
