package com.newcache.server.command.handler;

import com.newcache.server.cache.NewCache;
import com.newcache.server.command.object.CommandObject;
import com.newcache.server.command.object.BufferedCommandState;
import com.newcache.server.constants.Constants;
import com.newcache.server.exception.BadCommandException;
import com.newcache.server.exception.InvalidCommandException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CommandHandler implementation to process the set command.
 */
public class SetCommandHandler implements CommandHandler {

    private final static Logger LOGGER = Logger.getLogger(SetCommandHandler.class);

    /**
     * Parse the set command and extract the tokens.
     * Takes into account optional noreply parameter.
     *
     * @param command String
     * @return CommandObject
     * @throws BadCommandException
     */
    @Override
    public CommandObject parseCommand(String command) throws BadCommandException, InvalidCommandException {
        Pattern p = Pattern.compile("^\\s*set\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)?\\s*$");
        Matcher m = p.matcher(command);

        if (m.find()) {
            try {
                String key = m.group(1);
                Integer flags = Integer.valueOf(m.group(2));
                Integer expiryTime = Integer.valueOf(m.group(3));
                Integer bytes = Integer.valueOf(m.group(4));
                String noReply = m.group(5);
                return new CommandObject(key, flags, expiryTime, bytes, noReply);
            } catch (NumberFormatException ex) {
                LOGGER.error(ex);
                throw new BadCommandException(Constants.CLIENT_ERROR_BAD_FORMAT);
            }
        } else {
            LOGGER.error("Invalid command");
            throw new InvalidCommandException(Constants.ERROR);
        }
    }

    /**
     * Validate whether data block size matches the bytes specified in the set command.
     *
     * @param commandObject CommandObject
     * @param data String
     * @throws BadCommandException
     */
    private void validateDataBytes(CommandObject commandObject, String data) throws BadCommandException {
        int bytes = commandObject.getBytes();
        data = data.replaceAll("(\\r|\\n)", "");
        if (data != null && data.length() != bytes) {
            LOGGER.error("Value length did not match specified bytes");
            throw new BadCommandException(Constants.CLIENT_ERROR_BAD_CHUNK);
        }
        commandObject.setValue(data);
    }

    /**
     * Buffers the set command in thread local variable while
     * waiting for the data block in subsequent command.
     * When a valid data block is received,
     * set the key value pair in the cache and clear the thread local variable.
     *
     * @param command String
     * @return List<String>
     */
    @Override
    public List<String> processCommand(String command) {
        List<String> response = new ArrayList<>();
        try {
            CommandObject bufferedCommand = BufferedCommandState.get();
            if (bufferedCommand != null) {
                validateDataBytes(bufferedCommand, command);

                NewCache cache = NewCache.getInstance();
                cache.put(bufferedCommand.getKey(), bufferedCommand);

                LOGGER.debug("Stored item in cache");
                BufferedCommandState.unset();
                response.add(Constants.STORED);
            } else {
                BufferedCommandState.set(parseCommand(command));
            }
        } catch (BadCommandException | InvalidCommandException ex) {
            LOGGER.error(ex);
            BufferedCommandState.unset();
            response.add(ex.getMessage());
        }
        return response;
    }
}
