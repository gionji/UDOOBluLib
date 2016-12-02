package org.udoo.bluhomeexample.manager; /**************************************************************************************************
 Filename:       FwUpdateActivity_CC26xx.java

 Copyright (c) 2013 - 2015 Texas Instruments Incorporated

 All rights reserved not granted herein.
 Limited License.

 Texas Instruments Incorporated grants a world-wide, royalty-free,
 non-exclusive license under copyrights and patents it now or hereafter
 owns or controls to make, have made, use, import, offer to sell and sell ("Utilize")
 this software subject to the terms herein.  With respect to the foregoing patent
 license, such license is granted  solely to the extent that any such patent is necessary
 to Utilize the software alone.  The patent license shall not apply to any combinations which
 include this software, other than combinations with devices manufactured by or for TI ('TI Devices').
 No hardware patent is licensed hereunder.

 Redistributions must preserve existing copyright notices and reproduce this license (including the
 above copyright notice and the disclaimer and (if applicable) source code license limitations below)
 in the documentation and/or other materials provided with the distribution

 Redistribution and use in binary form, without modification, are permitted provided that the following
 conditions are met:

 * No reverse engineering, decompilation, or disassembly of this software is permitted with respect to any
 software provided in binary form.
 * any redistribution and use are licensed by TI for use only with TI Devices.
 * Nothing shall obligate TI to provide you with source code for the software licensed and provided to you in object code.

 If software source code is provided to you, modification and redistribution of the source code are permitted
 provided that the following conditions are met:

 * any redistribution and use of the source code, including any resulting derivative works, are licensed by
 TI for use only with TI Devices.
 * any redistribution and use of any object code compiled from the source code and any resulting derivative
 works, are licensed by TI for use only with TI Devices.

 Neither the name of Texas Instruments Incorporated nor the names of its suppliers may be used to endorse or
 promote products derived from this software without specific prior written permission.

 DISCLAIMER.

 THIS SOFTWARE IS PROVIDED BY TI AND TI'S LICENSORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL TI AND TI'S LICENSORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.


 **************************************************************************************************/

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import org.udoo.udooblulib.exceptions.UdooBluException;
import org.udoo.udooblulib.interfaces.IBleDeviceListener;
import org.udoo.udooblulib.interfaces.INotificationListener;
import org.udoo.udooblulib.interfaces.OnBluOperationResult;
import org.udoo.udooblulib.manager.UdooBluManager;
import org.udoo.udooblulib.sensor.TIUUID;
import org.udoo.udooblulib.utils.Conversion;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

public class TIOADManager implements IBleDeviceListener{

    public interface IOADEvents{
        void onLoadProgress(int value);
        void onLoadCompleted();
        void onOADProcessCompleted();
        void onLoadError(int state);
    }

    private static String TAG = "TIOADManager";
    private IOADEvents mIoadEvents;

    // Programming parameters
    private static final short OAD_CONN_INTERVAL = 6; // 15 milliseconds
    private static final short OAD_SUPERVISION_TIMEOUT = 50; // 500 milliseconds
    private static final int GATT_WRITE_TIMEOUT = 300; // Milliseconds

    private static final int FILE_BUFFER_SIZE = 0x40000;
    public static final String FW_CUSTOM_DIRECTORY = Environment.DIRECTORY_DOWNLOADS;

    // Image Identification size
    private static final int OAD_IMG_ID_SIZE = 4;

    private static final int OAD_BLOCK_SIZE = 16;
    private static final int HAL_FLASH_WORD_SIZE = 4;
    private static final int OAD_BUFFER_SIZE = 2 + OAD_BLOCK_SIZE;
    private static final int HAL_FLASH_PAGE_SIZE = 4096;
    private static final int OAD_BLOCKS_PER_PAGE = 4096 / OAD_BLOCK_SIZE;
    private static final int OAD_IMG_HDR_SIZE = 8;
    private static final long TIMER_INTERVAL = 1000;

    private static final int SEND_INTERVAL = 20; // Milliseconds (make sure this is longer than the connection interval)
    private static final int BLOCKS_PER_CONNECTION = 20; // May sent up to four blocks per connection

    private static final boolean isOADLocalFile = true;
    private static final String OADLocalFileName = "v0.4.2C.bin";


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
    private UdooBluManager mBluManager;
    private String mAddress;

