package com.wunmansho.portfoliosamples;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.auto.accident.report.R;
import com.auto.accident.report.models.DeviceImageStoreDao;
import com.auto.accident.report.models.DeviceUserDao;
import com.auto.accident.report.models.InvolvedPartyDao;
import com.auto.accident.report.models.PersistenceObjDao;
import com.auto.accident.report.objects.DeviceImageStore;
import com.auto.accident.report.objects.DeviceUser;
import com.auto.accident.report.objects.InvolvedParty;
import com.auto.accident.report.objects.PersistenceObj;
import com.auto.accident.report.photos.CameraActivity;
import com.auto.accident.report.photos.PhotoGalleryActivity;

import java.util.ArrayList;
import java.util.List;

import static com.auto.accident.report.util.utils.isNumber;

/**
 * Created by myron on 1/18/2018.
 */

class ListDeviceUserAdapter extends BaseAdapter {
    //   private LinearLayout ll01;
    private static LayoutInflater inflater = null;
    private Context context;
    private InvolvedPartyDao mInvolvedPartyDao;
    private final PersistenceObjDao mPersistenceObjDao;
    private final DeviceUserDao mDeviceUserDao;
    private final DeviceImageStoreDao mDeviceImageStoreDao;
    //  String [] result0;
    private final ArrayList<String> rsDU_ID = new ArrayList<>();
    private final ArrayList<String> rsDU_FNAME = new ArrayList<>();
    private final ArrayList<String> rsDU_PTYPE = new ArrayList<>();
    private final int alpha1 = 255;
    private final int alpha2 = 50;
    private Boolean fireClick = true;
    private String message;
    private InvolvedParty involvedParty;

    private String DA_CALLER;

    private String rsMode;
    private PersistenceObj persistenceObj;
    private DeviceUser deviceUser;
    private Intent intent;
    private RotateAnimation rotateAnimation;

    private int AID_ID;
    private int DU_ID;
    private int DU_ID1;
    private int DUX_ID;


    private int pos;

    private int XX_AID;
    private String DA_ID;
    private String DA_ID_STR;
    private String PERSIST_ACTION_IN_PROGRESS;

    public ListDeviceUserAdapter(ListDeviceUser ListDeviceUser) {
        // TODO Auto-generated constructor stub
        context = ListDeviceUser;
        mPersistenceObjDao = new PersistenceObjDao(context);
        persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_DU_MODE");
        rsMode = persistenceObj.getPERSISTENCE_VALUE();

        mDeviceImageStoreDao = new DeviceImageStoreDao(context);
        mDeviceUserDao = new DeviceUserDao(context);
        List<DeviceUser> deviceuserList = new ArrayList<>();

        persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_DU_CALLER");
        DA_CALLER = persistenceObj.getPERSISTENCE_VALUE();
        if (!DA_CALLER.equals("LIST_INVOLVED_PARTY")) {
            deviceuserList = mDeviceUserDao.getAllDeviceUsers();
        }
        if (DA_CALLER.equals("LIST_INVOLVED_PARTY")) {
            persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_AID_ID");
            DA_ID_STR = persistenceObj.getPERSISTENCE_VALUE();
            if (isNumber(DA_ID_STR)) {
                AID_ID = Integer.parseInt(DA_ID_STR);
            } else {
                AID_ID = 0;
            }


            deviceuserList = mDeviceUserDao.getAllDeviceUsersNotInvolved(AID_ID);
        }

        for (DeviceUser deviceUser : deviceuserList) {
            rsDU_ID.add(Integer.toString(deviceUser.getDU_ID()));
            rsDU_FNAME.add(deviceUser.getDU_FNAME());
            ArrayList<String> rsDU_MI = new ArrayList<>();
            rsDU_MI.add(deviceUser.getDU_MI());
            ArrayList<String> rsDU_LNAME = new ArrayList<>();
            rsDU_LNAME.add(deviceUser.getDU_LNAME());
            rsDU_PTYPE.add(deviceUser.getDU_PTYPE());

        }
        mInvolvedPartyDao = new InvolvedPartyDao(context);


        mPersistenceObjDao.updateData("PERSIST_CAMERA_CALLER", "LIST_DEVICE_USER");
        mPersistenceObjDao.updateData("PERSIST_PIC_MODE", "DEVICE_USER");
        mPersistenceObjDao.updateData("PERSIST_GALLERY_CALLER", "LIST_DEVICE_USER");
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return rsDU_ID.size();

    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.list_device_user_adapter, null);
        holder.ll01 = rowView.findViewById(R.id.ll01);
        holder.btnGallery = rowView.findViewById(R.id.btnGallery);
        holder.btnCamera = rowView.findViewById(R.id.btnCamera);
        holder.btnMedia = rowView.findViewById(R.id.btnMedia);
        holder.tvDU_PTYPE = rowView.findViewById(R.id.tvDU_PTYPE);
        holder.tvDU_ID = rowView.findViewById(R.id.tvDU_ID);
        holder.tvDU_FNAME = rowView.findViewById(R.id.tvDU_FNAME);
        //    holder.tv03 = rowView.findViewById(R.id.tvDU_MI);
        //    holder.tv04 = rowView.findViewById(R.id.tvDU_LNAME);

