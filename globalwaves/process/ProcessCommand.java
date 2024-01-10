package globalwaves.process;

import fileio.input.CommandInput;
import globalwaves.GlobalWaves;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import output.Output;

import java.util.List;
public final class ProcessCommand {
    public static int timestamp = 0;
    private ProcessCommand() {
    }
    /**
     * Calls the corresponding methods for each command then adds the output
     * to the outputs array to be written to the output file.
     */
    public static ArrayNode
    executeCommands(final List<CommandInput> commands, final ObjectMapper objectMapper) {
        ArrayNode outputs = objectMapper.createArrayNode();
        for (CommandInput command : commands) {
            ProcessCommand.timestamp = command.getTimestamp();
            switch (command.getCommand()) {
                case "search":
                    outputs.add(objectMapper.valueToTree(processSearchCommand(command)));
                    break;
                case "select":
                    outputs.add(objectMapper.valueToTree(processSelectCommand(command)));
                    break;
                case "load":
                    outputs.add(objectMapper.valueToTree(processLoadCommand(command)));
                    break;
                case "playPause":
                    outputs.add(objectMapper.valueToTree(processPlayPauseCommand(command)));
                    break;
                case "status":
                    outputs.add(objectMapper.valueToTree(processStatusCommand(command)));
                    break;
                case "like":
                    outputs.add(objectMapper.valueToTree(processLikeCommand(command)));
                    break;
                case "createPlaylist":
                    outputs.add(objectMapper.valueToTree(processCreatePlaylistCommand(command)));
                    break;
                case "addRemoveInPlaylist":
                    outputs.add(objectMapper.valueToTree(
                            processAddRemoveInPlaylistCommand(command)));
                    break;
                case "showPreferredSongs":
                    outputs.add(objectMapper.valueToTree(
                            processShowPreferredSongsCommand(command)));
                    break;
                case "showPlaylists":
                    outputs.add(objectMapper.valueToTree(processShowPlaylistsCommand(command)));
                    break;
                case "repeat":
                    outputs.add(objectMapper.valueToTree(processRepeatCommand(command)));
                    break;
                case "shuffle":
                    outputs.add(objectMapper.valueToTree(processShuffleCommand(command)));
                    break;
                case "forward":
                    outputs.add(objectMapper.valueToTree(processForwardCommand(command)));
                    break;
                case "backward":
                    outputs.add(objectMapper.valueToTree(processBackwardCommand(command)));
                    break;
                case "next":
                    outputs.add(objectMapper.valueToTree(processNextCommand(command)));
                    break;
                case "prev":
                    outputs.add(objectMapper.valueToTree(processPrevCommand(command)));
                    break;
                case "follow":
                    outputs.add(objectMapper.valueToTree(processFollowCommand(command)));
                    break;
                case "switchVisibility":
                    outputs.add(objectMapper.valueToTree(processSwitchVisibilityCommand(command)));
                    break;
                case "getOnlineUsers":
                    outputs.add(objectMapper.valueToTree(processGetOnlineUsersCommand(command)));
                    break;
                case "addAlbum":
                    outputs.add(objectMapper.valueToTree(processAddAlbumCommand(command)));
                    break;
                case "showAlbums":
                    outputs.add(objectMapper.valueToTree(processShowAlbumsCommand(command)));
                    break;
                case "printCurrentPage":
                    outputs.add(objectMapper.valueToTree(processPrintCurrentPageCommand(command)));
                    break;
                case "addEvent":
                    outputs.add(objectMapper.valueToTree(processAddEventCommand(command)));
                    break;
                case "addMerch":
                    outputs.add(objectMapper.valueToTree(processAddArtistMerch(command)));
                    break;
                case "getAllUsers":
                    outputs.add(objectMapper.valueToTree(processGetAllUsers(command)));
                    break;
                case "removePodcast":
                    outputs.add(objectMapper.valueToTree(processRemovePodcast(command)));
                    break;
                case "addPodcast":
                    outputs.add(objectMapper.valueToTree(processAddPodcast(command)));
                    break;
                case "showPodcasts":
                    outputs.add(objectMapper.valueToTree(processShowPodcasts(command)));
                    break;
                case "removeAnnouncement":
                    outputs.add(objectMapper.valueToTree(processRemoveAnnouncement(command)));
                    break;
                case "addAnnouncement":
                    outputs.add(objectMapper.valueToTree(processAddAnnouncement(command)));
                    break;
                case "removeAlbum":
                    outputs.add(objectMapper.valueToTree(processRemoveAlbum(command)));
                    break;
                case "changePage":
                    outputs.add(objectMapper.valueToTree(processChangePage(command)));
                    break;
                case "removeEvent":
                    outputs.add(objectMapper.valueToTree(processRemoveEvent(command)));
                    break;
                case "buyPremium":
                    outputs.add(objectMapper.valueToTree(buyPremium(command)));
                    break;
                case "cancelPremium":
                    outputs.add(objectMapper.valueToTree(cancelPremium(command)));
                    break;
                case "subscribe":
                    outputs.add(objectMapper.valueToTree(processSubscribeCommand(command)));
                    break;
                case "wrapped":
                    outputs.add(objectMapper.valueToTree(wrapped(command)));
                    break;
                case "getNotifications":
                    outputs.add(objectMapper.valueToTree(processGetNotifications(command)));
                    break;
                /* Admin commands */
                case "deleteUser", "addUser", "getTop5Albums", "getTop5Songs",
                      "getTop5Playlists", "getTop5Artists", "switchConnectionStatus":
                    outputs.add(objectMapper.valueToTree(processAdminCommands(command)));
                    break;
                default:
                    System.out.println("Unknown command: " + command.getCommand() + ".");
                    break;
            }
        }
        outputs.add(objectMapper.valueToTree(endProgram()));
        return outputs;
    }
    private static Output processGetNotifications(final CommandInput command) {
        return GlobalWaves.getInstance().getNotifications(command);
    }
    private static Output processSubscribeCommand(final CommandInput command) {
        return GlobalWaves.getInstance().subscribe(command);
    }
    private static Output processAdminCommands(final CommandInput command) {
        return GlobalWaves.getInstance().executeAdminCommand(command);
    }
    private static Output buyPremium(final CommandInput command) {
        return GlobalWaves.getInstance().buyPremium(command);
    }
    private static Output cancelPremium(final CommandInput command) {
        return GlobalWaves.getInstance().cancelPremium(command);
    }
    private static Output endProgram() {
        return GlobalWaves.getInstance().endProgram();
    }
    private static Output wrapped(final CommandInput command) {
        return GlobalWaves.getInstance().wrapped(command);
    }
    private static Output processRemoveEvent(final CommandInput command) {
        return GlobalWaves.getInstance().removeEvent(command);
    }
    private static Output processChangePage(final CommandInput command) {
        return GlobalWaves.getInstance().changePage(command);
    }
    private static Output processRemoveAlbum(final CommandInput command) {
        return GlobalWaves.getInstance().removeAlbum(command);
    }
    private static Output processRemovePodcast(final CommandInput command) {
        return GlobalWaves.getInstance().removePodcast(command);
    }
    private static Output processAddAnnouncement(final CommandInput command) {
        return GlobalWaves.getInstance().addHostAnnouncement(command);
    }
    private static Output processRemoveAnnouncement(final CommandInput command) {
        return GlobalWaves.getInstance().removeAnnouncement(command);
    }
    private static Output processAddPodcast(final CommandInput command) {
        return GlobalWaves.getInstance().addPodcast(command);
    }
    private static Output processShowPodcasts(final CommandInput command) {
        return GlobalWaves.getInstance().showPodcasts(command);
    }
    private static Output processGetAllUsers(final CommandInput command) {
        return GlobalWaves.getInstance().getAllUsers(command);
    }
    private static Output processAddArtistMerch(final CommandInput command) {
        return GlobalWaves.getInstance().addArtistMerch(command);
    }
    private static Output processAddEventCommand(final CommandInput command) {
        return GlobalWaves.getInstance().addArtistEvent(command);
    }
    private static Output processPrintCurrentPageCommand(
            final CommandInput command) {
        return GlobalWaves.getInstance().printUserCurrentPage(command);
    }
    private static Output processShowAlbumsCommand(
            final CommandInput command) {
        return GlobalWaves.getInstance().showAlbums(command);
    }
    private static Output processAddAlbumCommand(final CommandInput command) {
        return GlobalWaves.getInstance().addAlbum(command);
    }
    private static Output processGetOnlineUsersCommand(
            final CommandInput command) {
        return GlobalWaves.getInstance().getOnlineUsers(command);
    }
    private static Output processSwitchVisibilityCommand(final CommandInput command) {
        return GlobalWaves.getInstance().switchVisibility(command);
    }