    // Programming
    private final byte[] mFileBuffer = new byte[FILE_BUFFER_SIZE];
    private final byte[] mOadBuffer = new byte[OAD_BUFFER_SIZE];
    private ImgHdr mFileImgHdr;
    private ImgHdr mTargImgHdr;
    private Timer mTimer = null;
    private ProgInfo mProgInfo = new ProgInfo();
    private TimerTask mTimerTask = null;
    private float firmwareRevision;
    private static final boolean slowAlgo = true;
    private int fastAlgoMaxPackets = BLOCKS_PER_CONNECTION;
    private String internalFWFilename;
    private int packetsSent = 0;

    // Housekeeping
    private boolean mServiceOk = false;
    private boolean mProgramming = false;
    private Context mContext;
    private ParcelFileDescriptor mOADFile;

    public TIOADManager(String address, ParcelFileDescriptor oADFile, UdooBluManager bluManager, Context context, IOADEvents ioadEvents) {

        mIoadEvents = ioadEvents;
        mAddress = address;
        mBluManager = bluManager;
        mContext = context;
        mOADFile = oADFile;
        mBluManager.setIBleDeviceListener(mAddress, this);
    }

    @Override
    public void onDeviceConnected() {
        if(mIoadEvents != null)
            mIoadEvents.onOADProcessCompleted();
    }

    @Override
    public void onDeviceDisconnect() {

    }

    @Override
    public void onError(UdooBluException runtimeException) {
        Log.e(TAG, "onError: " +runtimeException.getReason());
    }

