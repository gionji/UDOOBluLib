package org.udoo.bluhomeexample.model;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import org.udoo.bluhomeexample.R;
import org.udoo.bluhomeexample.util.BindableBoolean;

/**
 * Created by harlem88 on 16/02/16.
 */

public class BluItem implements Parcelable {
    private transient boolean connected;
    public String name;
    public String address;
    public String rssi;
    public int color;
    public boolean paired;
    private transient boolean found;
    public String version;

    public BluItem() {
        connected = false;
    }

    public static BluItem Builder(BluetoothDevice device, String rssi) {
        BluItem bluItem = new BluItem();
        bluItem.name = device.getName();
        bluItem.address = device.getAddress();
        bluItem.rssi = rssi;
        bluItem.color = R.color.blue_500;
        bluItem.connected = false;
        bluItem.found = false;
        bluItem.paired = false;
        return bluItem;
    }

    public static BluItem Builder(String address, String name, String rssi, int color) {
        BluItem bluItem = new BluItem();
        bluItem.name = name;
        bluItem.address = address;
        bluItem.rssi = rssi;
        bluItem.color = color;
        bluItem.connected = false;
        bluItem.found = false;
        bluItem.paired = false;
        return bluItem;
    }

    public static BluItem Builder(BluItem bluItem) {
        BluItem newBluItem = new BluItem();
        newBluItem.name = bluItem.name;
        newBluItem.address = bluItem.address;
        newBluItem.rssi = bluItem.rssi;
        newBluItem.color = bluItem.color;
        newBluItem.connected = bluItem.connected;
        newBluItem.found = bluItem.found;
        newBluItem.paired = bluItem.paired;
        return newBluItem;
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * Storing the Student data to Parcel object
     **/
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(version);
        dest.writeString(rssi);
        dest.writeInt(color);
        dest.writeInt(paired ? 1 : 0);
        dest.writeInt(connected ? 1 : 0);

    }

    private BluItem(Parcel in) {
        name = in.readString();
        address = in.readString();
        version = in.readString();
        rssi = in.readString();
        color = in.readInt();
        paired = in.readInt() == 1;
        connected = in.readInt() == 1;
    }

    public static final Creator<BluItem> CREATOR = new Creator<BluItem>() {

        @Override
        public BluItem createFromParcel(Parcel source) {
            return new BluItem(source);
        }

        @Override
        public BluItem[] newArray(int size) {
            return new BluItem[size];
        }
    };

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}

