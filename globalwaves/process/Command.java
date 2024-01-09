package globalwaves.process;

import fileio.input.CommandInput;
import output.Output;

public interface Command {
    Output execute(CommandInput commandInput);
}
