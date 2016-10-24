package org.udoo.bluhomeexample.presenter;

import org.udoo.bluhomeexample.interfaces.IOADActivityView;
import org.udoo.bluhomeexample.interfaces.OnResult;
import org.udoo.bluhomeexample.manager.RequestManager;
import org.udoo.bluhomeexample.model.OADModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harlem88 on 21/10/16.
 */

public class OADPresenter {
    private List<OADModel> firmwares;
    private IOADActivityView mIoadActivityView;

    public OADPresenter(){
        firmwares = new ArrayList<>();
    }

    public void onStart(IOADActivityView ioadActivityView){
        mIoadActivityView = ioadActivityView;
        if(firmwares == null || firmwares.size() == 0){
            RequestManager.GetAODFirmwares(new OnResult<List<OADModel>>() {
                @Override
                public void onSuccess(List<OADModel> o) {
                    firmwares.clear();
                    firmwares.addAll(o);
                    if(mIoadActivityView != null)
                        mIoadActivityView.addFirmwares(firmwares);
                }

                @Override
                public void onError(Throwable throwable) {
                    //TODO
                }
            });
        }
    }

    public void onResume(){}
}
