package org.udoo.bluneocar.viewModel;

import android.view.View;
import android.widget.Button;

import org.udoo.bluneocar.interfaces.BluNeoCommand;

/**
 * Created by harlem88 on 23/03/16.
 */
public class CommandViewModel {
    private BluNeoCommand mBlueNeoCommand;

    public CommandViewModel(BluNeoCommand blueNeoCommand) {
        mBlueNeoCommand = blueNeoCommand;
    }

    public Button.OnClickListener onForwardOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBlueNeoCommand != null)
                mBlueNeoCommand.sendCommand(BluNeoCommand.Command.FORWARD);
        }
    };

    public Button.OnClickListener onLeftOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBlueNeoCommand != null)
                mBlueNeoCommand.sendCommand(BluNeoCommand.Command.LEFT);
        }
    };

    public Button.OnClickListener onRigthOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBlueNeoCommand != null)
                mBlueNeoCommand.sendCommand(BluNeoCommand.Command.RIGHT);
        }
    };

    public Button.OnClickListener onStopOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBlueNeoCommand != null)
                mBlueNeoCommand.sendCommand(BluNeoCommand.Command.STOP);
        }
    };

    public Button.OnClickListener onBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBlueNeoCommand != null)
                mBlueNeoCommand.sendCommand(BluNeoCommand.Command.BACK);
        }
    };
}
