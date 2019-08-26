package com.wunmansho.portfoliosamples;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.auto.accident.report.R;
import com.auto.accident.report.models.DeviceImageStoreDao;
import com.auto.accident.report.models.DeviceUserDao;
import com.auto.accident.report.models.InvolvedImageStoreDao;
import com.auto.accident.report.models.PersistenceObjDao;
import com.auto.accident.report.objects.DeviceImageStore;
import com.auto.accident.report.objects.DeviceUser;
import com.auto.accident.report.objects.InvolvedImageStore;
import com.auto.accident.report.objects.PersistenceObj;
import com.auto.accident.report.presenter.AccidentMenu;
import com.auto.accident.report.presenter.AddDeviceUser;
import com.auto.accident.report.presenter.AddDeviceVehicle;
import com.auto.accident.report.presenter.AddInvolvedParty;
import com.auto.accident.report.presenter.AddInvolvedVehicle;
import com.auto.accident.report.presenter.ListDeviceUser;
import com.auto.accident.report.presenter.ListDeviceVehicle;
import com.auto.accident.report.presenter.ListInvolvedMenu;
import com.auto.accident.report.presenter.MDToast;
import com.auto.accident.report.presenter.MultiMediaMenu;
import com.auto.accident.report.presenter.SelectNoteAttachment;
import com.auto.accident.report.presenter.UpdateDeviceUser;
import com.auto.accident.report.presenter.UpdateDeviceVehicle;
import com.auto.accident.report.presenter.UpdateInvolvedParty;
import com.auto.accident.report.presenter.UpdateInvolvedVehicle;
import com.auto.accident.report.util.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.auto.accident.report.util.utils.isNumber;


public class PhotoGalleryActivity extends Activity {


