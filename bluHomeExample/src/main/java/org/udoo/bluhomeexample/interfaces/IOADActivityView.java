package org.udoo.bluhomeexample.interfaces;

import org.udoo.bluhomeexample.model.OADModel;

import java.util.List;

/**
 * Created by harlem88 on 21/10/16.
 */

public interface IOADActivityView {
    void addFirmwares(List<OADModel> firmwares);
    void showProgress(String text, boolean indeterminate);
    void updateProgress(int value);
    void dismissProgress();
    void enableOADUpload(boolean enable);
    void setOADInfo(String name, String version, String data);
}