    private static Output processFollowCommand(final CommandInput command) {
        return GlobalWaves.getInstance().follow(command);
    }
    private static Output processPrevCommand(final CommandInput command) {
        return GlobalWaves.getInstance().prev(command);
    }
    private static Output processNextCommand(final CommandInput command) {
        return GlobalWaves.getInstance().next(command);
    }
    private static Output processBackwardCommand(final CommandInput command) {
        return GlobalWaves.getInstance().backward(command);
    }
    private static Output processForwardCommand(final CommandInput command) {
        return GlobalWaves.getInstance().forward(command);
    }
    private static Output processShuffleCommand(final CommandInput command) {
        return GlobalWaves.getInstance().shuffle(command);
    }
    private static Output processRepeatCommand(final CommandInput command) {
        return GlobalWaves.getInstance().repeat(command);
    }
    private static Output processShowPreferredSongsCommand(final CommandInput command) {
        return GlobalWaves.getInstance().showPreferredSongs(command);
    }
    private static Output processShowPlaylistsCommand(final CommandInput command) {
        return GlobalWaves.getInstance().showPlaylists(command);
    }
    private static Output processAddRemoveInPlaylistCommand(final CommandInput command) {
        return GlobalWaves.getInstance().addRemoveInPlaylist(command);
    }
    private static Output processCreatePlaylistCommand(final CommandInput command) {
        return GlobalWaves.getInstance().createPlaylist(command);
    }
    private static Output processLikeCommand(final CommandInput command) {
        return GlobalWaves.getInstance().like(command);
    }
    private static Output processStatusCommand(final CommandInput command) {
        return GlobalWaves.getInstance().status(command);
    }
    private static Output processPlayPauseCommand(final CommandInput command) {
        return GlobalWaves.getInstance().playPause(command);
    }
    private static Output processLoadCommand(final CommandInput command) {
        return GlobalWaves.getInstance().load(command);
    }
    private static Output processSelectCommand(final CommandInput command) {
        return GlobalWaves.getInstance().select(command);
    }
    private static Output processSearchCommand(final CommandInput command) {
        return GlobalWaves.getInstance().search(command);
    }
}
