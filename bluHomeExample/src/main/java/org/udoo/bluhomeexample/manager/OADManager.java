package org.udoo.bluhomeexample.manager;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.udoo.udooblulib.utils.Conversion;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by harlem88 on 24/10/16.
 */

public class OADManager {
    private static final short OAD_CONN_INTERVAL = 6; // 15 milliseconds
    private static final short OAD_SUPERVISION_TIMEOUT = 50; // 500 milliseconds
    private static final int GATT_WRITE_TIMEOUT = 300; // Milliseconds

    private static final int FILE_BUFFER_SIZE = 0x40000;
    public static final String FW_CUSTOM_DIRECTORY = Environment.DIRECTORY_DOWNLOADS;

    private static final int OAD_BLOCK_SIZE = 16;
    private static final int HAL_FLASH_WORD_SIZE = 4;
    private static final int OAD_BUFFER_SIZE = 2 + OAD_BLOCK_SIZE;
    private static final int OAD_IMG_HDR_SIZE = 8;
    private static final long TIMER_INTERVAL = 1000;

    private static final int SEND_INTERVAL = 20; // Milliseconds (make sure this is longer than the connection interval)
    private static final int BLOCKS_PER_CONNECTION = 20; // May sent up to four blocks per connection
    private static final String TAG = "OADManager";

    // BLE
    private BluetoothGattService mOadService;
    private BluetoothGattService mConnControlService;
    private BluetoothGattService mTestService;
    private List<BluetoothGattCharacteristic> mCharListOad;
    private List<BluetoothGattCharacteristic> mCharListCc;
    private BluetoothGattCharacteristic mCharIdentify = null;
    private BluetoothGattCharacteristic mCharBlock = null;
    private BluetoothGattCharacteristic mCharConnReq = null;
    private BluetoothGattCharacteristic mTestResult = null;

    // Programming
    private final byte[] mFileBuffer = new byte[FILE_BUFFER_SIZE];
    private final byte[] mOadBuffer = new byte[OAD_BUFFER_SIZE];
    private ImgHdr mFileImgHdr;
    private ImgHdr mTargImgHdr;
    private Timer mTimer = null;
    private ProgInfo mProgInfo = new ProgInfo();
    private TimerTask mTimerTask = null;
    private boolean mProgramming;
    private int mPacketsSent;
    private BluetoothGattService mOADService;

    public OADManager(BluetoothGattService oadService){
        mProgInfo = new ProgInfo();
        mOADService = oadService;
    }

    private void startProgramming() {
        Log.i(TAG, "Programming started");
        mProgramming = true;
        mPacketsSent = 0;
//        updateGui();

        mCharIdentify.setValue(mFileImgHdr.getRequest());
//        mLeService.writeCharacteristic(mCharIdentify);

        // Initialize stats
        mProgInfo.reset();
        mTimer = new Timer();
        mTimerTask = new ProgTimerTask();
        mTimer.scheduleAtFixedRate(mTimerTask, 0, TIMER_INTERVAL);
    }

    private void stopProgramming() {
        mTimer.cancel();
        mTimer.purge();
        mTimerTask.cancel();
        mTimerTask = null;

        mProgramming = false;
//        mProgressBar.setProgress(0);
//        updateGui();

//        mLeService.setCharacteristicNotification(mCharBlock, false);
//        if (mProgInfo.iBlocks == mProgInfo.nBlocks) {
//            mLog.setText("Programming complete!\n");
//        } else {
//            mLog.append("Programming cancelled\n");
//        }
    }


    private boolean loadFile(String filepath, boolean isAsset, Context context) {
        boolean fSuccess = false;
        int readLen = 0;
        // Load binary file
        try {
            // Read the file raw into a buffer
            InputStream stream;
            if (isAsset) {
                stream = context.getAssets().open(filepath);
            } else {
                File f = new File(filepath);
                stream = new FileInputStream(f);
            }
            readLen = stream.read(mFileBuffer, 0, mFileBuffer.length);
            stream.close();
        } catch (IOException e) {
            // Handle exceptions here
            Log.e(TAG, "File open failed: " + filepath + "\n");
            return false;
        }

//        if (!isAsset) {
//            mFileImage.setText(filepath);
//        }
//
//        //Always enable button on CC26xx
//        mBtnStart.setEnabled(true);
//
//        mFileImgHdr = new ImgHdr(mFileBuffer,readLen);

        // Expected duration
//        displayStats();

        // Log
//        mLog.setText("Programming image : " + filepath + "\n");
//        mLog.append("File size : " + readLen + " bytes (" + (readLen / 16) + ") blocks\n");
//        mLog.append("Ready to program device!\n");

//        updateGui();

        return fSuccess;
    }


