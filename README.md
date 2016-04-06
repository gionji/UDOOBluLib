# RxUDOOBluLib

![alt tag](http://www.udoo.org/wp-content/uploads/2014/12/logoogo.png)

Library for Udoo Blu board 

# Usage

*For a working implementation of this project see the `example/`.*

  1. Include the library as local library project or add the dependency in your build.gradle.
        
        repositories {
            maven {
                url  "http://dl.bintray.com/harlem88/maven"
            }
        }

        ...

        dependencies {
            compile 'org.udoo:rxudooblulib:0.1'
        }

  2. In your `onCreate` method in Application class, bind the `UdooBluManager`.

             @Override
             public void onCreate() {
                 super.onCreate();
                 mUdooBluManager = new UdooBluManager(this);

             }

             public UdooBluManager getBluManager(){
                 return mUdooBluManager;
             }
             
  3. Scan ble device :
            
          mUooUdooBluManager.scanLeDevice(enable)
                .subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ScanResult>() {
            @Override
            public void onCompleted() {
                /* scan completed*/
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(ScanResult scanResult) {
                BluetoothDevice device = scanResult.getDevice();
            }
        });   

  4. Connect ble device:

            udooBluManager.connect(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        getFragmentManager().beginTransaction().replace(R.id.container, BlueNeoFragment.Builder(address)).commit();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean)
                            udooBluManager.discoveryServices(address);
                    }
                });

  5. Enable notifications

            udooBluManager.enableSensor(address1, UDOOBLESensor.ACCELEROMETER, true);
            udooBluManager.setNotificationPeriod(address1, UDOOBLESensor.ACCELEROMETER);

  5. Listen notifications
            
            udooBluManager.enableNotification(address1, true, UDOOBLESensor.ACCELEROMETER)
            .onBackpressureBuffer().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CharacteristicModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CharacteristicModel characteristicModel) {
                        Point3D accel3D = UDOOBLESensor.ACCELEROMETER.convert(characteristicModel.value);
                    }
                });
            
  6. Digital write
            
          mUdooBluManager.digitalWrite(address1, IOPIN_VALUE.HIGH, IOPIN.D6);