    public void start(){
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                mOadService = mBluManager.getService(mAddress, TIUUID.UUID_OAD_SERV);
                mConnControlService = mBluManager.getService(mAddress, TIUUID.UUID_CONN_CTRL_SERV);

                // Characteristics list
                mCharListOad = mOadService.getCharacteristics();
                mCharListCc = mConnControlService.getCharacteristics();

                mServiceOk = mCharListOad.size() == 2 && mCharListCc.size() >= 3;
                if (mServiceOk) {

                    mCharConnReq = mConnControlService.getCharacteristic(TIUUID.UUID_CONN_CTRL_REQ);

                    mCharIdentify = mOadService.getCharacteristic(TIUUID.UUID_OAD_IMAGE_IDENTIFY);
                    mCharBlock = mOadService.getCharacteristic(TIUUID.UUID_OAD_BLOCK_REQUEST);
                    mCharBlock.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);

                    onLoad();
                }
            }
        });
    }

    private void onLoad() {
        subscribeOADImageIdentify();

        subscribeOADImageBlock();

        mBluManager.requestConnectionPriority(mAddress, BluetoothGatt.CONNECTION_PRIORITY_HIGH);
        setConnectionParameters(new OnBluOperationResult<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                Log.i(TAG, "onSuccess setConnectionParameters :" + aBoolean);
            }

            @Override
            public void onError(UdooBluException runtimeException) {
                Log.e(TAG, "onError setConnectionParameters: " + runtimeException.getMessage());
            }
        });

        if (loadFile(mContext, OADLocalFileName)) {
            startProgramming();
        }
    }


    private void subscribeOADImageIdentify(){
        mBluManager.subscribeNotification(mAddress, mCharIdentify, new INotificationListener<byte[]>() {
            @Override
            public void onNext(byte[] value) {
                // Block check here :
                Log.i(TAG, "onNext: Image");
                int size = value.length;
                for(int i = 0; i< size; i++){
                    Log.i(TAG, "onNext: Image " + value[i]);
                }
            }

            @Override
            public void onError(UdooBluException runtimeException) {
                Log.e(TAG, "onError: subscribeNotification" +runtimeException.getMessage());
            }
        });
    }

    private void subscribeOADImageBlock(){
        mBluManager.subscribeNotification(mAddress, mCharBlock, new INotificationListener<byte[]>() {
            @Override
            public void onNext(byte[] value) {
                // Block check here :
                Log.i(TAG, "onNext: ImageBlock");
                String block = String.format("%02x%02x", value[1], value[0]);
                Log.d(TAG, "Received block req: " + block);
                if (slowAlgo) {
                    programBlock();
                } else {
                    if (packetsSent != 0) packetsSent--;
                    if (packetsSent > 10) return;
                    while (packetsSent < fastAlgoMaxPackets) {
                        waitABit();
                        programBlock();
                    }
                }
            }

            @Override
            public void onError(UdooBluException runtimeException) {
                Log.e(TAG, "onError: subscribeNotification" +runtimeException.getMessage());
            }
        });

        }

    private void startProgramming() {
        Log.i(TAG, "startProgramming: Programming started");
        mProgramming = true;
        packetsSent = 0;
        updateGui();

        mBluManager.writeCharacteristic(mAddress, mCharIdentify, mFileImgHdr.getRequest(), new OnBluOperationResult<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {}

            @Override
            public void onError(UdooBluException runtimeException) {}
        });

        mProgInfo.reset(mFileImgHdr.len);
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
//        mProgressInfo.setText("");
//        mProgressBar.setProgress(0);
        updateGui();

        mBluManager.unSubscribeNotification(mAddress, mCharBlock, new OnBluOperationResult<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {}

            @Override
            public void onError(UdooBluException runtimeException) {}
        });

        if (mProgInfo.iBlocks == mProgInfo.nBlocks) {
            Log.i(TAG, "stopProgramming: Programming complete!");
            if(mIoadEvents != null)
                mIoadEvents.onLoadCompleted();
        } else {
            if(mIoadEvents != null)
                mIoadEvents.onLoadError(-1);
            Log.i(TAG, "stopProgramming: Programming cancelled");
        }
    }

    private void updateGui() {
        if (mProgramming) {
            Log.i(TAG, "updateGui: programming");
            // Busy: stop label, progress bar, disabled file selector
//            mBtnStart.setText(R.string.cancel);
//            mBtnLoadA.setEnabled(false);
//            mBtnLoadC.setEnabled(false);
        } else {
            Log.i(TAG, "updateGui: no programming");
            // Idle: program label, enable file selector
//            mProgressBar.setProgress(0);
//            mBtnStart.setText(R.string.start_prog);
//            mBtnLoadA.setEnabled(true);
//            mBtnLoadC.setEnabled(true);
        }
    }

    private boolean loadFile(Context ctx, String filepath) {
        boolean fSuccess = false;
        int readLen = 0;
        // Load binary file
        try {
            // Read the file raw into a buffer
            InputStream stream;
            if (isOADLocalFile) {
                stream = ctx.getAssets().open(filepath);
            } else {
                stream = new ParcelFileDescriptor.AutoCloseInputStream(mOADFile);
            }
            readLen = stream.read(mFileBuffer, 0, mFileBuffer.length);
            stream.close();
            fSuccess = true;
        } catch (IOException e) {
            // Handle exceptions here
            Log.e(TAG, "loadFile: File open failed: " + filepath );
        }

        if (!isOADLocalFile) {
//            mFileImage.setText(filepath);
        }

        if (fSuccess) {
            mFileImgHdr = new ImgHdr(mFileBuffer, readLen);

            // Expected duration
            displayStats();

            // Log
            Log.i(TAG, "Programming image : " + filepath);
            Log.i(TAG, "File size : " + readLen + " bytes (" + (int) Math.ceil(readLen / (float) 16) + ") blocks");
            Log.i(TAG, "Ready to program device!");

            updateGui();
        }

        return fSuccess;
    }

    private void displayStats() {

        int byteRate;
        int sec = mProgInfo.iTimeElapsed / 1000;
        if (sec > 0) {
            byteRate = mProgInfo.iBytes / sec;
        } else {
            byteRate = 0;
            return;
        }
        float timeEstimate = ((float)(mFileImgHdr.len *4) / (float)mProgInfo.iBytes) * sec;

        String txt = String.format("Time: %d / %d sec", sec, (int)timeEstimate);
        String txt2 = String.format("    Bytes: %d (%d/sec)", mProgInfo.iBytes, byteRate);
        Log.i(TAG, "displayStats: " + txt);
        Log.i(TAG, "displayStats: " + txt2);
        if(mIoadEvents != null)
            mIoadEvents.onLoadProgress((mProgInfo.iBytes * 100 ) / (mFileImgHdr.len * 4));

    }

    private void setConnectionParameters(OnBluOperationResult<Boolean> result) {
        byte[] value = {Conversion.loUint16(OAD_CONN_INTERVAL), Conversion.hiUint16(OAD_CONN_INTERVAL), Conversion.loUint16(OAD_CONN_INTERVAL),
                Conversion.hiUint16(OAD_CONN_INTERVAL), 0, 0, Conversion.loUint16(OAD_SUPERVISION_TIMEOUT), Conversion.hiUint16(OAD_SUPERVISION_TIMEOUT)};

        mBluManager.writeCharacteristic(mAddress, mCharConnReq, value, result);
    }

  /*
   * Called when a notification with the current image info has been received
   */

    private void programBlock() {
        Log.i(TAG, "programBlock: ");
        if (!mProgramming)
            return;

        if (mProgInfo.iBlocks < mProgInfo.nBlocks) {
            mProgramming = true;

            // Prepare block
            mOadBuffer[0] = Conversion.loUint16(mProgInfo.iBlocks);
            mOadBuffer[1] = Conversion.hiUint16(mProgInfo.iBlocks);
            System.arraycopy(mFileBuffer, mProgInfo.iBytes, mOadBuffer, 2, OAD_BLOCK_SIZE);


            Log.i(TAG, "block 0 " + mOadBuffer[0]);
            Log.i(TAG, "block 1 " + mOadBuffer[1]);

            boolean success = mBluManager.writeCharacteristicNonBlock(mAddress, mCharBlock, mOadBuffer);

            String msg = "";
            if (success) {
                // Update stats
                packetsSent++;
                mProgInfo.iBlocks++;
                mProgInfo.iBytes += OAD_BLOCK_SIZE;
                Log.i(TAG, "programBlock: " + packetsSent);
//                        mProgressBar.setProgress((mProgInfo.iBlocks * 100) / mProgInfo.nBlocks);
                if (mProgInfo.iBlocks == mProgInfo.nBlocks) {

                    mProgramming = false;
                    Log.i(TAG, "Programming finished at block " + (mProgInfo.iBlocks ) + "\n");
                }
            } else {
                mProgramming = false;
                msg = "GATT writeCharacteristic failed\n";
            }
            if (!success) {
                Log.i(TAG, msg);
            }

        } else {
            mProgramming = false;
        }
        if ((mProgInfo.iBlocks % 10) == 0) {
            displayStats();
        }

        if (!mProgramming) {
            displayStats();
            stopProgramming();
        }
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (mTimerTask != null)
            mTimerTask.cancel();
        mTimer = null;
    }

    public void onStart() {
        if (mProgramming) {
            stopProgramming();
        } else {
            startProgramming();
        }
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
            //len in words each is HAL_FLASH_WORD_SIZE
            this.len = (short) Math.ceil(fileLen / (16.0f / 4));
            this.ver = 0;

            this.uid[0] = this.uid[1] = this.uid[2] = this.uid[3] = 'E';
            this.addr = 0;
            this.imgType = 1; //EFL_OAD_IMG_TYPE_APP
            this.crc0 = calcImageCRC((int) 0, mProgInfo.calculateBlocks(this.len), buf);
            Log.i(TAG, "calcImageCRC: " + this.crc0);
            crc1 = (short)0xFFFF;
            Log.d("FwUpdateActivity_CC26xx","ImgHdr.len = " + this.len);
            Log.d("FwUpdateActivity_CC26xx","ImgHdr.ver = " + this.ver);
            Log.d("FwUpdateActivity_CC26xx",String.format("ImgHdr.uid = %02x%02x%02x%02x",this.uid[0],this.uid[1],this.uid[2],this.uid[3]));
            Log.d("FwUpdateActivity_CC26xx","ImgHdr.addr = " + this.addr);
            Log.d("FwUpdateActivity_CC26xx","ImgHdr.imgType = " + this.imgType);
            Log.d("FwUpdateActivity_CC26xx",String.format("ImgHdr.crc0 = %04x",this.crc0));
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

        short calcImageCRC(int page, short nBlocksTot, byte[] buf) {
            short crc = 0;
            long addr = page * 0x1000;

            byte pageBeg = (byte)page;

            byte pageEnd =(byte) (nBlocksTot / OAD_BLOCKS_PER_PAGE);
            int numRemBytes = (nBlocksTot - (pageEnd * OAD_BLOCKS_PER_PAGE)) * OAD_BLOCK_SIZE;

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
                    else if ((page == pageEnd) && (oset == numRemBytes)) {

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

        void reset(int len) {
            iBytes = 0;
            iBlocks = 0;
            iTimeElapsed = 0;
            nBlocks = calculateBlocks(len);
        }

        short calculateBlocks (int len){
            return (short)  Math.ceil(len / ((float)OAD_BLOCK_SIZE / HAL_FLASH_WORD_SIZE));
        }
    }

    private void waitABit() {
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
