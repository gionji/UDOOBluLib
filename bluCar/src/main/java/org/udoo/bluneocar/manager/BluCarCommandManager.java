package org.udoo.bluneocar.manager;

import org.udoo.bluneocar.interfaces.BluNeoCommand;
import org.udoo.udooblulib.manager.UdooBluManager;
import org.udoo.udooblulib.sensor.Constant.IOPIN_VALUE;
import org.udoo.udooblulib.sensor.Constant.IOPIN;

/**
 * Created by harlem88 on 01/04/16.
 */
public class BluCarCommandManager implements BluNeoCommand {
    private UdooBluManager mUdooBluManager;
    private String mAddress;

    public BluCarCommandManager(UdooBluManager udooBluManager, String address) {
        mUdooBluManager = udooBluManager;
        mAddress = address;
    }

    /**
     * |
     * FORWARD, BACK, STOP, LEFT, RIGHT
     *
     * @param command
     */
    @Override
    public void sendCommand(Command command) {
        IOPIN ioPin[] = null;
        IOPIN_VALUE value = IOPIN_VALUE.LOW;
        switch (command) {
            case FORWARD:
                ioPin = new IOPIN[4];
                ioPin[0] = IOPIN.D6;
                ioPin[1] = IOPIN.A0;
                ioPin[2] = IOPIN.A1;
                ioPin[3] = IOPIN.A4;
                value = IOPIN_VALUE.HIGH;
                break;
            case BACK:
                ioPin = new IOPIN[2];
                ioPin[0] = IOPIN.A1;
                ioPin[1] = IOPIN.A2;
                value = IOPIN_VALUE.HIGH;
                break;
            case STOP:
                ioPin = new IOPIN[0];
                value = IOPIN_VALUE.LOW;
                break;
            case LEFT:
                ioPin = new IOPIN[2];
                ioPin[0] = IOPIN.D6;
                ioPin[1] = IOPIN.A1;
                break;
            case RIGHT:
                ioPin = new IOPIN[2];
                ioPin[0] = IOPIN.A0;
                ioPin[1] = IOPIN.A2;
                value = IOPIN_VALUE.HIGH;
                break;
        }
        if (mUdooBluManager != null && ioPin != null)
            mUdooBluManager.digitalWrite(mAddress, value, ioPin);
    }
}
