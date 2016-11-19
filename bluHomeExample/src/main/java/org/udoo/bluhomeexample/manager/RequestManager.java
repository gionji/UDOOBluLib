package org.udoo.bluhomeexample.manager;

import android.app.DownloadManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import org.udoo.bluhomeexample.interfaces.OnResult;
import org.udoo.bluhomeexample.model.OADModel;
import org.udoo.bluhomeexample.model.OADModelResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by harlem88 on 21/10/16.
 */

public class RequestManager {
    private final static String JSON_URL_OAD = "https://udooboard.github.io/UDOOBlu/OADFirmware/firmware.json";
    private final static String TAG = "RequestManager";

    public static String GetStrinRequest(String url) {
        String resp = "";
        try {
            URL serverUrl = new URL(url);
            HttpURLConnection httpConnection = (HttpURLConnection) serverUrl.openConnection();
            httpConnection.setRequestMethod("GET");
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == 200) {
                StringBuilder sb = new StringBuilder();
                BufferedReader rd = new BufferedReader(new InputStreamReader( httpConnection.getInputStream()));
                String line;

                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                Log.i(TAG, "run: " +sb.toString());
                resp = sb.toString();

            } else {
                Log.e(TAG, "CODE: " + responseCode);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException : " + e.getMessage());
        }
        return resp;
    }

    public static void GetAODFirmwares(final OnResult<List<OADModel>> onResult) {
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                String source = GetStrinRequest(JSON_URL_OAD);
                if (source != null && source.length() > 0) {
                    Gson gson = new Gson();
                    OADModelResponse response = gson.fromJson(source, OADModelResponse.class);
                    if (response != null && response.oad_firmwares != null) {
                        if (onResult != null)
                            onResult.onSuccess(response.oad_firmwares);
                    } else {
                        if (onResult != null)
                            onResult.onError(new Throwable("null list firmwares"));
                    }
                } else {
                    if (onResult != null)
                        onResult.onError(new Throwable("null response"));
                }
            }
        });

    }


    public static long DownloadOadFirmaware(String name, String url, DownloadManager downloadManager, Context context) {
        long downloadReference;


        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        request.setTitle("OAD Download");

        request.setDescription("OAD " + name + " download...");

        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, name);
        downloadReference = downloadManager.enqueue(request);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);

//        Button DownloadStatus = (Button) findViewById(R.id.DownloadStatus);
//        DownloadStatus.setEnabled(true);
//        Button CancelDownload = (Button) findViewById(R.id.CancelDownload);
//        CancelDownload.setEnabled(true);

        return downloadReference;
    }

}
