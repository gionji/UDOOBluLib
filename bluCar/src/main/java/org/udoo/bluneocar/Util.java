package org.udoo.bluneocar;

/**
 * Created by harlem88 on 23/03/16.
 */

import android.hardware.SensorManager;


public class Util {

    public static float[] GetOrientationValues(float acc[], float magn[]) {
        float[] rotationMatrix = new float[9];
        float[] orientationValues = new float[3];

        //trasform acc magg into yaw pitch and roll
        boolean succes = SensorManager.getRotationMatrix(rotationMatrix, null, acc, magn);
        SensorManager.getOrientation(rotationMatrix, orientationValues);

        for (int i=0; i < orientationValues.length; i++) {
            Double degrees = (orientationValues[i] * 180) / Math.PI;
            orientationValues[i] = degrees.floatValue();
        }


        return orientationValues;
    }
}