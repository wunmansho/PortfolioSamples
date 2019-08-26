package com.wunmansho.portfoliosamples;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ListView;

import com.auto.accident.report.R;
import com.auto.accident.report.models.DeviceUserDao;
import com.auto.accident.report.models.PersistenceObjDao;
import com.auto.accident.report.objects.DeviceUser;
import com.auto.accident.report.objects.PersistenceObj;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetSequence;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

import static com.auto.accident.report.util.utils.isNumber;

/**
 * Created by myron on 1/18/2018.
 */

public class ListDeviceUser extends AppCompatActivity {
    private static final String TAG = "ListDeviceUser";
    // DeviceUserDao mDeviceUserDao;
    private PersistenceObjDao mPersistenceObjDao;
    private ListView mListView;
    private FloatingActionButton btnAdd;
    private FloatingActionButton btnHelp;
    private FloatingActionButton btnLightning;
    private Boolean fireClick = true;
    private String message;
    private int duration;
    private int type;
    private int pos;
    private final int alpha1 = 255;
    private final int alpha2 = 50;
    private String[] splitString;
    private int splitLength;
    private List<DeviceUser> deviceuserList;
    private FloatingActionButton btnMedia;
   // private FloatingActionButton btnCamera;
   // private FloatingActionButton btnGallery;
    private Toolbar toolbar;
    private Resources res;
    private String did_play_ListDeviceUser;
    private PersistenceObj persistenceObj;
    private String rsMode;
    private DeviceUser deviceUser;
    private  DeviceUserDao mDeviceUserDao;
    private RotateAnimation rotateAnimation;
    private boolean loaded;
    private Intent intent;
    private View view;
    private Context context;
    private int helpSequence;
    private String DA_ID_STR;
    private int DUX_ID;
    private long insertData;
    private boolean ActionInProgress;
    private String PERSIST_ACTION_IN_PROGRESS;
    private String timeStamp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_device_user);
        btnAdd = findViewById(R.id.btnAdd);
        btnHelp = findViewById(R.id.btnHelp);
        btnLightning = findViewById(R.id.btnLightning);

        toolbar = findViewById(R.id.my_toolbar);


        setSupportActionBar(toolbar);
        mListView = findViewById(R.id.listView);
        mDeviceUserDao = new DeviceUserDao(this);
        deviceuserList = new ArrayList<>();
        deviceuserList = mDeviceUserDao.getAllDeviceUsers();


        mPersistenceObjDao = new PersistenceObjDao(this);
        mPersistenceObjDao.updateData("DP_CATEGORY", "DEVICE_USER");
        timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
        ActionBar actionBar = getSupportActionBar();
        persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_DU_CALLER");
        rsMode = persistenceObj.getPERSISTENCE_VALUE();
        res = getResources();
        helpSequence = 0;
        mPersistenceObjDao.updateData("PERSIST_ACTION_IN_PROGRESS", "false");
        ActionInProgress = false;

        switch (rsMode) {
            case "ACCIDENT_MENU": {
                toolbar.setSubtitle(getString(R.string.welcome) + " - " + getString(R.string.sdu));
                toolbar.setTitle(getString(R.string.app_name));

                break;
            }
            case "ACCIDENT_MENU_NAVIGATION_DRAWER": {
                toolbar.setSubtitle(getString(R.string.welcome) + " - " + getString(R.string.sdu));
                toolbar.setTitle(getString(R.string.app_name));
                break;
            }

            case "LIST_INVOLVED_PARTY": {
                persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_AID_ID");
                String ACC_NUM = persistenceObj.getPERSISTENCE_VALUE();

                toolbar.setTitle(getString(R.string.app_name) + " # " + ACC_NUM);
                persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_DU_ID");
                DA_ID_STR = persistenceObj.getPERSISTENCE_VALUE();
                if (isNumber(DA_ID_STR)) {
                    DUX_ID = Integer.parseInt(DA_ID_STR);
                } else {
                    DUX_ID = 0;
                }
                deviceUser = mDeviceUserDao.getDeviceUser(DUX_ID);
                String DU_FNAME = deviceUser.getDU_FNAME();
                String[] splitString;
                int splitLength;
                String DA_RESULT2;
                splitString = DU_FNAME.split(" ");
                DA_RESULT2 = splitString[0];

                toolbar.setSubtitle(getString(R.string.welcome) + " " + DA_RESULT2 + " - " + getString(R.string.sup) + " " + getString(R.string.tocopy));
                break;

            }

            default: {
                toolbar.setSubtitle(getString(R.string.welcome) + " - " + getString(R.string.sdu));
                toolbar.setTitle(getString(R.string.app_name));

                break;
            }

        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

        }

        toolbar.setNavigationOnClickListener(view -> {
            context = view.getContext();


            if (rsMode.equals("ACCIDENT_MENU")) {
                 intent = new Intent(this, AccidentMenu.class);

            }
            if (rsMode.equals("ACCIDENT_MENU_NAVIGATION_DRAWER")) {
                 intent = new Intent(this, AccidentMenu.class);

            }

            if (rsMode.equals("LIST_INVOLVED_PARTY")) {
                 intent = new Intent(this, ListInvolvedParty.class);

            }
            rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(100);
            rotateAnimation.setRepeatCount(1);
            rotateAnimation.setRepeatMode(Animation.RELATIVE_TO_SELF);
            View btnBack = toolbar.getChildAt(2);
            btnBack.startAnimation(rotateAnimation);
            scheduleDismissToolbar();

           

        });

        //  Intent receivedIntent = getIntent();
        btnHelp.setOnClickListener(view -> {
            if (ActionInProgress == false) {

                mPersistenceObjDao.updateData("PERSIST_ACTION_IN_PROGRESS", "true");
                ActionInProgress = true;
                helpSequence++;
                rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setRepeatCount(1);
                rotateAnimation.setRepeatMode(Animation.RELATIVE_TO_SELF);
                rotateAnimation.setDuration(100);
                btnHelp.startAnimation(rotateAnimation);
                scheduleDoHelp();
            }
            });
        if (!deviceuserList.isEmpty()) {
            mPersistenceObjDao.updateData("did_play_ListDeviceUser", "true");
        }
        if (deviceuserList.isEmpty()) {
            persistenceObj = mPersistenceObjDao.getPersistence("did_play_ListDeviceUser");
            did_play_ListDeviceUser = persistenceObj.getPERSISTENCE_VALUE();
            if (!did_play_ListDeviceUser.equals("true")) {
     //        btnHelp.callOnClick();
                mPersistenceObjDao.updateData("did_play_ListDeviceUser", "true");
            }

        }


        btnAdd.setOnClickListener(view -> {
            if (fireClick == true) {
                context = view.getContext();
                intent = new Intent(context, AddDeviceUser.class);
                rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setRepeatCount(1);
                rotateAnimation.setRepeatMode(Animation.RELATIVE_TO_SELF);
                rotateAnimation.setDuration(100);
                btnAdd.startAnimation(rotateAnimation);
                scheduleDismissIntent();

            }
            fireClick = true;
            btnAdd.setImageAlpha(alpha1);
        });


        btnAdd.setOnLongClickListener(view -> {

            btnAdd.setImageAlpha(alpha2);
            context = view.getContext();


            message = res.getString(R.string.tv0149);
            duration = 20;
            type = 0;
            MDToast mdToast = MDToast.makeText(context, message, duration, type);
            mdToast.setGravity(Gravity.TOP, 50, 200);

            mdToast.show();

            fireClick = false;

            return false;

        });
        btnLightning.setOnClickListener(view -> {
            if (fireClick == true) {
                rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setRepeatCount(1);
                rotateAnimation.setRepeatMode(Animation.RELATIVE_TO_SELF);
                rotateAnimation.setDuration(100);
                btnLightning.startAnimation(rotateAnimation);
                scheduleDoLightning();
            }
            fireClick = true;
            btnLightning.setImageAlpha(alpha1);
        });


        btnLightning.setOnLongClickListener(view -> {

            btnLightning.setImageAlpha(alpha2);
            context = view.getContext();


            message = res.getString(R.string.tv0149);
            duration = 20;
            type = 0;
            MDToast mdToast = MDToast.makeText(context, message, duration, type);
            mdToast.setGravity(Gravity.TOP, 50, 200);

            mdToast.show();

            fireClick = false;

            return false;

        });
        showListView();

    }


    private void showListView() {

        mListView = findViewById(R.id.listView);
        mListView.setAdapter(new ListDeviceUserAdapter(this));

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!loaded) {
            //First time just set the loaded flag true
            loaded = true;
        } else {
            showListView();
        }
    }
    public void disableButtons() {
           btnAdd.setEnabled(false);
              btnHelp.setEnabled(false);
        btnLightning.setEnabled(false);


    }
    public void enableButtons() {
            btnAdd.setEnabled(true);

        btnHelp.setEnabled(true);
        btnLightning.setEnabled(true);

    }


    public void showSequence1(View view) {
        final Toolbar tb = this.findViewById(R.id.my_toolbar);

        //int toolBarColorValue = Color.parseColor("#FF0288D1");
        //
        disableButtons();
        new MaterialTapTargetSequence()
                .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                        .setTarget(tb.getChildAt(2))

                        .setPrimaryText(res.getString(R.string.shield_icon2))
                        .setSecondaryText(res.getString(R.string.got_it))
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                        )
                .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                        .setTarget(btnAdd)
                        .setPrimaryText(res.getString(R.string.plus_icon_profile_first_time))
                        .setSecondaryText(res.getString(R.string.got_it))
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                        )
                .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                        .setTarget(btnLightning)
                        .setPrimaryText(res.getString(R.string.fast_add_profile_first_time))
                        .setSecondaryText(res.getString(R.string.got_it))
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                        )
                .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                        .setTarget(btnHelp)
                        .setPrimaryText(res.getString(R.string.btn_help) + TAG)
                        .setSecondaryText(res.getString(R.string.got_it))
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                        .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                        {
                            @Override
                            public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                            {
                                if (state == MaterialTapTargetPrompt.STATE_DISMISSED || state == MaterialTapTargetPrompt.STATE_FINISHING || state == MaterialTapTargetPrompt.STATE_FINISHED || state == MaterialTapTargetPrompt.STATE_DISMISSING)
                                {
                                    enableButtons();
                                    ActionInProgress = false;
                                    mPersistenceObjDao.updateData("PERSIST_ACTION_IN_PROGRESS", "false");

                                }
                            }
                        }))


                .show();
    }

    public void showSequence2(View view) {
        final Toolbar tb = this.findViewById(R.id.my_toolbar);
        disableButtons();
        //int toolBarColorValue = Color.parseColor("#FF0288D1");
      //  btnCamera = mListView.getChildAt(0).findViewById(R.id.btnCamera);
       // btnGallery = mListView.getChildAt(0).findViewById(R.id.btnGallery);
        btnMedia = mListView.getChildAt(0).findViewById(R.id.btnMedia);
        switch (rsMode) {
            case "ACCIDENT_MENU_NAVIGATION_DRAWER": {
                new MaterialTapTargetSequence()

                        .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                                .setTarget(tb.getChildAt(2))

                                .setPrimaryText(res.getString(R.string.shield_icon2))
                                .setSecondaryText(res.getString(R.string.got_it))
                                .setAnimationInterpolator(new LinearOutSlowInInterpolator()))


                        .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                                .setTarget(btnAdd)
                                .setPrimaryText(res.getString(R.string.plus_icon_profile))
                                .setSecondaryText(res.getString(R.string.got_it))
                                .setAnimationInterpolator(new LinearOutSlowInInterpolator()))

                        .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                                .setTarget(btnLightning)
                                .setPrimaryText(res.getString(R.string.fast_add_profile))
                                .setSecondaryText(res.getString(R.string.got_it))
                                .setAnimationInterpolator(new LinearOutSlowInInterpolator()))


                        .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                                .setTarget(mListView.getChildAt(0))

                                .setPrimaryText(res.getString(R.string.to_edit_device_user_profile))
                                .setSecondaryText(res.getString(R.string.got_it))
                                .setPromptFocal(new RectanglePromptFocal())
                        )
                        .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                                .setTarget(btnMedia)

                                .setPrimaryText(res.getString(R.string.multi_media_menu_helpa))
                                .setSecondaryText(res.getString(R.string.got_it))
                                .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                        )

                        .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                                .setTarget(btnHelp)
                                .setPrimaryText(res.getString(R.string.btn_help) + TAG)
                                .setSecondaryText(res.getString(R.string.got_it))
                                .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                                {
                                    @Override
                                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                                    {
                                        if (state == MaterialTapTargetPrompt.STATE_DISMISSED || state == MaterialTapTargetPrompt.STATE_FINISHING || state == MaterialTapTargetPrompt.STATE_FINISHED || state == MaterialTapTargetPrompt.STATE_DISMISSING)
                                        {
                                            enableButtons();
                                            ActionInProgress = false;
                                            mPersistenceObjDao.updateData("PERSIST_ACTION_IN_PROGRESS", "false");

                                        }
                                    }
                                }))


                        .show();
            }


            case "LIST_INVOLVED_PARTY": {
                new MaterialTapTargetSequence()

                        .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                                .setTarget(tb.getChildAt(2))

                                .setPrimaryText(res.getString(R.string.shield_icon2))
                                .setSecondaryText(res.getString(R.string.got_it))
                                .setAnimationInterpolator(new LinearOutSlowInInterpolator()))


                        .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                                .setTarget(btnAdd)
                                .setPrimaryText(res.getString(R.string.plus_icon_profile))
                                .setSecondaryText(res.getString(R.string.got_it))
                                .setAnimationInterpolator(new LinearOutSlowInInterpolator()))

                        .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                                .setTarget(btnLightning)
                                .setPrimaryText(res.getString(R.string.fast_add_profile))
                                .setSecondaryText(res.getString(R.string.got_it))
                                .setAnimationInterpolator(new LinearOutSlowInInterpolator()))


                        .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                                .setTarget(mListView.getChildAt(0))

                                .setPrimaryText(res.getString(R.string.press_list_to_select_profile))
                                .setSecondaryText(res.getString(R.string.got_it))
                                .setPromptFocal(new RectanglePromptFocal())
                        )
                        .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                                .setTarget(btnMedia)

                                .setPrimaryText(res.getString(R.string.multi_media_menu_helpa))
                                .setSecondaryText(res.getString(R.string.got_it))
                                .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                        )


                        .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                                .setTarget(btnHelp)
                                .setPrimaryText(res.getString(R.string.btn_help) + TAG)
                                .setSecondaryText(res.getString(R.string.got_it))
                                .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                                {
                                    @Override
                                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                                    {
                                        if (state == MaterialTapTargetPrompt.STATE_DISMISSED || state == MaterialTapTargetPrompt.STATE_FINISHING || state == MaterialTapTargetPrompt.STATE_FINISHED || state == MaterialTapTargetPrompt.STATE_DISMISSING)
                                        {
                                            enableButtons();
                                            ActionInProgress = false;
                                            mPersistenceObjDao.updateData("PERSIST_ACTION_IN_PROGRESS", "false");

                                        }
                                    }
                                }))


                        .show();
                break;

            }



        }

    }
    public void showFirstSequence3(View view) {
        final Toolbar tb = this.findViewById(R.id.my_toolbar);
        disableButtons();
        //int toolBarColorValue = Color.parseColor("#FF0288D1");
        btnMedia = mListView.getChildAt(0).findViewById(R.id.btnMedia);

     //   btnCamera = mListView.getChildAt(0).findViewById(R.id.btnCamera);
     //   btnGallery = mListView.getChildAt(0).findViewById(R.id.btnGallery);
        new MaterialTapTargetSequence()


                .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                        .setTarget(mListView.getChildAt(0))

                        .setPrimaryText(res.getString(R.string.to_procede_select_profile))
                        .setSecondaryText(res.getString(R.string.got_it))
                        .setPromptFocal(new RectanglePromptFocal())
                )
                        .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                        .setTarget(btnHelp)
                        .setPrimaryText(res.getString(R.string.btn_first_help) + TAG)
                        .setSecondaryText(res.getString(R.string.got_it))
                                .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                                {
                                    @Override
                                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                                    {
                                        if (state == MaterialTapTargetPrompt.STATE_DISMISSED || state == MaterialTapTargetPrompt.STATE_FINISHING || state == MaterialTapTargetPrompt.STATE_FINISHED || state == MaterialTapTargetPrompt.STATE_DISMISSING)
                                        {
                                            enableButtons();
                                            ActionInProgress = false;
                                            mPersistenceObjDao.updateData("PERSIST_ACTION_IN_PROGRESS", "false");

                                        }
                                    }
                                }))


                .show();
    }
    public void showSecondSequence3(View view) {
        disableButtons();
        final Toolbar tb = this.findViewById(R.id.my_toolbar);
        //int toolBarColorValue = Color.parseColor("#FF0288D1");
        btnMedia = mListView.getChildAt(0).findViewById(R.id.btnMedia);

      //  btnCamera = mListView.getChildAt(0).findViewById(R.id.btnCamera);
       // btnGallery = mListView.getChildAt(0).findViewById(R.id.btnGallery);
        new MaterialTapTargetSequence()

                .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                        .setTarget(tb.getChildAt(2))

                        .setPrimaryText(res.getString(R.string.shield_icon2))
                        .setSecondaryText(res.getString(R.string.got_it))
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                        )


                .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                        .setTarget(btnAdd)
                        .setPrimaryText(res.getString(R.string.plus_icon_profile))
                        .setSecondaryText(res.getString(R.string.got_it))
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                        )

                .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                        .setTarget(btnLightning)
                        .setPrimaryText(res.getString(R.string.fast_add_profile))
                        .setSecondaryText(res.getString(R.string.got_it))
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                        )

                .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                        .setTarget(mListView.getChildAt(0))

                        .setPrimaryText(res.getString(R.string.to_procede_select_profile))
                        .setSecondaryText(res.getString(R.string.got_it))
                        .setPromptFocal(new RectanglePromptFocal())
                        )
                .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                        .setTarget(btnMedia)

                        .setPrimaryText(res.getString(R.string.multi_media_menu_helpa))
                        .setSecondaryText(res.getString(R.string.got_it))
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                )
                .addPrompt(new MaterialTapTargetPrompt.Builder(ListDeviceUser.this)
                        .setTarget(btnHelp)
                        .setPrimaryText(res.getString(R.string.btn_help) + TAG)
                        .setSecondaryText(res.getString(R.string.got_it))
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                        .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                        {
                            @Override
                            public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                            {
                                if (state == MaterialTapTargetPrompt.STATE_DISMISSED || state == MaterialTapTargetPrompt.STATE_FINISHING || state == MaterialTapTargetPrompt.STATE_FINISHED || state == MaterialTapTargetPrompt.STATE_DISMISSING)
                                {
                                    enableButtons();
                                    ActionInProgress = false;
                                    mPersistenceObjDao.updateData("PERSIST_ACTION_IN_PROGRESS", "false");

                                }
                            }
                        }))


                .show();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();



    }
    private void scheduleDismissToolbar() {
        Handler handler = new Handler();
        handler.postDelayed(this::dismissActivity, 200);
    }
    private void dismissActivity() {
        doClose();
        View btnBack = toolbar.getChildAt(2);
        btnBack.clearAnimation();
        startActivity(intent);
    }
    public void doClose() {
        //mInsurancePolicyVDao.closeAll();
        //mDeviceImageStoreDao.closeAll();
        //mPartyTypeDao.closeAll();
        //mInvolvedImageStoreDao.closeAll();
        //mVehicleTypeDao.closeAll();
        //mAccidentNoteDao.closeAll();
        //mInvolvedPartyDao.closeAll();
        //mDeviceUserDao.closeAll();
        //mInsurancePolicyPDao.closeAll();
        //mInvolvedVehicleDao.closeAll();
        //mAccidentIdDao.closeAll();

            mDeviceUserDao.closeAll();

        mPersistenceObjDao.closeAll();
        //mPremiumAdvertiserDao.closeAll();
        //mInsurancePolicyDao.closeAll();
        //mDeviceVehicleDao.closeAll();
        //mVehicleManifestDao.closeAll();

    }
    private void scheduleDismissIntent() {
        Handler handler = new Handler();
        handler.postDelayed(this::dismissIntent, 250);
    }
    private void dismissIntent() {
        doClose();
        this.startActivity(intent);
    }
    private void scheduleDoHelp() {
        Handler handler = new Handler();
        handler.postDelayed(this::doHelp, 250);
    }
    private void doHelp() {

        if (deviceuserList.isEmpty()) {
            showSequence1(view);
        } else {
            if (rsMode.equals("ACCIDENT_MENU")) {
                helpSequence++;
                if (helpSequence < 2) {
                    showFirstSequence3(view);
                } else {
                    showSecondSequence3(view);
                }
            } else {
                showSequence2(view);

            }
        }
    }
    @Override
    public void onBackPressed() {
        if (rsMode.equals("ACCIDENT_MENU")) {
            intent = new Intent(this, AccidentMenu.class);

        }
        if (rsMode.equals("ACCIDENT_MENU_NAVIGATION_DRAWER")) {
            intent = new Intent(this, AccidentMenu.class);

        }

        if (rsMode.equals("LIST_INVOLVED_PARTY")) {
            intent = new Intent(this, ListInvolvedParty.class);

        }
            rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

            rotateAnimation.setDuration(100);
            rotateAnimation.setRepeatCount(1);
            rotateAnimation.setRepeatMode(Animation.RELATIVE_TO_SELF);
            View btnBack = toolbar.getChildAt(2);
            scheduleDismissToolbar();
            btnBack.startAnimation(rotateAnimation);


    }
    private void scheduleDoLightning() {
        Handler handler = new Handler();
        handler.postDelayed(this::doLightning, 200);
    }
    private void doLightning() {
        String DU_PTYPE = "";
        String DU_FNAME = "";
        String DU_MI = "";
        String DU_LNAME = "";
        String DU_LICENSE = "";
        String DU_LST = "";
        String DU_ADDR1 = "";
        String DU_ADDR2 = "";
        String DU_CITY = "";
        String DU_ST = "";
        String DU_ZIP = "";
        String DU_EMAIL ="";
        String DU_PHON1 = "";
        String DU_PHON2 = "";
        String DU_PHON3 = "";
        String DU_CNAM01 = "";
        String DU_PNUM01 = "";
        String DU_CNAM02 = "";
        String DU_PNUM02 = "";
        String DU_CNAM03 = "";
        String DU_PNUM03 = "";
        String DU_COMP = "";

        int  DU_CUID = DUX_ID;

        timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
        String DU_CDATE = timeStamp;

        int    DU_UUID = DUX_ID;

        String DU_UDATE = timeStamp;
        String DU_LICENSE_COUNTRY = "";
        String DU_RESIDENT_COUNTRY = "";
        String DU_PHON1_COUNTRY = "";
        String DU_PHON2_COUNTRY = "";
        String DU_PHON3_COUNTRY = "";
        AddData(DU_FNAME, DU_MI, DU_LNAME, DU_LICENSE, DU_LST,
                DU_ADDR1, DU_ADDR2, DU_CITY, DU_ST, DU_ZIP, DU_EMAIL, DU_PHON1, DU_PHON2,
                DU_PHON3, DU_PTYPE, DU_CNAM01, DU_PNUM01, DU_CNAM02, DU_PNUM02, DU_CNAM03,
                DU_PNUM03, DU_COMP, DU_LICENSE_COUNTRY, DU_RESIDENT_COUNTRY,
                DU_PHON1_COUNTRY, DU_PHON2_COUNTRY, DU_PHON3_COUNTRY);
        int IPID = (int) insertData;
        String DA_ID = Integer.toString(IPID);
        DU_FNAME = res.getString(R.string.fast_add_profile_str) + DA_ID;
        mDeviceUserDao.updateDataName(IPID, DU_FNAME);

        intent = new Intent(this, ListDeviceUser.class);
        startActivity(intent);

    }
    private void AddData(String DU_FNAME, String DU_MI, String DU_LNAME, String DU_LICENSE, String DU_LST, String DU_ADDR1,
                         String DU_ADDR2, String DU_CITY, String DU_ST, String DU_ZIP, String DU_EMAIL,
                         String DU_PHON1, String DU_PHON2, String DU_PHON3, String DU_PTYPE,
                         String DU_CNAM01, String DU_PNUM01, String DU_CNAM02, String DU_PNUM02,
                         String DU_CNAM03, String DU_PNUM03, String DU_COMP, String DU_LICENSE_COUNTRY,
                         String DU_RESIDENT_COUNTRY, String DU_PHON1_COUNTRY, String DU_PHON2_COUNTRY,
                         String DU_PHON3_COUNTRY) {
        insertData = mDeviceUserDao.addData(DU_FNAME, DU_MI, DU_LNAME, DU_LICENSE, DU_LST,
                DU_ADDR1, DU_ADDR2, DU_CITY, DU_ST, DU_ZIP, DU_EMAIL, DU_PHON1, DU_PHON2,
                DU_PHON3, DU_PTYPE, DU_CNAM01, DU_PNUM01, DU_CNAM02, DU_PNUM02, DU_CNAM03,
                DU_PNUM03, DU_COMP, DU_LICENSE_COUNTRY, DU_RESIDENT_COUNTRY, DU_PHON1_COUNTRY,
                DU_PHON2_COUNTRY, DU_PHON3_COUNTRY);


    }




}