        holder.tvDU_ID.setText(rsDU_ID.get(position));
        holder.tvDU_FNAME.setText(rsDU_FNAME.get(position));
        //     holder.tv03.setText(rsDU_MI.get(position));
        //     holder.tv04.setText(rsDU_LNAME.get(position));
        holder.tvDU_PTYPE.setText(rsDU_PTYPE.get(position));


        Boolean clickImage;
        if (rsMode.equals("SELECT")) {
            //   holder.ll01.setVisibility(View.GONE);
            //  holder.btnGallery.setVisibility(View.GONE);
            //   holder.btnCamera.setVisibility(View.GONE);
            //  clickImage = false;
        }
        DA_ID_STR = rsDU_ID.get(position);
        if (isNumber(DA_ID_STR)) {
            DU_ID = Integer.parseInt(DA_ID_STR);
        } else {
            DU_ID = 0;
        }

        List<DeviceImageStore> deviceImageStoreList = new ArrayList<>();

        deviceImageStoreList = mDeviceImageStoreDao.getDevPics(DU_ID);
        clickImage = true;
        if (deviceImageStoreList.size() == 0) {
            holder.btnGallery.setImageAlpha(alpha2);
            clickImage = false;
        }
        if (1 == 2) {
            if (clickImage) {
                holder.btnGallery.setOnClickListener(view -> {
                    if (fireClick == true) {
                        persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_ACTION_IN_PROGRESS");
                        PERSIST_ACTION_IN_PROGRESS = persistenceObj.getPERSISTENCE_VALUE();
                        if (PERSIST_ACTION_IN_PROGRESS.equals("false")) {

                            context = view.getContext();
                            String DA_ID = rsDU_ID.get(position);
                            mPersistenceObjDao.updateData("PERSIST_DU_ID", DA_ID);
                            intent = new Intent(context, PhotoGalleryActivity.class);
                            rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                            rotateAnimation.setRepeatCount(1);
                            rotateAnimation.setRepeatMode(Animation.RELATIVE_TO_SELF);
                            rotateAnimation.setDuration(100);
                            holder.btnGallery.startAnimation(rotateAnimation);
                            pos = position;
                            scheduleDismissIntent();
                        }
                    }
                    holder.btnGallery.setImageAlpha(alpha1);
                    fireClick = true;
                });
                holder.btnGallery.setOnLongClickListener(view -> {
                    persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_ACTION_IN_PROGRESS");
                    PERSIST_ACTION_IN_PROGRESS = persistenceObj.getPERSISTENCE_VALUE();
                    if (PERSIST_ACTION_IN_PROGRESS.equals("false")) {

                        context = view.getContext();
                        Resources res = context.getResources();
                        holder.btnGallery.setImageAlpha(alpha2);
                        message = res.getString(R.string.wipp);
                        makeToast();

                        fireClick = false;
                    }
                        return false;

                });

            }
            holder.btnCamera.setOnClickListener(view -> {
                if (fireClick == true) {
                    persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_ACTION_IN_PROGRESS");
                    PERSIST_ACTION_IN_PROGRESS = persistenceObj.getPERSISTENCE_VALUE();
                    if (PERSIST_ACTION_IN_PROGRESS.equals("false")) {

                        context = view.getContext();
                        String DA_ID = rsDU_ID.get(position);
                        mPersistenceObjDao.updateData("PERSIST_DU_ID", DA_ID);
                        intent = new Intent(context, CameraActivity.class);
                        rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        rotateAnimation.setRepeatCount(1);
                        rotateAnimation.setRepeatMode(Animation.RELATIVE_TO_SELF);
                        rotateAnimation.setDuration(100);
                        holder.btnCamera.startAnimation(rotateAnimation);
                        pos = position;
                        scheduleDismissIntent();
                    }

                }
                holder.btnCamera.setImageAlpha(alpha1);
                fireClick = true;
            });
        }
        holder.btnMedia.setOnClickListener(view -> {
            if (fireClick == true) {
                persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_ACTION_IN_PROGRESS");
                PERSIST_ACTION_IN_PROGRESS = persistenceObj.getPERSISTENCE_VALUE();
                if (PERSIST_ACTION_IN_PROGRESS.equals("false")) {

                    context = view.getContext();
                    mPersistenceObjDao.updateData("PERSIST_MULTI_MEDIA_CALLER", "LIST_DEVICE_USER");
                    //   intent = new Intent(context, CameraActivity.class);
                    // intent = new Intent(context, OldMultiMediaDeviceMenu.class);
                    DA_ID = rsDU_ID.get(position);
                    mPersistenceObjDao.updateData("PERSIST_DU_ID", DA_ID);
                    mPersistenceObjDao.updateData("PERSIST_MULTI_MEDIA_CALLER", "LIST_DEVICE_USER");
                    //   DA_ID = holder.tvDV_ID.getText().toString();
                    //   mPersistenceObjDao.updateData("PERSIST_IV_ID", DA_ID);
                    mPersistenceObjDao.updateData("PERSIST_TEMP_01", holder.tvDU_FNAME.getText().toString());
                    mPersistenceObjDao.updateData("PERSIST_TEMP_02", holder.tvDU_PTYPE.getText().toString());
                    mPersistenceObjDao.updateData("PERSIST_TEMP_03", "");
                    mPersistenceObjDao.updateData("PERSIST_TEMP_04", "");
                    mPersistenceObjDao.updateData("PERSIST_TEMP_05", "");
                    intent = new Intent(context, MultiMediaMenu.class);

                    rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setRepeatCount(1);
                    rotateAnimation.setRepeatMode(Animation.RELATIVE_TO_SELF);
                    rotateAnimation.setDuration(100);
                    holder.btnMedia.startAnimation(rotateAnimation);
                    pos = position;
                    scheduleDismissIntent();
                }
                // context.startActivity(intent);
            }
            holder.btnMedia.setImageAlpha(alpha1);
            fireClick = true;

        });
        holder.btnMedia.setOnLongClickListener(view -> {
            persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_ACTION_IN_PROGRESS");
            PERSIST_ACTION_IN_PROGRESS = persistenceObj.getPERSISTENCE_VALUE();
            if (PERSIST_ACTION_IN_PROGRESS.equals("false")) {

                context = view.getContext();
                Resources res = context.getResources();
                holder.btnMedia.setImageAlpha(alpha2);
                message = res.getString(R.string.multi_media_menu_helpa);
                makeToast();

                fireClick = false;
            }
            return false;

        });
        rowView.setOnClickListener(view -> {
            persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_ACTION_IN_PROGRESS");
            PERSIST_ACTION_IN_PROGRESS = persistenceObj.getPERSISTENCE_VALUE();
            if (PERSIST_ACTION_IN_PROGRESS.equals("false")) {

                // TODO Auto-generated method stub
                context = view.getContext();
                DA_ID = rsDU_ID.get(position);

                if (rsMode.equals("SELECT")) {
                    mPersistenceObjDao.updateData("PERSIST_DU_ID", DA_ID);
                }
                persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_AID_ID");
                DA_ID_STR = persistenceObj.getPERSISTENCE_VALUE();
                if (isNumber(DA_ID_STR)) {
                    XX_AID = Integer.parseInt(DA_ID_STR);
                } else {
                    XX_AID = 0;
                }

                if (rsMode.equals("SELECT_PROFILE")) {
                    if (isNumber(DA_ID)) {
                        DU_ID1 = Integer.parseInt(DA_ID);
                    } else {
                        DU_ID1 = 0;
                    }

                    // Test InvolvedParty Object
                    deviceUser = mDeviceUserDao.getDeviceUser(DU_ID1);
                    String DU_FNAME = deviceUser.getDU_FNAME();
                    //  String DU_MI = deviceUser.getDU_MI();
                    //   String DU_LNAME = deviceUser.getDU_LNAME();

                    String DU_MI = "";
                    String DU_LNAME = "";
                    String DU_LICENSE = deviceUser.getDU_LICENSE();
                    String DU_LST = deviceUser.getDU_LST();
                    String DU_ADDR1 = deviceUser.getDU_ADDR1();
                    String DU_ADDR2 = deviceUser.getDU_ADDR2();
                    String DU_CITY = deviceUser.getDU_CITY();
                    String DU_ST = deviceUser.getDU_ST();
                    String DU_ZIP = deviceUser.getDU_ZIP();
                    String DU_EMAIL = deviceUser.getDU_EMAIL();
                    String DU_PHON1 = deviceUser.getDU_PHON1();
                    String DU_PHON2 = deviceUser.getDU_PHON2();
                    //   persistenceObj1 = mPersistenceObjDao.getPersistence("PERSIST_PT_TYPE");
                    //  String DU_PTYPE = persistenceObj1.getPERSISTENCE_VALUE();
                    String DU_PTYPE = deviceUser.getDU_PTYPE();

                    String DU_PHON3 = deviceUser.getDU_PHON3();
                    String DU_CNAM01 = deviceUser.getDU_CNAM01();
                    String DU_PNUM01 = deviceUser.getDU_PNUM01();
                    String DU_CNAM02 = deviceUser.getDU_CNAM02();
                    String DU_PNUM02 = deviceUser.getDU_PNUM02();
                    String DU_CNAM03 = deviceUser.getDU_CNAM03();
                    ;
                    String DU_PNUM03 = deviceUser.getDU_PNUM03();
                    String DU_COMP = deviceUser.getDU_COMP();

                    String DU_LICENSE_COUNTRY = deviceUser.getDU_LICENSE_COUNTRY();
                    String DU_RESIDENT_COUNTRY = deviceUser.getDU_RESIDENT_COUNTRY();
                    String DU_PHON1_COUNTRY = deviceUser.getDU_PHON1_COUNTRY();
                    String DU_PHON2_COUNTRY = deviceUser.getDU_PHON2_COUNTRY();
                    String DU_PHON3_COUNTRY = deviceUser.getDU_PHON3_COUNTRY();
                    mInvolvedPartyDao = new InvolvedPartyDao(context);
                    involvedParty = mInvolvedPartyDao.getInvolvedPartyFname(DU_FNAME, XX_AID);
                    int contents = involvedParty.getIP_AID();
                    if (contents == 0) {
                        AddData(XX_AID, DU_FNAME, DU_MI, DU_LNAME, DU_LICENSE, DU_LST,
                                DU_ADDR1, DU_ADDR2, DU_CITY, DU_ST, DU_ZIP, DU_EMAIL, DU_PHON1, DU_PHON2,
                                DU_PHON3, DU_PTYPE, DU_CNAM01, DU_PNUM01, DU_CNAM02, DU_PNUM02, DU_CNAM03,
                                DU_PNUM03, DU_COMP, DU_LICENSE_COUNTRY, DU_RESIDENT_COUNTRY, DU_PHON1_COUNTRY, DU_PHON2_COUNTRY, DU_PHON3_COUNTRY);
                    } else {
                        Resources res = context.getResources();
                        message = res.getString(R.string.tv2000);
                        makeToast();
                    }

                }
                if (rsMode.equals("UPDATE")) {
                    mPersistenceObjDao.updateData("PERSIST_SELECTED_DU_ID", DA_ID);
                }
                rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setRepeatCount(1);
                rotateAnimation.setRepeatMode(Animation.RELATIVE_TO_SELF);
                rotateAnimation.setDuration(100);
                rowView.startAnimation(rotateAnimation);
                pos = position;
                scheduleDoListItem();
            }
        });
        return rowView;
    }

    private void scheduleDoListItem() {
        Handler handler = new Handler();
        handler.postDelayed(this::doListItem, 200);
    }

    private void doListItem() {
        doClose();
        if (rsMode.equals("SELECT")) {
            intent = new Intent(context, ListAccident.class);
            context.startActivity(intent);
        }
        if (rsMode.equals("SELECT_PROFILE")) {
            intent = new Intent(context, ListInvolvedParty.class);
            context.startActivity(intent);
        }
        if (rsMode.equals("UPDATE")) {
            intent = new Intent(context, UpdateDeviceUser.class);
            context.startActivity(intent);
        }

    }

    private void AddData(Integer XX_AID, String DU_FNAME, String DU_MI, String DU_LNAME, String DU_LICENSE, String DU_LST, String DU_ADDR1,
                         String DU_ADDR2, String DU_CITY, String DU_ST, String DU_ZIP, String DU_EMAIL,
                         String DU_PHON1, String DU_PHON2, String DU_PHON3, String DU_PTYPE,
                         String DU_CNAM01, String DU_PNUM01, String DU_CNAM02, String DU_PNUM02,
                         String DU_CNAM03, String DU_PNUM03, String DU_COMP, String DU_LICENSE_COUNTRY, String DU_RESIDENT_COUNTRY, String DU_PHON1_COUNTRY, String DU_PHON2_COUNTRY, String DU_PHON3_COUNTRY) {

        long insertData = mInvolvedPartyDao.addData(XX_AID, DU_FNAME, DU_MI, DU_LNAME, DU_LICENSE, DU_LST,
                DU_ADDR1, DU_ADDR2, DU_CITY, DU_ST, DU_ZIP, DU_EMAIL, DU_PHON1, DU_PHON2,
                DU_PHON3, DU_PTYPE, DU_CNAM01, DU_PNUM01, DU_CNAM02, DU_PNUM02, DU_CNAM03,
                DU_PNUM03, DU_COMP, DU_LICENSE_COUNTRY, DU_RESIDENT_COUNTRY, DU_PHON1_COUNTRY, DU_PHON2_COUNTRY, DU_PHON3_COUNTRY);


    }

    private void makeToast() {
        int duration = 20;
        int type = 0;
        MDToast mdToast = MDToast.makeText(context, message, duration, type);
        mdToast.setGravity(Gravity.TOP, 50, 200);

        mdToast.show();

    }

    public class Holder {
        FloatingActionButton btnGallery;
        FloatingActionButton btnCamera;
        FloatingActionButton btnMedia;
        TextView tvDU_PTYPE;
        TextView tvDU_ID;
        TextView tvDU_FNAME;
        TextView tv03;
        TextView tv04;
        RelativeLayout ll01;

    }

    public void doClose() {
        //  mInsurancePolicyVDao.closeAll();
        mDeviceImageStoreDao.closeAll();
        // mPartyTypeDao.closeAll();
        // mInvolvedImageStoreDao.closeAll();
        // mVehicleTypeDao.closeAll();
        // mAccidentNoteDao.closeAll();
        mInvolvedPartyDao.closeAll();
        if (!DA_CALLER.equals("ACCIDENT_MENU") && !DA_CALLER.equals("ACCIDENT_MENU_NAVIGATION_DRAWER")) {
            mDeviceUserDao.closeAll();
        }
        // mInsurancePolicyPDao.closeAll();
        // mInvolvedVehicleDao.closeAll();
        // mAccidentIdDao.closeAll();
        mPersistenceObjDao.closeAll();
        // mPremiumAdvertiserDao.closeAll();
        // mInsurancePolicyDao.closeAll();
        // mDeviceVehicleDao.closeAll();
        // mVehicleManifestDao.closeAll();

    }

    private void scheduleDismissIntent() {
        Handler handler = new Handler();
        handler.postDelayed(this::dismissIntent, 200);
    }

    private void dismissIntent() {
        doClose();
        context.startActivity(intent);
    }
}
