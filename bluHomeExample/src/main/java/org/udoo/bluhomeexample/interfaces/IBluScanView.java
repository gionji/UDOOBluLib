package org.udoo.bluhomeexample.interfaces;

import org.udoo.bluhomeexample.dialog.BluSaveDialog;
import org.udoo.bluhomeexample.model.BluItem;
import org.udoo.udooblulib.exceptions.UdooBluException;

import java.util.List;

/**
 * Created by harlem88 on 09/10/16.
 */

public interface IBluScanView {
    void setRefresh(boolean refresh);
    void showDialog(BluSaveDialog bluSaveDialog);
    void addDevice(BluItem bluItem);
    void updateDevice(BluItem bluItem);
    void addDevices(List<BluItem> bluItems);
    void onError(UdooBluException throwable);
    void onConnectPage(BluItem item);
}