    private PersistenceObjDao mPersistenceObjDao;
    private File file;
    private List<Person> persons;
    private RecyclerView photoGalleryAdapter;
    private Context context;
    private int maxScroll;
    private String GALLERY_CALLER;
    private RotateAnimation rotateAnimation;
    private String rsMode1;
    private DeviceUserDao mDeviceUserDao;
    private DeviceUser deviceUser;
    private PersistenceObj persistenceObj;
    private Intent intent;
    private Resources res;
    private MDToast mdToast;
    private InvolvedImageStoreDao mInvolvedImageStoreDao;
    private DeviceImageStoreDao mDeviceImageStoreDao;
    private Intent fullScreenIntent;
    private AssetManager assetManager;
    private String filename;
    private String albumName;
    private File mStorageDirectory;
    private String mImageFileLocation;
    private String DA_ICON;
    private InputStream in;
    private OutputStream out;
    private String DA_ID;
    private int IP_ID;
    private String DA_ID_STR;
    private int DUX_ID;
    private int DU_ID;
    private int DV_ID;
    private int IV_ID;
    private int AID_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.photo_gallery_adapter);

        photoGalleryAdapter = findViewById(R.id.photoGalleryAdapter);
        int numberOfColumns = 3;
        photoGalleryAdapter.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
       mPersistenceObjDao = new PersistenceObjDao(this);
        mDeviceUserDao = new DeviceUserDao(this);
        mInvolvedImageStoreDao = new InvolvedImageStoreDao(this);
        mDeviceImageStoreDao = new DeviceImageStoreDao(this);



        persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_GALLERY_CALLER");
        String rsMode = persistenceObj.getPERSISTENCE_VALUE();

        persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_DU_ID");
        DA_ID_STR = persistenceObj.getPERSISTENCE_VALUE();
        if (isNumber(DA_ID_STR)) {
            DUX_ID = Integer.parseInt(DA_ID_STR);
        } else {
            DUX_ID = 0;
        }


        deviceUser = mDeviceUserDao.getDeviceUser(DUX_ID);
        String DU_FNAME = deviceUser.getDU_FNAME();

        Toolbar toolbar = findViewById(R.id.image_gallery_toolbar);
        if (rsMode.equals("SELECT_NOTE_ATTACHMENT")) {
            String[] splitString;
            int splitLength;
            String DA_RESULT2;
            splitString = DU_FNAME.split(" ");
            DA_RESULT2 = splitString[0];

            toolbar.setSubtitle(getString(R.string.welcome) + " " + DA_RESULT2 + " - " + getString(R.string.sna));
        }
        toolbar.setNavigationOnClickListener(v -> {
            rotateAnimation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(300);
            View btnBack = toolbar.getChildAt(2);
	        PersistenceObj persistenceObj1 = mPersistenceObjDao.getPersistence("PERSIST_GALLERY_CALLER");
            rsMode1 = persistenceObj1.getPERSISTENCE_VALUE();
            scheduleDismissToolbar();

            btnBack.startAnimation(rotateAnimation);
               });
        albumName = "AccidentReport/ic_record_voice_over_black.jpg";
        mStorageDirectory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        mImageFileLocation = mStorageDirectory.getAbsolutePath();
      //  if (!mStorageDirectory.exists()) {
            mPersistenceObjDao.updateData("PERSIST_AUDIO_ICON", mImageFileLocation);
            in = null;
            out = null;
            filename = "ic_record_voice_over_black.jpg";
            try {
                assetManager = getAssets();
                in = assetManager.open(filename);
                File outFile = new File(mImageFileLocation);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
       // }
        initializeData();
        initializeAdapter();

        }

    private void initializeData() {
        persons = new ArrayList<>();


        persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_PIC_MODE");
        String picMode = persistenceObj.getPERSISTENCE_VALUE();
        persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_GALLERY_CALLER");
        String rsMode = persistenceObj.getPERSISTENCE_VALUE();

        String[] storedPathStrings;
        int type;
        int duration;
        String message;
        int j;
        if (rsMode.equals("LIST_ACCIDENT_MENU") || rsMode.equals("INVOLVED_MENU") || rsMode.equals("LIST_INVOLVED_PARTY")
                || rsMode.equals("LIST_INVOLVED_VEHICLE") || rsMode.equals("UPDATE_INVOLVED_PARTY")
                || rsMode.equals("ADD_INVOLVED_VEHICLE") || rsMode.equals("ADD_INVOLVED_PARTY") || rsMode.equals("LIST_ACCIDENT") || rsMode.equals("SELECT_NOTE_ATTACHMENT")) {

            persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_AID_ID");
            DA_ID_STR = persistenceObj.getPERSISTENCE_VALUE();
            if (isNumber(DA_ID_STR)) {
                AID_ID = Integer.parseInt(DA_ID_STR);
            } else {
                AID_ID = 0;
            }
            persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_IP_ID");
            DA_ID = persistenceObj.getPERSISTENCE_VALUE();
            if (!DA_ID.equals("") &&  !DA_ID.equals("0") && !DA_ID.equals("-1")) {
                 IP_ID = Integer.parseInt(DA_ID);
            }
            persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_IV_ID");
            DA_ID_STR = persistenceObj.getPERSISTENCE_VALUE();
            if (isNumber(DA_ID_STR)) {
                IV_ID = Integer.parseInt(DA_ID_STR);
            } else {
                IV_ID = 0;
            }
            List<InvolvedImageStore> involvedImageStoreList = new ArrayList<>();
            if (picMode.equals("ACCIDENT")) {
                involvedImageStoreList = mInvolvedImageStoreDao.getAllAccPics(AID_ID);
            }
            if (picMode.equals("INVOLVED_PARTY")) {
                involvedImageStoreList = mInvolvedImageStoreDao.getAccPics(AID_ID, IP_ID);
            }
            if (picMode.equals("INVOLVED_VEHICLE")) {
                involvedImageStoreList = mInvolvedImageStoreDao.getAccPics(AID_ID, IV_ID);
            }
            if (picMode.equals("SINGLE_PICTURE")) {
                persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_AP_FILENAME");
                String AP_FILENAME = persistenceObj.getPERSISTENCE_VALUE();
                involvedImageStoreList = mInvolvedImageStoreDao.getNewImage(AP_FILENAME);
            }

            //   mInvolvedImageStoreDao.chgData();
            storedPathStrings = new String[involvedImageStoreList.size()];
            j = 0;
            for (InvolvedImageStore involvedImageStore : involvedImageStoreList) {
                // for (int i = 0; i < involvedImageStoreList.size(); i++) {
                //    StoredPathStrings[j] = involvedImageStore.getAP_PATH();

                String StoredPathString = involvedImageStore.getAP_PATH();
                persons.add(new Person(StoredPathString));
//Path path = Paths.get(StoredPathStrings[j]);
                j++;
            }
            //  indicator.setViewPager(mPager);

            if (j == 0) {

                if (rsMode.equals("LIST_INVOLVED_MENU")) {
                    intent = new Intent(PhotoGalleryActivity.this, ListInvolvedMenu.class);
                    startActivity(intent);
                }
                if (rsMode.equals("ACCIDENT_MENU")) {
                    intent = new Intent(PhotoGalleryActivity.this, MultiMediaMenu.class);
                    startActivity(intent);
                }
                if (rsMode.equals("LIST_INVOLVED_PARTY")) {
                    intent = new Intent(PhotoGalleryActivity.this, MultiMediaMenu.class);
                    startActivity(intent);
                }
                if (rsMode.equals("UPDATE_INVOLVED_PARTY")) {
                    intent = new Intent(PhotoGalleryActivity.this, UpdateInvolvedParty.class);
                    startActivity(intent);
                }
                if (rsMode.equals("ADD_INVOLVED_PARTY")) {
                    intent = new Intent(PhotoGalleryActivity.this, AddInvolvedParty.class);
                    startActivity(intent);
                }
                if (rsMode.equals("LIST_INVOLVED_VEHICLE")) {
                    intent = new Intent(PhotoGalleryActivity.this, MultiMediaMenu.class);
                    startActivity(intent);
                }
                if (rsMode.equals("UPDATE_INVOLVED_VEHICLE")) {
                    intent = new Intent(PhotoGalleryActivity.this, UpdateInvolvedVehicle.class);
                    startActivity(intent);
                }
                if (rsMode.equals("ADD_INVOLVED_VEHICLE")) {
                    intent = new Intent(PhotoGalleryActivity.this, AddInvolvedVehicle.class);
                    startActivity(intent);
                }
                if (rsMode.equals("LIST_ACCIDENT")) {
                    intent = new Intent(PhotoGalleryActivity.this, MultiMediaMenu.class);
                    startActivity(intent);
                }
                if (rsMode.equals("SELECT_NOTE_ATTACHMENT")) {
                    intent = new Intent(PhotoGalleryActivity.this, SelectNoteAttachment.class);
                    startActivity(intent);
                }

                Resources res = getResources();
                message = res.getString(R.string.tv0500);
                duration = 40;
                type = 0;
                mdToast = MDToast.makeText(PhotoGalleryActivity.this, message, duration, type);
                mdToast.setGravity(Gravity.TOP, 50, 200);
                mdToast.show();

            }

        } else if (rsMode.equals("LIST_DEVICE_USER") || rsMode.equals("LIST_DEVICE_VEHICLE")
                || rsMode.equals("UPDATE_DEVICE_USER") || rsMode.equals("UPDATE_DEVICE_VEHICLE")
                || rsMode.equals("ADD_DEVICE_USER") || rsMode.equals("ADD_DEVICE_VEHICLE")) {
            persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_DU_ID");
            DA_ID_STR = persistenceObj.getPERSISTENCE_VALUE();
            if (isNumber(DA_ID_STR)) {
                DU_ID = Integer.parseInt(DA_ID_STR);
            } else {
                DU_ID = 0;
            }
            persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_DV_ID");
            DA_ID_STR = persistenceObj.getPERSISTENCE_VALUE();
            if (isNumber(DA_ID_STR)) {
                DV_ID = Integer.parseInt(DA_ID_STR);
            } else {
                DV_ID = 0;
            }
            List<DeviceImageStore> deviceImageStoreList = new ArrayList<>();
            if (picMode.equals("DEVICE")) {
                deviceImageStoreList = mDeviceImageStoreDao.getAllDevPics();
            }
            if (picMode.equals("DEVICE_USER")) {
                deviceImageStoreList = mDeviceImageStoreDao.getDevPics(DU_ID);
            }
            if (picMode.equals("DEVICE_VEHICLE")) {
                deviceImageStoreList = mDeviceImageStoreDao.getDevPics(DV_ID);
            }
            if (picMode.equals("SINGLE_PICTURE")) {
                persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_DU_FILENAME");
                String DU_FILENAME = persistenceObj.getPERSISTENCE_VALUE();
                deviceImageStoreList = mDeviceImageStoreDao.getNewImage(DU_FILENAME);
            }

            storedPathStrings = new String[deviceImageStoreList.size()];
            j = 0;
            for (DeviceImageStore deviceImageStore : deviceImageStoreList) {
                // for (int i = 0; i < deviceImageStoreList.size(); i++) {
                //    StoredPathStrings[j] = deviceImageStore.getDP_PATH();
                String StoredPathString = deviceImageStore.getDP_PATH();
                persons.add(new Person(StoredPathString));
//Path path = Paths.get(StoredPathStrings[j]);
                j++;
            }
            //  indicator.setViewPager(mPager);

            if (j == 0) {
                persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_GALLERY_CALLER");
                rsMode = persistenceObj.getPERSISTENCE_VALUE();


                if (rsMode.equals("LIST_DEVICE_USER")) {
                    intent = new Intent(PhotoGalleryActivity.this, ListDeviceUser.class);
                    startActivity(intent);
                }
                if (rsMode.equals("UPDATE_DEVICE_USER")) {
                    intent = new Intent(PhotoGalleryActivity.this, UpdateDeviceUser.class);
                    startActivity(intent);
                }
                if (rsMode.equals("ADD_DEVICE_USER")) {
                    intent = new Intent(PhotoGalleryActivity.this, AddDeviceUser.class);
                    startActivity(intent);
                }
                if (rsMode.equals("LIST_DEVICE_VEHICLE")) {
                    intent = new Intent(PhotoGalleryActivity.this, ListDeviceVehicle.class);
                    startActivity(intent);
                }
                if (rsMode.equals("UPDATE_DEVICE_VEHICLE")) {
                    intent = new Intent(PhotoGalleryActivity.this, UpdateDeviceVehicle.class);
                    startActivity(intent);
                }
                if (rsMode.equals("ADD_DEVICE_VEHICLE")) {
                    intent = new Intent(PhotoGalleryActivity.this, AddDeviceVehicle.class);
                    startActivity(intent);
                }

                Resources res = getResources();
                message = res.getString(R.string.tv0500);
                duration = 40;
                type = 0;
                mdToast = MDToast.makeText(PhotoGalleryActivity.this, message, duration, type);
                mdToast.setGravity(Gravity.TOP, 50, 200);
                mdToast.show();

            }
        }
    }  private void initializeAdapter() {
        PhotoGalleryAdapter adapter = new PhotoGalleryAdapter(getApplicationContext(), persons, (v, position) -> {
            //   Toast.makeText(PhotoGalleryActivity.this, "Clicked Item: "+position,Toast.LENGTH_SHORT).show();
            String image = persons.get(position).photoId;
            String fileExt = utils.splitFileExt(image);
            if (fileExt.equals("mp4")) {
                fullScreenIntent = new Intent(PhotoGalleryActivity.this, VideoPlayActivity.class);
                fullScreenIntent.putExtra("DA_IMAGE", image);
                startActivity(fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
            if (fileExt.equals("jpg")) {
                fullScreenIntent = new Intent(PhotoGalleryActivity.this, FullScreenImageActivity.class);
                fullScreenIntent.putExtra("DA_IMAGE", image);
                startActivity(fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
            if (fileExt.equals("gif")) {
                fullScreenIntent = new Intent(PhotoGalleryActivity.this, FullScreenImageActivity.class);
                fullScreenIntent.putExtra("DA_IMAGE", image);
                startActivity(fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
            if (fileExt.equals("m4a")) {
                fullScreenIntent = new Intent(PhotoGalleryActivity.this, VideoPlayActivity.class);
                fullScreenIntent.putExtra("DA_IMAGE", image);
                startActivity(fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }

            // startActivity(fullScreenIntent);


        });
        photoGalleryAdapter.setAdapter(adapter);
    }


      private void scheduleDismissToolbar() {
        Handler handler = new Handler();
        handler.postDelayed(this::dismissActivity, 250);
    }
    private void dismissActivity() {

        if (rsMode1.equals("LIST_INVOLVED_MENU")) {
            intent = new Intent(PhotoGalleryActivity.this, ListInvolvedMenu.class);
            startActivity(intent);
        }
        if (rsMode1.equals("LIST_ACCIDENT")) {
            intent = new Intent(PhotoGalleryActivity.this, MultiMediaMenu.class);
            startActivity(intent);
        }
        if (rsMode1.equals("ACCIDENT_MENU")) {
            intent = new Intent(PhotoGalleryActivity.this, AccidentMenu.class);
            startActivity(intent);
        }
        if (rsMode1.equals("LIST_INVOLVED_PARTY")) {
            intent = new Intent(PhotoGalleryActivity.this, MultiMediaMenu.class);
            startActivity(intent);
        }
        if (rsMode1.equals("UPDATE_INVOLVED_PARTY")) {
            intent = new Intent(PhotoGalleryActivity.this, UpdateInvolvedParty.class);
            startActivity(intent);
        }
        if (rsMode1.equals("ADD_INVOLVED_PARTY")) {
            intent = new Intent(PhotoGalleryActivity.this, AddInvolvedParty.class);
            startActivity(intent);
        }
        if (rsMode1.equals("LIST_INVOLVED_VEHICLE")) {
            intent = new Intent(PhotoGalleryActivity.this, MultiMediaMenu.class);
            startActivity(intent);
        }
        if (rsMode1.equals("UPDATE_INVOLVED_VEHICLE")) {
            intent = new Intent(PhotoGalleryActivity.this, UpdateInvolvedVehicle.class);
            startActivity(intent);
        }
        if (rsMode1.equals("ADD_INVOLVED_VEHICLE")) {
            intent = new Intent(PhotoGalleryActivity.this, AddInvolvedVehicle.class);
            startActivity(intent);
        }

        if (rsMode1.equals("LIST_DEVICE_USER")) {
            intent = new Intent(PhotoGalleryActivity.this, MultiMediaMenu.class);
            startActivity(intent);
        }
        if (rsMode1.equals("UPDATE_DEVICE_USER")) {
            intent = new Intent(PhotoGalleryActivity.this, UpdateDeviceUser.class);
            startActivity(intent);
        }
        if (rsMode1.equals("ADD_DEVICE_USER")) {
            intent = new Intent(PhotoGalleryActivity.this, AddDeviceUser.class);
            startActivity(intent);
        }
        if (rsMode1.equals("LIST_DEVICE_VEHICLE")) {
            intent = new Intent(PhotoGalleryActivity.this, MultiMediaMenu.class);
            startActivity(intent);
        }
        if (rsMode1.equals("UPDATE_DEVICE_VEHICLE")) {
            intent = new Intent(PhotoGalleryActivity.this, UpdateDeviceVehicle.class);
            startActivity(intent);
        }
        if (rsMode1.equals("ADD_DEVICE_VEHICLE")) {
            intent = new Intent(PhotoGalleryActivity.this, AddDeviceVehicle.class);
            startActivity(intent);
        }
        if (rsMode1.equals("SELECT_NOTE_ATTACHMENT")) {
            intent = new Intent(PhotoGalleryActivity.this, SelectNoteAttachment.class);
            startActivity(intent);
        }

    }
    public void doClose() {
        // mInsurancePolicyVDao.closeAll();
        mDeviceImageStoreDao.closeAll();
        //mPartyTypeDao.closeAll();
        mInvolvedImageStoreDao.closeAll();
        //  mVehicleTypeDao.closeAll();
        //mAccidentNoteDao.closeAll();
        //mInvolvedPartyDao.closeAll();

        mDeviceUserDao.closeAll();

        //mInsurancePolicyPDao.closeAll();
        //mInvolvedVehicleDao.closeAll();
        //mAccidentIdDao.closeAll();
        mPersistenceObjDao.closeAll();
        //mPremiumAdvertiserDao.closeAll();
        //mInsurancePolicyDao.closeAll();
        //mDeviceVehicleDao.closeAll();
        //mVehicleManifestDao.closeAll();

    }
    @Override
    public void onBackPressed() {
        doClose();
        intent = new Intent(this, MultiMediaMenu.class);
        startActivity(intent);
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}