    private void displayStats() {
        String txt;
        int byteRate;
        int sec = mProgInfo.iTimeElapsed / 1000;
        if (sec > 0) {
            byteRate = mProgInfo.iBytes / sec;
        } else {
            byteRate = 0;
            return;
        }
        float timeEstimate;

        timeEstimate = ((float)(mFileImgHdr.len *4) / (float)mProgInfo.iBytes) * sec;

        txt = String.format("Time: %d / %d sec", sec, (int)timeEstimate);
        txt += String.format(" Bytes: %d (%d/sec)", mProgInfo.iBytes, byteRate);
//        mProgressInfo.setText(txt);
    }


    private class ProgTimerTask extends TimerTask {
        @Override
        public void run() {
            mProgInfo.iTimeElapsed += TIMER_INTERVAL;
        }
    }

    private class ImgHdr {
        short crc0;
        short crc1;
        short ver;
        int len;
        byte[] uid = new byte[4];
        short addr;
        byte imgType;

        ImgHdr(byte[] buf, int fileLen) {
            this.len = (fileLen / (16 / 4));
            this.ver = 0;
            this.uid[0] = this.uid[1] = this.uid[2] = this.uid[3] = 'E';
            this.addr = 0;
            this.imgType = 1; //EFL_OAD_IMG_TYPE_APP
            this.crc0 = calcImageCRC((int)0,buf);
            crc1 = (short)0xFFFF;
            Log.d(TAG,"ImgHdr.len = " + this.len);
            Log.d(TAG,"ImgHdr.ver = " + this.ver);
            Log.d(TAG,String.format("ImgHdr.uid = %02x%02x%02x%02x",this.uid[0],this.uid[1],this.uid[2],this.uid[3]));
            Log.d(TAG,"ImgHdr.addr = " + this.addr);
            Log.d(TAG,"ImgHdr.imgType = " + this.imgType);
            Log.d(TAG,String.format("ImgHdr.crc0 = %04x",this.crc0));
        }

        byte[] getRequest() {
            byte[] tmp = new byte[16];
            tmp[0] = Conversion.loUint16((short)this.crc0);
            tmp[1] = Conversion.hiUint16((short)this.crc0);
            tmp[2] = Conversion.loUint16((short)this.crc1);
            tmp[3] = Conversion.hiUint16((short)this.crc1);
            tmp[4] = Conversion.loUint16(this.ver);
            tmp[5] = Conversion.hiUint16(this.ver);
            tmp[6] = Conversion.loUint16((short)this.len);
            tmp[7] = Conversion.hiUint16((short)this.len);
            tmp[8] = tmp[9] = tmp[10] = tmp[11] = this.uid[0];
            tmp[12] = Conversion.loUint16(this.addr);
            tmp[13] = Conversion.hiUint16(this.addr);
            tmp[14] = imgType;
            tmp[15] = (byte)0xFF;
            return tmp;
        }

        short calcImageCRC(int page, byte[] buf) {
            short crc = 0;
            long addr = page * 0x1000;

            byte pageBeg = (byte)page;
            byte pageEnd = (byte)(this.len / (0x1000 / 4));
            int osetEnd = ((this.len - (pageEnd * (0x1000 / 4))) * 4);

            pageEnd += pageBeg;


            while (true) {
                int oset;

                for (oset = 0; oset < 0x1000; oset++) {
                    if ((page == pageBeg) && (oset == 0x00)) {
                        //Skip the CRC and shadow.
                        //Note: this increments by 3 because oset is incremented by 1 in each pass
                        //through the loop
                        oset += 3;
                    }
                    else if ((page == pageEnd) && (oset == osetEnd)) {
                        crc = this.crc16(crc,(byte)0x00);
                        crc = this.crc16(crc,(byte)0x00);

                        return crc;
                    }
                    else {
                        crc = this.crc16(crc,buf[(int)(addr + oset)]);
                    }
                }
                page += 1;
                addr = page * 0x1000;
            }


        }

        short crc16(short crc, byte val) {
            final int poly = 0x1021;
            byte cnt;
            for (cnt = 0; cnt < 8; cnt++, val <<= 1) {
                byte msb;
                if ((crc & 0x8000) == 0x8000) {
                    msb = 1;
                }
                else msb = 0;

                crc <<= 1;
                if ((val & 0x80) == 0x80) {
                    crc |= 0x0001;
                }
                if (msb == 1) {
                    crc ^= poly;
                }
            }

            return crc;
        }

    }

    private class ProgInfo {
        int iBytes = 0; // Number of bytes programmed
        short iBlocks = 0; // Number of blocks programmed
        short nBlocks = 0; // Total number of blocks
        int iTimeElapsed = 0; // Time elapsed in milliseconds

        void reset() {
            iBytes = 0;
            iBlocks = 0;
            iTimeElapsed = 0;
            nBlocks = (short) (mFileImgHdr.len / (OAD_BLOCK_SIZE / HAL_FLASH_WORD_SIZE));
        }
    }
    public void waitABit() {
        int waitTimeout = 20;
        while ((waitTimeout -= 10) > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
