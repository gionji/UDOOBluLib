package org.udoo.bluhomeexample.presenter;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

/**
 * Created by harlem88 on 27/01/17.
 */

public class BrickPresenter {

    public void onBrickShopClick(View view, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        view.getContext().startActivity(intent);
    }

    public void onAllBrickShopClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.udoo.org/udoo-bricks/"));
        view.getContext().startActivity(intent);
    }
}
