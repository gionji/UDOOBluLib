package org.udoo.bluneocar.interfaces;

/**
 * Created by harlem88 on 23/03/16.
 */

public interface BluNeoCommand {
    enum Command {FORWARD, BACK, STOP, LEFT, RIGHT}

    void sendCommand(Command command);
}
