package com.wunmansho.portfoliosamples;


import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.auto.accident.report.R;
import com.auto.accident.report.models.AccidentNoteDao;
import com.auto.accident.report.models.InsurancePolicyDao;
import com.auto.accident.report.models.InsurancePolicyPDao;
import com.auto.accident.report.models.InsurancePolicyVDao;
import com.auto.accident.report.models.InvolvedImageStoreDao;
import com.auto.accident.report.models.InvolvedPartyDao;
import com.auto.accident.report.models.InvolvedVehicleDao;
import com.auto.accident.report.models.PersistenceObjDao;
import com.auto.accident.report.models.VehicleManifestDao;
import com.auto.accident.report.objects.AccidentNote;
import com.auto.accident.report.objects.InsurancePolicy;
import com.auto.accident.report.objects.InsurancePolicyP;
import com.auto.accident.report.objects.InsurancePolicyV;
import com.auto.accident.report.objects.InvolvedImageStore;
import com.auto.accident.report.objects.InvolvedParty;
import com.auto.accident.report.objects.InvolvedVehicle;
import com.auto.accident.report.objects.PersistenceObj;
import com.auto.accident.report.objects.VehicleManifest;
import com.auto.accident.report.util.KeyboardUtils;
import com.auto.accident.report.util.utils;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDDocumentCatalog;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDAcroForm;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDTextField;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetSequence;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

import static com.auto.accident.report.util.AdvancedSend.advancedSend;
import static com.auto.accident.report.util.utils.isNumber;
import static com.auto.accident.report.util.utils.splitFileExt;

//import com.auto.accident.report.about.AboutActivity;
//import com.auto.accident.report.firebase.FirebaseCloudUpload;

public class PdfPrint extends AppCompatActivity {
    // --Commented out by Inspection (5/14/2018 2:27 PM):private int pos;
    // todo is PVMIV_ID an issue with printing  vehicles with no passengers
    private static final String TAG = "PdfPrint";
    private final int alpha1 = 255;
    private final int alpha2 = 50;
    // --Commented out by Inspection (5/14/2018 2:27 PM):private String deviceLocale = Locale.getDefault().getCountry();
    private final String deviceLocalel = Locale.getDefault().getLanguage();
    private PersistenceObjDao mPersistenceObjDao;
    private InvolvedPartyDao mInvolvedPartyDao;
    private InvolvedVehicleDao mInvolvedVehicleDao;
    private AccidentNoteDao mAccidentNoteDao;
    private AssetManager assetManager;
    private String AID;
    private PDDocument document;
    private String albumName;
    private String DA_DOC;
    private Boolean fireClick = true;
    private String message;
    private int duration;
    private int type;
    private KProgressHUD hud;
    private String PageS;
    private int Page;
    private int VMIP_ID;
    private int VMIV_ID;
    private int PVMIV_ID;
    private List<AccidentNote> accidentnoteList = new ArrayList<>();
    private boolean printVehicleNote = false;
    private String ImagePath;
    private View view;
    //   private FloatingActionButton iv_image;

    private int AID_ID;
    private List<VehicleManifest> vehicleManifestList = new ArrayList<>();

    private String IP_TEMPLATE = "";
    private PDAcroForm acroForm2;
    private String EMAIL;
    private FloatingActionButton btnView;
   // private FloatingActionButton btnCloud;  Enterprise
    private FloatingActionButton btnShare;
    private FloatingActionButton btnCreate;
    private FloatingActionButton btnHelp;
    private ProgressBar progressBar;
    private String FIREBASE_CLOUD_UPLOAD_IN_PROGRESS;
    private RotateAnimation rotateAnimation;
    private Toolbar toolbar;
    private InsurancePolicyDao mInsurancePolicyDao;
    private InsurancePolicyPDao mInsurancePolicyPDao;
    private InsurancePolicyVDao mInsurancePolicyVDao;
    private PersistenceObj persistenceObj;
    private VehicleManifestDao mVehicleManifestDao;
    private InvolvedImageStoreDao mInvolvedImageStoreDao;
    private InvolvedVehicle involvedVehicle;
    private InvolvedParty involvedParty;
    private boolean hasPassengers;
    private Intent intent;
    private Context context;
    private Resources res;
    private int totalPages;
    private boolean isCreating;
    private String DA_ID_STR;
    private String Report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pdf_print);
        progressBar = findViewById(R.id.progressBar);
        btnHelp = findViewById(R.id.btnHelp);
        btnView = findViewById(R.id.btnView);
        btnCreate = findViewById(R.id.btnCreate);
        btnShare = findViewById(R.id.btnShare);
      //  btnCloud = findViewById(R.id.btnCloud); Enterprise
        toolbar = findViewById(R.id.my_toolbar);
        isCreating = false;
        mInvolvedImageStoreDao = new InvolvedImageStoreDao(this);
        mInvolvedVehicleDao = new InvolvedVehicleDao(this);
        mInvolvedPartyDao = new InvolvedPartyDao(this);
        mVehicleManifestDao = new VehicleManifestDao(this);
        mInsurancePolicyVDao = new InsurancePolicyVDao(this);
        mInsurancePolicyPDao = new InsurancePolicyPDao(this);
        mInsurancePolicyDao = new InsurancePolicyDao(this);
        mPersistenceObjDao = new PersistenceObjDao(this);
        mAccidentNoteDao = new AccidentNoteDao(this);
        persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_AID_ID");

        AID = persistenceObj.getPERSISTENCE_VALUE();
        String PERSISTANCE_KEY = "PERSIST_PDF_PAGE_COUNT" + AID;
        persistenceObj = mPersistenceObjDao.getPersistence(PERSISTANCE_KEY);
        String totalPagesS = persistenceObj.getPERSISTENCE_VALUE();
        if (isNumber(totalPagesS)) {
            totalPages = Integer.parseInt(totalPagesS);
        } else {
            totalPages = 0;
        }


        res = getResources();
        context = this;
        PVMIV_ID = 0;
        if (totalPages == 0) {
            btnView.setVisibility(View.INVISIBLE);
            btnShare.setVisibility(View.INVISIBLE);
        //    btnCloud.setVisibility(View.INVISIBLE);  Enterprise
        } else {
            btnView.setVisibility(View.VISIBLE);
            btnShare.setVisibility(View.VISIBLE);
       //     btnCloud.setVisibility(View.VISIBLE);   Enterprise
        }

        toolbar.setNavigationOnClickListener(view -> {
            mPersistenceObjDao.updateData("FIREBASE_CLOUD_UPLOAD_IN_PROGRESS", "false");
            rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(100);
            rotateAnimation.setRepeatCount(1);
            rotateAnimation.setRepeatMode(Animation.RELATIVE_TO_SELF);
            View btnBack = toolbar.getChildAt(2);
            scheduleDismissToolbar();
            btnBack.startAnimation(rotateAnimation);
         
        });

        btnView.setOnClickListener(view -> {
            if (fireClick) {
                rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setRepeatCount(1);
                rotateAnimation.setRepeatMode(Animation.RELATIVE_TO_SELF);
                rotateAnimation.setDuration(100);
                btnView.startAnimation(rotateAnimation);

                scheduleviewPdf();

            }
            fireClick = true;
            btnView.setImageAlpha(alpha1);

        });

        btnView.setOnLongClickListener(view -> {
            btnView.setImageAlpha(alpha2);
            
            
            message = res.getString(R.string.tv0166);
            duration = 20;
            type = 0;
            MDToast mdToast = MDToast.makeText(context, message, duration, type);
            mdToast.setGravity(Gravity.TOP, 50, 200);
            mdToast.show();
            fireClick = false;
            return false;
        });

        btnCreate.setOnClickListener(view -> {
            if (fireClick) {
                if (!isCreating) {
                    rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setRepeatCount(1);
                    rotateAnimation.setRepeatMode(Animation.RELATIVE_TO_SELF);
                    rotateAnimation.setDuration(100);
                    btnCreate.startAnimation(rotateAnimation);
                    //    printAccident();
                    schedulePdfCreate();
                 isCreating = true;
                }
                //	printAccident();
            }
            fireClick = true;
            btnCreate.setImageAlpha(alpha1);
        });

        btnCreate.setOnLongClickListener(view -> {
            btnCreate.setImageAlpha(alpha2);
            
            
            message = res.getString(R.string.tv0167);
            duration = 20;
            type = 0;
            MDToast mdToast = MDToast.makeText(context, message, duration, type);
            mdToast.setGravity(Gravity.TOP, 50, 200);
            mdToast.show();
            fireClick = false;
            return false;
        });
        btnShare.setOnClickListener(view -> {
            if (fireClick) {

                rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setRepeatCount(Animation.INFINITE);
                rotateAnimation.setRepeatMode(Animation.RESTART);
                rotateAnimation.setDuration(1000);
                btnShare =  findViewById(R.id.btnShare);
                btnShare.startAnimation(rotateAnimation);
               // intent = new Intent(this, AdvancedSend.class);
              //  this.startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                boolean sendresult = advancedSend(this, res);
                if (sendresult == true) {
                    scheduleDismissShareAnimation();
                }

            }
            fireClick = true;
            btnShare.setImageAlpha(alpha1);
        });

        btnShare.setOnLongClickListener(view -> {
            btnShare.setImageAlpha(alpha2);
            
            
            message = res.getString(R.string.share_report);
            duration = 20;
            type = 0;
            MDToast mdToast = MDToast.makeText(context, message, duration, type);
            mdToast.setGravity(Gravity.TOP, 50, 200);
            mdToast.show();
            fireClick = false;
            return false;
        });
 
        btnHelp.setOnClickListener(view -> {
            rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setRepeatCount(1);
            rotateAnimation.setRepeatMode(Animation.RELATIVE_TO_SELF);
            rotateAnimation.setDuration(100);
            btnHelp.startAnimation(rotateAnimation);
            scheduleDoHelp();
        });
    }

    private void scheduleDismiss() {
        Handler handler = new Handler();
        //hud.dismiss();
        handler.postDelayed(this::printAccident, 100);
    }

    private void scheduleDismissCloudAnimation() {
        Handler handler = new Handler();
        //hud.dismiss();
        handler.postDelayed(this::animateCloud, 15000);

    }
    private void animateCloud() {

        persistenceObj = mPersistenceObjDao.getPersistence("FIREBASE_CLOUD_UPLOAD_IN_PROGRESS");
        FIREBASE_CLOUD_UPLOAD_IN_PROGRESS  = persistenceObj.getPERSISTENCE_VALUE();
        if(!FIREBASE_CLOUD_UPLOAD_IN_PROGRESS.equals("false")) {
            scheduleDismissCloudAnimation();
        } else {
     //       btnCloud.clearAnimation();  Enterprise
        }
    }
    private void scheduleDismissShareAnimation() {
        Handler handler = new Handler();
        //hud.dismiss();
        handler.postDelayed(this::animateShare, 15000);

    }
    private void animateShare() {
            btnShare.clearAnimation();
     }

    private void scheduleDismissToolbar() {

        Handler handler = new Handler();
        //hud.dismiss();
        handler.postDelayed(this::dismissActivity, 200);

    }
    private void dismissActivity() {
        doClose();
        View btnBack = toolbar.getChildAt(2);
        btnBack.clearAnimation();
        intent = new Intent(this,  ListInvolvedMenu.class);
        startActivity(intent);

    }
    private void printAccident() {
        Page = 0;
        cleanUp();
        Page = 0;
        PDFBoxResourceLoader.init(getApplicationContext());

        //   root = android.os.Environment.getExternalStorageDirectory();
        assetManager = getAssets();
        // Load the document and get the AcroForm

        persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_AID_ID");
        DA_ID_STR = persistenceObj.getPERSISTENCE_VALUE();
        if (isNumber(DA_ID_STR)) {
            AID_ID = Integer.parseInt(DA_ID_STR);
        } else {
            AID_ID = 0;
        }

        persistenceObj = mPersistenceObjDao.getPersistence("PERSIST_IP_CALLER");
        String DA_CALLER = persistenceObj.getPERSISTENCE_VALUE();
        mPersistenceObjDao.updateData("PERSIST_PIC_MODE", "INVOLVED_PARTY");



        PVMIV_ID = 0;

        vehicleManifestList = mVehicleManifestDao.getAllVehicleManifestsBothNZ(AID_ID);
        IP_TEMPLATE = deviceLocalel + "/involvedparty.pdf";
        for (VehicleManifest vehicleManifest : vehicleManifestList) {
            printVehicleNote = false;
            VMIV_ID = vehicleManifest.getVMIV_ID();
            VMIP_ID = vehicleManifest.getVMIP_ID();
            if (VMIV_ID != PVMIV_ID && VMIV_ID != 0) {
                PVMIV_ID = VMIV_ID;
                hasPassengers = true;

                createVehiclePdf();
                createManifest();

                printVehicle();


                createPhotoPdf();


                if (printVehicleNote) {
                    createVehicleNotePdf();
                }
            }

              
                involvedParty = mInvolvedPartyDao.getInvolvedParty(VMIP_ID, AID_ID);

                createInvolvedPartyPdf();
                createPolicyReportPdf();


        }
        List<InvolvedVehicle> involvedVehicleList = mInvolvedVehicleDao.getAllInvolvedVehicles(AID_ID);
        for (InvolvedVehicle involvedVehicle : involvedVehicleList) {
            VMIV_ID = involvedVehicle.getIV_ID();
            vehicleManifestList = mVehicleManifestDao.getAllVehicleManifestsPersonZ(AID_ID);
            for (VehicleManifest vehicleManifest : vehicleManifestList) {
                hasPassengers = false;
                createVehiclePdf();
              //  createManifest();

                printVehicle();


                createPhotoPdf();


                if (printVehicleNote) {
                    createVehicleNotePdf();
                }

            }
        }

        // getAllInvolvedParties
        IP_TEMPLATE = deviceLocalel + "/involvedpartynv.pdf";

        List<InvolvedParty> involvedpartyList = mInvolvedPartyDao.getAllInvolvedParties(AID_ID);
        for (InvolvedParty involvedParty2 : involvedpartyList) {
            VMIP_ID = involvedParty2.getIP_ID();
            vehicleManifestList = mVehicleManifestDao.getVehicleManifestPersonNZ(VMIP_ID, AID_ID);
            if (vehicleManifestList.size() == 0) {
                involvedParty = mInvolvedPartyDao.getInvolvedParty(VMIP_ID, AID_ID);
                createInvolvedPartyPdf();
                createPolicyReportPdf();
            }
        }

        createUnattachedNotePdf();
        createUnattachedPhotoPdf();

     
        hud.dismiss();
        mPersistenceObjDao.updateData("PERSIST_PDF_PAGE_COUNT" + AID, PageS);

        viewPdf();

    }


    private void cleanUp() {
        boolean deleted = true;
        String PageSA = "0";
        for (int PageNum = 1; deleted; PageNum++) {

            if (PageNum < 10) {
                PageSA = "00" + Integer.toString(PageNum);
            }
            if (PageNum > 9 && PageNum < 100) {
                PageSA = "0" + Integer.toString(PageNum);
            }
            if (PageNum > 99) {
                PageSA = Integer.toString(PageNum);
            }
            albumName = "AccidentReport";

            albumName = "AccidentReport";
            File mStorageDirectory = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), albumName);
            if (!mStorageDirectory.exists()) {
                mStorageDirectory.mkdirs();
            }

            Report = "/AccidentReport000" + AID + PageS + ".pdf";
            String path = mStorageDirectory.getAbsolutePath() + Report;
            File filePath = new File(path);

            deleted = filePath.delete();
        }

    }

    private void createVehiclePdf() {
     //   PVMIV_ID = VMIV_ID;
        printVehicleNote = true;
        involvedVehicle = mInvolvedVehicleDao.getInvolvedVehicle(VMIV_ID, AID_ID);
        // res.getString(R.string.tv0131);
        try {
            if (hasPassengers) {
                DA_DOC = deviceLocalel + "/involvedvehicle.pdf";
            } else {
                DA_DOC = deviceLocalel + "/involvedvehiclenp.pdf";
            }
            document = PDDocument.load(assetManager.open(DA_DOC));
        } catch (IOException e) {
            e.printStackTrace();
        }
        PDDocumentCatalog docCatalog = document.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();
  
        Page++;
        if (Page < 10) {
            PageS = "00" + Integer.toString(Page);
        }
        if (Page > 9 && Page < 100) {
            PageS = "0" + Integer.toString(Page);
        }
        if (Page > 99) {
            PageS = Integer.toString(Page);
        }

        try {
            PDTextField IV_TYPE = (PDTextField) acroForm.getField("IV_TYPE");
            IV_TYPE.setValue(involvedVehicle.getIV_TYPE());
            IV_TYPE.setPartialName("IV_TYPE" + PageS);
            IV_TYPE.setReadOnly(false);


        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PDTextField IV_TAG = (PDTextField) acroForm.getField("IV_TAG");
            IV_TAG.setValue(involvedVehicle.getIV_TAG());
            IV_TAG.setPartialName("IV_TAG" + PageS);
            IV_TAG.setReadOnly(false);


        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PDTextField IV_PLATE_COUNTRY = (PDTextField) acroForm.getField("IV_PLATE_COUNTRY");
            IV_PLATE_COUNTRY.setValue(involvedVehicle.getIV_PLATE_COUNTRY());
            IV_PLATE_COUNTRY.setPartialName("IV_PLATE_COUNTRY" + PageS);
            IV_PLATE_COUNTRY.setReadOnly(false);


        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PDTextField IV_STATE = (PDTextField) acroForm.getField("IV_STATE");
            IV_STATE.setValue(involvedVehicle.getIV_STATE());
            IV_STATE.setPartialName("IV_STATE" + PageS);
            IV_STATE.setReadOnly(false);


        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PDTextField IV_EXPIRATION = (PDTextField) acroForm.getField("IV_EXPIRATION");
            IV_EXPIRATION.setValue(involvedVehicle.getIV_EXPIRATION());
            IV_EXPIRATION.setPartialName("IV_EXPIRATION" + PageS);
            IV_EXPIRATION.setReadOnly(false);


        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PDTextField IV_VIN = (PDTextField) acroForm.getField("IV_VIN");
            IV_VIN.setValue(involvedVehicle.getIV_VIN());
            IV_VIN.setPartialName("IV_VIN" + PageS);
            IV_VIN.setReadOnly(false);


        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PDTextField IV_YEAR = (PDTextField) acroForm.getField("IV_YEAR");
            IV_YEAR.setValue(involvedVehicle.getIV_YEAR());
            IV_YEAR.setPartialName("IV_YEAR" + PageS);
            IV_YEAR.setReadOnly(false);


        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PDTextField IV_MAKE = (PDTextField) acroForm.getField("IV_MAKE");
            IV_MAKE.setValue(involvedVehicle.getIV_MAKE());
            IV_MAKE.setPartialName("IV_MAKE" + PageS);
            IV_MAKE.setReadOnly(false);


        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PDTextField IV_MODEL = (PDTextField) acroForm.getField("IV_MODEL");

            IV_MODEL.setValue(involvedVehicle.getIV_MODEL());
            IV_MODEL.setPartialName("IV_MODEL" + PageS);
            IV_MODEL.setReadOnly(false);


        } catch (IOException e) {
            e.printStackTrace();
        }
        acroForm2 = acroForm;
    }

    private void createOccupantsPdf() {
     //   PVMIV_ID = VMIV_ID;

        involvedVehicle = mInvolvedVehicleDao.getInvolvedVehicle(VMIV_ID, AID_ID);
        // res.getString(R.string.tv0131);
        try {
            DA_DOC = deviceLocalel + "/vehicleoccupants.pdf";
            document = PDDocument.load(assetManager.open(DA_DOC));
        } catch (IOException e) {
            e.printStackTrace();
        }
        PDDocumentCatalog docCatalog = document.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();

        Page++;
        if (Page < 10) {
            PageS = "00" + Integer.toString(Page);
        }
        if (Page > 9 && Page < 100) {
            PageS = "0" + Integer.toString(Page);
        }
        if (Page > 99) {
            PageS = Integer.toString(Page);
        }
        try {
            PDTextField IV_YEAR = (PDTextField) acroForm.getField("IV_YEAR");
            IV_YEAR.setValue(involvedVehicle.getIV_YEAR());
            IV_YEAR.setPartialName("IV_YEAR" + PageS);
            IV_YEAR.setReadOnly(false);


        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PDTextField IV_MAKE = (PDTextField) acroForm.getField("IV_MAKE");
            IV_MAKE.setValue(involvedVehicle.getIV_MAKE());
            IV_MAKE.setPartialName("IV_MAKE" + PageS);
            IV_MAKE.setReadOnly(false);


        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PDTextField IV_MODEL = (PDTextField) acroForm.getField("IV_MODEL");

            IV_MODEL.setValue(involvedVehicle.getIV_MODEL());
            IV_MODEL.setPartialName("IV_MODEL" + PageS);
            IV_MODEL.setReadOnly(false);


        } catch (IOException e) {
            e.printStackTrace();
        }
        acroForm2 = acroForm;
    }

    private void createManifest() {

        PDAcroForm acroForm = acroForm2;
        int ManifestEntries = vehicleManifestList.size();
        ManifestEntries--;
        int OCC = 0;
        String DA_STRING2 = res.getString(R.string.hiv_occupants);
        for (VehicleManifest vehicleManifest2 : vehicleManifestList) {


            int NVMIV_ID = vehicleManifest2.getVMIV_ID();
            if (VMIV_ID == NVMIV_ID)  {
                int NVMIP_ID = vehicleManifest2.getVMIP_ID();
                    // if (NVMIV_ID == PVMIV_ID) {
                OCC++;

                involvedParty = mInvolvedPartyDao.getInvolvedParty(NVMIP_ID, AID_ID);


                String IP_PTYPE = involvedParty.getIP_PTYPE();
                IP_PTYPE = utils.splitDash2(IP_PTYPE);
                String IP_FNAME = involvedParty.getIP_FNAME();
                // String DA_STRING = " ";

                String DA_STRING = IP_PTYPE + " - " + IP_FNAME;

                switch (OCC) {
                    case 1: {
                        try {
                            PDTextField OCC01 = (PDTextField) acroForm.getField("OCC01");
                            OCC01.setValue(DA_STRING2);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC01.setPartialName("OCC01" + PageS);
                            OCC01.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            PDTextField OCC02 = (PDTextField) acroForm.getField("OCC02");
                            OCC02.setValue(DA_STRING);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC02.setPartialName("OCC02" + PageS);
                            OCC02.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 2: {
                        try {
                            PDTextField OCC03 = (PDTextField) acroForm.getField("OCC03");
                            OCC03.setValue(DA_STRING);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC03.setPartialName("OCC03" + PageS);
                            OCC03.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 3: {
                        try {
                            PDTextField OCC04 = (PDTextField) acroForm.getField("OCC04");
                            OCC04.setValue(DA_STRING);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC04.setPartialName("OCC04" + PageS);
                            OCC04.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 4: {
                        try {
                            PDTextField OCC05 = (PDTextField) acroForm.getField("OCC05");
                            OCC05.setValue(DA_STRING);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC05.setPartialName("OCC05" + PageS);
                            OCC05.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 5: {
                        try {
                            PDTextField OCC06 = (PDTextField) acroForm.getField("OCC06");
                            OCC06.setValue(DA_STRING);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC06.setPartialName("OCC06" + PageS);
                            OCC06.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 6: {
                        try {
                            PDTextField OCC07 = (PDTextField) acroForm.getField("OCC07");
                            OCC07.setValue(DA_STRING);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC07.setPartialName("OCC07" + PageS);
                            OCC07.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 7: {
                        try {
                            PDTextField OCC08 = (PDTextField) acroForm.getField("OCC08");
                            OCC08.setValue(DA_STRING);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC08.setPartialName("OCC08" + PageS);
                            OCC08.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 8: {
                        try {
                            PDTextField OCC09 = (PDTextField) acroForm.getField("OCC09");
                            OCC09.setValue(DA_STRING);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC09.setPartialName("OCC09" + PageS);
                            OCC09.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 9: {
                        try {
                            PDTextField OCC10 = (PDTextField) acroForm.getField("OCC10");
                            OCC10.setValue(DA_STRING);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC10.setPartialName("OCC10" + PageS);
                            OCC10.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 10: {
                        try {
                            PDTextField OCC11 = (PDTextField) acroForm.getField("OCC11");
                            OCC11.setValue(DA_STRING);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC11.setPartialName("OCC11" + PageS);
                            OCC11.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 11: {
                        printVehicle();
                        createOccupantsPdf();
                        acroForm = acroForm2;


                        try {
                            PDTextField OCC01 = (PDTextField) acroForm.getField("OCC01");
                            OCC01.setValue(DA_STRING2);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC01.setPartialName("OCC01" + PageS);
                            OCC01.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            PDTextField OCC02 = (PDTextField) acroForm.getField("OCC02");
                            OCC02.setValue(DA_STRING);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC02.setPartialName("OCC02" + PageS);
                            OCC02.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;


                    }
                    case 12: {
                        try {
                            PDTextField OCC03 = (PDTextField) acroForm.getField("OCC03");
                            OCC03.setValue(DA_STRING);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC03.setPartialName("OCC03" + PageS);
                            OCC03.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 13: {
                        try {
                            PDTextField OCC04 = (PDTextField) acroForm.getField("OCC04");
                            OCC04.setValue(DA_STRING);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC04.setPartialName("OCC04" + PageS);
                            OCC04.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 14: {
                        try {
                            PDTextField OCC05 = (PDTextField) acroForm.getField("OCC05");
                            OCC05.setValue(DA_STRING);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC05.setPartialName("OCC05" + PageS);
                            OCC05.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 15: {
                        try {
                            PDTextField OCC06 = (PDTextField) acroForm.getField("OCC06");
                            OCC06.setValue(DA_STRING);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC06.setPartialName("OCC06" + PageS);
                            OCC06.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 16: {
                        try {
                            PDTextField OCC07 = (PDTextField) acroForm.getField("OCC07");
                            OCC07.setValue(DA_STRING);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC07.setPartialName("OCC07" + PageS);
                            OCC07.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 17: {
                        try {
                            PDTextField OCC08 = (PDTextField) acroForm.getField("OCC08");
                            OCC08.setValue(DA_STRING);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC08.setPartialName("OCC08" + PageS);
                            OCC08.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 18: {
                        try {
                            PDTextField OCC09 = (PDTextField) acroForm.getField("OCC09");
                            OCC09.setValue(DA_STRING);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC09.setPartialName("OCC09" + PageS);
                            OCC09.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 19: {
                        try {
                            PDTextField OCC10 = (PDTextField) acroForm.getField("OCC10");
                            OCC10.setValue(DA_STRING);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC10.setPartialName("OCC10" + PageS);
                            OCC10.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 20: {
                        try {
                            PDTextField OCC11 = (PDTextField) acroForm.getField("OCC11");
                            OCC11.setValue(DA_STRING);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC11.setPartialName("OCC11" + PageS);
                            OCC11.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 21: {
                        printVehicle();
                        createOccupantsPdf();
                        acroForm = acroForm2;


                        try {
                            PDTextField OCC01 = (PDTextField) acroForm.getField("OCC01");
                            OCC01.setValue(DA_STRING2);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC01.setPartialName("OCC01" + PageS);
                            OCC01.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            PDTextField OCC02 = (PDTextField) acroForm.getField("OCC02");
                            OCC02.setValue(DA_STRING);
                            // Optional: don't allow this field to be edited
                            //		pdfField = IP_FNAME;
                            OCC02.setPartialName("OCC02" + PageS);
                            OCC02.setReadOnly(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;


                    }
                }
            }
        }


    }

    private void printVehicle() {
        albumName = "AccidentReport";
        File mStorageDirectory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!mStorageDirectory.exists()) {
            mStorageDirectory.mkdirs();
        }

        Report = "/AccidentReport000" + AID + PageS + ".pdf";
        String path = mStorageDirectory.getAbsolutePath() + Report;
//			tv.setText("Saved Accident form to " + path);

        try {
            document.save(path);
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void createPdfFromJpg() {

        Bitmap bitmap = BitmapFactory.decodeFile(ImagePath);
        //  iv_image.setImageBitmap(bitmap);
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels;
        float width = displaymetrics.widthPixels;

      //  int convertHighth = (int) hight, convertWidth = (int) width;
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();


        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawPaint(paint);


        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);

        File mStorageDirectory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!mStorageDirectory.exists()) {
            mStorageDirectory.mkdirs();
        }

        Report = "/AccidentReport000" + AID + PageS + ".pdf";
        String path = mStorageDirectory.getAbsolutePath() + Report;

        File filePath = new File(path);
        try {
            document.writeTo(new FileOutputStream(filePath));


        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();

    }

    private void createVehicleNotePdf() {

        accidentnoteList = mAccidentNoteDao.getIVAccidentNotes(AID_ID, VMIV_ID);
        if (accidentnoteList.size() > 0) {

            int OCC = 1;

            for (AccidentNote accidentNote : accidentnoteList) {
                Page++;
                if (Page < 10) {
                    PageS = "00" + Integer.toString(Page);
                }
                if (Page > 9 && Page < 100) {
                    PageS = "0" + Integer.toString(Page);
                }
                if (Page > 99) {
                    PageS = Integer.toString(Page);
                }
                DA_DOC = deviceLocalel + "/notes.pdf";
                try {
                    document = PDDocument.load(assetManager.open(DA_DOC));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                PDDocumentCatalog docCatalog = document.getDocumentCatalog();
                PDAcroForm acroForm = docCatalog.getAcroForm();

                switch (OCC) {
                    case 1: {
                        OCC = 4;
                        try {
                            PDTextField NOTE01 = (PDTextField) acroForm.getField("NOTE01");
                            NOTE01.setValue(res.getString(R.string.tv0300) + " " + accidentNote.getAN_SUBJECT());
                            NOTE01.setPartialName("NOTE01" + PageS);
                            NOTE01.setReadOnly(false);


                        } catch (IOException f) {
                            f.printStackTrace();
                        }
                        try {
                            PDTextField NOTE02 = (PDTextField) acroForm.getField("NOTE02");
                            NOTE02.setValue(res.getString(R.string.tv0301) + " " + accidentNote.getAN_NOTE());
                            NOTE02.setPartialName("NOTE02" + PageS);
                            NOTE02.setReadOnly(false);


                        } catch (IOException f) {
                            f.printStackTrace();
                        }
                        involvedVehicle = mInvolvedVehicleDao.getInvolvedVehicle(VMIV_ID, AID_ID);


                        try {
                            PDTextField NOTE03 = (PDTextField) acroForm.getField("NOTE03");
                            NOTE03.setValue(involvedVehicle.getIV_YEAR() + " " + involvedVehicle.getIV_MAKE() + " " + involvedVehicle.getIV_MODEL());
                            NOTE03.setPartialName("NOTE03" + PageS);
                            NOTE03.setReadOnly(false);


                        } catch (IOException f) {
                            f.printStackTrace();
                        }

                        try {
                            PDTextField NOTE04 = (PDTextField) acroForm.getField("NOTE04");
                            NOTE04.setValue(accidentNote.getAN_CDATE() + "   " + accidentNote.getAN_UDATE());
                            NOTE04.setPartialName("NOTE04" + PageS);
                            NOTE04.setReadOnly(false);


                        } catch (IOException f) {
                            f.printStackTrace();
                        }

                        break;
                    }


                }
                OCC = 1;


                albumName = "AccidentReport";
                File mStorageDirectory = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), albumName);
                if (!mStorageDirectory.exists())

                {
                    mStorageDirectory.mkdirs();
                }

                Report = "/AccidentReport000" + AID + PageS + ".pdf";
                String path = mStorageDirectory.getAbsolutePath() + Report;
//			tv.setText("Saved Accident form to " + path);

                try

                {
                    document.save(path);
                    document.close();
                } catch (
                        IOException e)

                {
                    e.printStackTrace();
                }
            }

        }


    }
    private void createUnattachedNotePdf() {

        accidentnoteList = mAccidentNoteDao.getUnattachedAccidentNotes(AID_ID);
        if (accidentnoteList.size() > 0) {

            int OCC = 1;

            for (AccidentNote accidentNote : accidentnoteList) {
                Page++;
                if (Page < 10) {
                    PageS = "00" + Integer.toString(Page);
                }
                if (Page > 9 && Page < 100) {
                    PageS = "0" + Integer.toString(Page);
                }
                if (Page > 99) {
                    PageS = Integer.toString(Page);
                }
                DA_DOC = deviceLocalel + "/notes.pdf";
                try {
                    document = PDDocument.load(assetManager.open(DA_DOC));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                PDDocumentCatalog docCatalog = document.getDocumentCatalog();
                PDAcroForm acroForm = docCatalog.getAcroForm();

                switch (OCC) {
                    case 1: {
                        OCC = 4;
                        try {
                            PDTextField NOTE01 = (PDTextField) acroForm.getField("NOTE01");
                            NOTE01.setValue(res.getString(R.string.tv0300) + " " + accidentNote.getAN_SUBJECT());
                            NOTE01.setPartialName("NOTE01" + PageS);
                            NOTE01.setReadOnly(false);


                        } catch (IOException f) {
                            f.printStackTrace();
                        }
                        try {
                            PDTextField NOTE02 = (PDTextField) acroForm.getField("NOTE02");
                            NOTE02.setValue(res.getString(R.string.tv0301) + " " + accidentNote.getAN_NOTE());
                            NOTE02.setPartialName("NOTE02" + PageS);
                            NOTE02.setReadOnly(false);


                        } catch (IOException f) {
                            f.printStackTrace();
                        }



                        try {
                            PDTextField NOTE04 = (PDTextField) acroForm.getField("NOTE04");
                            NOTE04.setValue(accidentNote.getAN_CDATE() + "   " + accidentNote.getAN_UDATE());
                            NOTE04.setPartialName("NOTE04" + PageS);
                            NOTE04.setReadOnly(false);


                        } catch (IOException f) {
                            f.printStackTrace();
                        }

                        break;
                    }


                }
                OCC = 1;


                albumName = "AccidentReport";
                File mStorageDirectory = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), albumName);
                if (!mStorageDirectory.exists())

                {
                    mStorageDirectory.mkdirs();
                }

                Report = "/AccidentReport000" + AID + PageS + ".pdf";
                String path = mStorageDirectory.getAbsolutePath() + Report;
//			tv.setText("Saved Accident form to " + path);

                try

                {
                    document.save(path);
                    document.close();
                } catch (
                        IOException e)

                {
                    e.printStackTrace();
                }
            }

        }


    }

    private void createPhotoPdf() {
        mPersistenceObjDao.updateData("PERSIST_PIC_MODE", "INVOLVED_VEHICLE");

        List<InvolvedImageStore> involvedImageStoreList = new ArrayList<>();
        involvedImageStoreList = mInvolvedImageStoreDao.getAccPics(AID_ID, VMIV_ID);
        for (InvolvedImageStore involvedImageStore : involvedImageStoreList) {
            ImagePath = involvedImageStore.getAP_PATH();
            if (splitFileExt(ImagePath).equals("jpg")) {
                Page++;
                if (Page < 10) {
                    PageS = "00" + Integer.toString(Page);
                }
                if (Page > 9 && Page < 100) {
                    PageS = "0" + Integer.toString(Page);
                }
                if (Page > 99) {
                    PageS = Integer.toString(Page);
                }

                createPdfFromJpg();
            }
        }
    }
    private void createUnattachedPhotoPdf() {


        List<InvolvedImageStore> involvedImageStoreList = new ArrayList<>();
        involvedImageStoreList = mInvolvedImageStoreDao.getAllUnattachedAccPics(AID_ID);
        for (InvolvedImageStore involvedImageStore : involvedImageStoreList) {
            ImagePath = involvedImageStore.getAP_PATH();
            if (splitFileExt(ImagePath).equals("jpg")) {
                Page++;
                if (Page < 10) {
                    PageS = "00" + Integer.toString(Page);
                }
                if (Page > 9 && Page < 100) {
                    PageS = "0" + Integer.toString(Page);
                }
                if (Page > 99) {
                    PageS = Integer.toString(Page);
                }

                createPdfFromJpg();
            }
        }
    }

    private void createInvolvedPartyPdf() {
        //  for (InvolvedParty involvedparty : involvedParty) {

        try {
            document = PDDocument.load(assetManager.open(IP_TEMPLATE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        PDDocumentCatalog docCatalog = document.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();

        Page++;
        if (Page < 10) {
            PageS = "00" + Integer.toString(Page);
        }
        if (Page > 9 && Page < 100) {
            PageS = "0" + Integer.toString(Page);
        }
        if (Page > 99) {
            PageS = Integer.toString(Page);
        }
        // Fill the text field

        try {
            PDTextField IP_FNAME = (PDTextField) acroForm.getField("IP_FNAME");

            String FName = involvedParty.getIP_FNAME();
            IP_FNAME.setValue(involvedParty.getIP_FNAME());
            // Optional: don't allow this field to be edited
            //		pdfField = IP_FNAME;
            IP_FNAME.setPartialName("IP_FNAME" + PageS);
            IP_FNAME.setReadOnly(false);


        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            PDTextField IP_LIC = (PDTextField) acroForm.getField("IP_LIC");
            IP_LIC.setValue(involvedParty.getIP_LICENSE());
            // Optional: don't allow this field to be edited
            IP_LIC.setPartialName("IP_LIC" + PageS);
            IP_LIC.setReadOnly(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            PDTextField IP_LICENSE_COUNTRY = (PDTextField) acroForm.getField("IP_LICENSE_COUNTRY");
            IP_LICENSE_COUNTRY.setValue(involvedParty.getIP_LICENSE_COUNTRY());
            // Optional: don't allow this field to be edited
            IP_LICENSE_COUNTRY.setPartialName("IP_LICENSE_COUNTRY" + PageS);
            IP_LICENSE_COUNTRY.setReadOnly(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PDTextField IP_LST = (PDTextField) acroForm.getField("IP_LST");
            IP_LST.setValue(involvedParty.getIP_LST());
            // Optional: don't allow this field to be edited
            IP_LST.setPartialName("IP_LST" + PageS);
            IP_LST.setReadOnly(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PDTextField IP_ADDR1 = (PDTextField) acroForm.getField("IP_ADDR1");
            IP_ADDR1.setValue(involvedParty.getIP_ADDR1());
            // Optional: don't allow this field to be edited
            IP_ADDR1.setPartialName("IP_ADDR1" + PageS);
            IP_ADDR1.setReadOnly(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PDTextField IP_CITY = (PDTextField) acroForm.getField("IP_CITY");
            IP_CITY.setValue(involvedParty.getIP_CITY());
            // Optional: don't allow this field to be edited
            IP_CITY.setPartialName("IP_CITY" + PageS);
            IP_CITY.setReadOnly(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PDTextField IP_ST = (PDTextField) acroForm.getField("IP_ST");
            IP_ST.setValue(involvedParty.getIP_ST());
            // Optional: don't allow this field to be edited
            IP_ST.setPartialName("IP_ST" + PageS);
            IP_ST.setReadOnly(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PDTextField IP_ZIP = (PDTextField) acroForm.getField("IP_ZIP");
            IP_ZIP.setValue(involvedParty.getIP_ZIP());
            // Optional: don't allow this field to be edited
            IP_ZIP.setPartialName("IP_ZIP" + PageS);
            IP_ZIP.setReadOnly(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PDTextField IP_RESIDENT_COUNTRY = (PDTextField) acroForm.getField("IP_RESIDENT_COUNTRY");
            IP_RESIDENT_COUNTRY.setValue(involvedParty.getIP_RESIDENT_COUNTRY());
            // Optional: don't allow this field to be edited
            IP_RESIDENT_COUNTRY.setPartialName("IP_RESIDENT_COUNTRY" + PageS);
            IP_RESIDENT_COUNTRY.setReadOnly(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            PDTextField IP_EMAIL = (PDTextField) acroForm.getField("IP_EMAIL");
            IP_EMAIL.setValue(involvedParty.getIP_EMAIL());
            // Optional: don't allow this field to be edited
            IP_EMAIL.setPartialName("IP_EMAIL" + PageS);
            IP_EMAIL.setReadOnly(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PDTextField IP_PHON1_COUNTRY = (PDTextField) acroForm.getField("IP_PHON1_COUNTRY");
            IP_PHON1_COUNTRY.setValue(involvedParty.getIP_PHON1_COUNTRY());
            // Optional: don't allow this field to be edited
            IP_PHON1_COUNTRY.setPartialName("IP_PHON1_COUNTRY" + PageS);
            IP_PHON1_COUNTRY.setReadOnly(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PDTextField IP_PHON1 = (PDTextField) acroForm.getField("IP_PHON1");
            IP_PHON1.setValue(involvedParty.getIP_PHON1());
            // Optional: don't allow this field to be edited
            IP_PHON1.setPartialName("IP_PHON1" + PageS);
            IP_PHON1.setReadOnly(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PDTextField IP_PHON2_COUNTRY = (PDTextField) acroForm.getField("IP_PHON2_COUNTRY");
            IP_PHON2_COUNTRY.setValue(involvedParty.getIP_PHON2_COUNTRY());
            // Optional: don't allow this field to be edited
            IP_PHON2_COUNTRY.setPartialName("IP_PHON2_COUNTRY" + PageS);
            IP_PHON2_COUNTRY.setReadOnly(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PDTextField IP_PHON2 = (PDTextField) acroForm.getField("IP_PHON2");
            IP_PHON2.setValue(involvedParty.getIP_PHON2());
            // Optional: don't allow this field to be edited
            IP_PHON2.setPartialName("IP_PHON2" + PageS);
            IP_PHON2.setReadOnly(false);
        } catch (IOException e) {
            e.printStackTrace();

        }
        try {
            PDTextField IP_PHON3_COUNTRY = (PDTextField) acroForm.getField("IP_PHON3_COUNTRY");
            IP_PHON3_COUNTRY.setValue(involvedParty.getIP_PHON3_COUNTRY());
            // Optional: don't allow this field to be edited
            IP_PHON3_COUNTRY.setPartialName("IP_PHON3_COUNTRY" + PageS);
            IP_PHON3_COUNTRY.setReadOnly(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PDTextField IP_PHON3 = (PDTextField) acroForm.getField("IP_PHON3");
            IP_PHON3.setValue(involvedParty.getIP_PHON3());
            // Optional: don't allow this field to be edited
            IP_PHON3.setPartialName("IP_PHON3" + PageS);
            IP_PHON3.setReadOnly(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String DA_STRING = involvedParty.getIP_PTYPE();
            DA_STRING = utils.getThirdWord(DA_STRING);
            PDTextField IP_PTYPE = (PDTextField) acroForm.getField("IP_PTYPE");
            IP_PTYPE.setValue(DA_STRING);
            // Optional: don't allow this field to be edited
            IP_PTYPE.setPartialName("IP_PTYPE" + PageS);
            IP_PTYPE.setReadOnly(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            PDTextField IP_COMP = (PDTextField) acroForm.getField("IP_COMP");
            IP_COMP.setValue(involvedParty.getIP_COMP());
            // Optional: don't allow this field to be edited
            IP_COMP.setPartialName("IP_COMP" + PageS);
            IP_COMP.setReadOnly(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        albumName = "AccidentReport";
        File mStorageDirectory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!mStorageDirectory.exists()) {
            mStorageDirectory.mkdirs();
        }

        Report = "/AccidentReport000" + AID + PageS + ".pdf";
        String path = mStorageDirectory.getAbsolutePath() + Report;
//			tv.setText("Saved Accident form to " + path);

        try {
            document.save(path);
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPersistenceObjDao.updateData("PERSIST_PIC_MODE", "INVOLVED_PARTY");
        InvolvedImageStoreDao mInvolvedImageStoreDao = new InvolvedImageStoreDao(this);
        List<InvolvedImageStore> involvedImageStoreList = new ArrayList<>();
        involvedImageStoreList = mInvolvedImageStoreDao.getAccPics(AID_ID, VMIP_ID);
        for (InvolvedImageStore involvedImageStore : involvedImageStoreList) {
            ImagePath = involvedImageStore.getAP_PATH();
            if (splitFileExt(ImagePath).equals("jpg")) {
            Page++;
            if (Page < 10) {
                PageS = "00" + Integer.toString(Page);
            }
            if (Page > 9 && Page < 100) {
                PageS = "0" + Integer.toString(Page);
            }
            if (Page > 99) {
                PageS = Integer.toString(Page);
            }


                createPdfFromJpg();
            }
        }
        // *********************



        involvedParty = mInvolvedPartyDao.getInvolvedParty(VMIP_ID, AID_ID);

        String DA_NAME = involvedParty.getIP_FNAME();

        accidentnoteList = mAccidentNoteDao.getIPAccidentNotes(AID_ID, VMIP_ID);
        if (accidentnoteList.size() > 0) {

            int OCC = 1;

            for (AccidentNote accidentNote : accidentnoteList) {
                Page++;
                if (Page < 10) {
                    PageS = "00" + Integer.toString(Page);
                }
                if (Page > 9 && Page < 100) {
                    PageS = "0" + Integer.toString(Page);
                }
                if (Page > 99) {
                    PageS = Integer.toString(Page);
                }
                DA_DOC = deviceLocalel + "/notes.pdf";
                try {
                    document = PDDocument.load(assetManager.open(DA_DOC));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                docCatalog = document.getDocumentCatalog();
                acroForm = docCatalog.getAcroForm();

                switch (OCC) {
                    case 1: {
                        OCC = 4;
                        try {
                            PDTextField NOTE01 = (PDTextField) acroForm.getField("NOTE01");
                            NOTE01.setValue(res.getString(R.string.tv0300) + " " + accidentNote.getAN_SUBJECT());
                            NOTE01.setPartialName("NOTE01" + PageS);
                            NOTE01.setReadOnly(false);


                        } catch (IOException f) {
                            f.printStackTrace();
                        }
                        try {
                            PDTextField NOTE02 = (PDTextField) acroForm.getField("NOTE02");
                            NOTE02.setValue(res.getString(R.string.tv0301) + " " + accidentNote.getAN_NOTE());
                            NOTE02.setPartialName("NOTE02" + PageS);
                            NOTE02.setReadOnly(false);


                        } catch (IOException f) {
                            f.printStackTrace();
                        }


                        try {
                            PDTextField NOTE03 = (PDTextField) acroForm.getField("NOTE03");
                            NOTE03.setValue(DA_NAME);
                            NOTE03.setPartialName("NOTE03" + PageS);
                            NOTE03.setReadOnly(false);


                        } catch (IOException f) {
                            f.printStackTrace();
                        }

                        try {
                            PDTextField NOTE04 = (PDTextField) acroForm.getField("NOTE04");
                            NOTE04.setValue(accidentNote.getAN_CDATE() + "   " + accidentNote.getAN_UDATE());
                            NOTE04.setPartialName("NOTE04" + PageS);
                            NOTE04.setReadOnly(false);


                        } catch (IOException f) {
                            f.printStackTrace();
                        }

                        break;
                    }


                }
                OCC = 1;

                albumName = "AccidentReport";
                mStorageDirectory = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), albumName);
                if (!mStorageDirectory.exists())

                {
                    mStorageDirectory.mkdirs();
                }

                Report = "/AccidentReport000" + AID + PageS + ".pdf";
                path = mStorageDirectory.getAbsolutePath() + Report;

                try

                {
                    document.save(path);
                    document.close();
                } catch (
                        IOException e)

                {
                    e.printStackTrace();
                }
            }
        }


    }

    private void createPolicyReportPdf() {

        List<InsurancePolicy> insurancepolicyList = mInsurancePolicyDao.getHolderInsurancePolicys(AID_ID, VMIP_ID);

        if (insurancepolicyList.size() > 0) {


            for (InsurancePolicy insurancePolicy : insurancepolicyList) {
                Page++;
                if (Page < 10) {
                    PageS = "00" + Integer.toString(Page);
                }
                if (Page > 9 && Page < 100) {
                    PageS = "0" + Integer.toString(Page);
                }
                if (Page > 99) {
                    PageS = Integer.toString(Page);
                }
                int CURRENT_IPO_ID = insurancePolicy.getIPO_ID();
                DA_DOC = deviceLocalel + "/policyreport.pdf";
                try {
                    document = PDDocument.load(assetManager.open(DA_DOC));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                PDDocumentCatalog docCatalog = document.getDocumentCatalog();
                PDAcroForm acroForm = docCatalog.getAcroForm();


                try {
                    PDTextField POLICY_HOLDER = (PDTextField) acroForm.getField("POLICY_HOLDER");
                    POLICY_HOLDER.setValue(involvedParty.getIP_FNAME());
                    POLICY_HOLDER.setPartialName("POLICY_HOLDER" + PageS);
                    POLICY_HOLDER.setReadOnly(false);


                } catch (IOException f) {
                    f.printStackTrace();
                }
                try {
                    PDTextField POLICY_COMPANY = (PDTextField) acroForm.getField("POLICY_COMPANY");
                    POLICY_COMPANY.setValue(insurancePolicy.getIPO_CNAM());
                    POLICY_COMPANY.setPartialName("POLICY_COMPANY" + PageS);
                    POLICY_COMPANY.setReadOnly(false);


                } catch (IOException f) {
                    f.printStackTrace();
                }
                try {
                    PDTextField POLICY_NUMBER = (PDTextField) acroForm.getField("POLICY_NUMBER");
                    POLICY_NUMBER.setValue(insurancePolicy.getIPO_PNUM());
                    POLICY_NUMBER.setPartialName("POLICY_NUMBER" + PageS);
                    POLICY_NUMBER.setReadOnly(false);


                } catch (IOException f) {
                    f.printStackTrace();
                }
                String FNAME01 = "";
                String FNAME02 = "";
                String FNAME03 = "";
                String FNAME04 = "";
                String FNAME05 = "";
                String FNAME06 = "";
                String FNAME07 = "";
                String FNAME08 = "";
                String FNAME09 = "";
                String FNAME10 = "";
                String FNAME11 = "";
                String FNAME12 = "";
                String FNAME = "";

                List<InsurancePolicyV> insurancepolicyvList = mInsurancePolicyVDao.getInsurancePolicyVs(AID_ID, CURRENT_IPO_ID);
                int OCC = 1;

                for (InsurancePolicyV insurancePolicyV : insurancepolicyvList) {
                    int IV_ID = insurancePolicyV.getIPV_IVID();
                    involvedVehicle = mInvolvedVehicleDao.getInvolvedVehicle(IV_ID, AID_ID);
                    FNAME = involvedVehicle.getIV_YEAR() + " " +
                            involvedVehicle.getIV_MAKE() + " " +
                            involvedVehicle.getIV_MODEL() + " - " +
                            involvedVehicle.getIV_TYPE();
                    switch (OCC) {
                        case 1: {
                            FNAME01 = FNAME;
                            break;
                        }

                        case 2: {
                            FNAME02 = FNAME;
                            break;
                        }
                        case 3: {
                            FNAME03 = FNAME;
                            break;
                        }
                        case 4: {
                            FNAME04 = FNAME;
                            break;
                        }
                        case 5: {
                            FNAME05 = FNAME;
                            break;
                        }
                        case 6: {
                            FNAME06 = FNAME;
                            break;
                        }
                        case 7: {
                            FNAME07 = FNAME;
                            break;
                        }
                        case 8: {
                            FNAME08 = FNAME;
                            break;
                        }
                        case 9: {
                            FNAME09 = FNAME;
                            break;
                        }
                        case 10: {
                            FNAME10 = FNAME;
                            break;
                        }
                        case 11: {
                            FNAME11 = FNAME;
                            break;
                        }
                        case 12: {
                            FNAME12 = FNAME;
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                    OCC++;
                }


                List<InsurancePolicyP> insurancepolicypList = mInsurancePolicyPDao.getInsurancePolicyPs(AID_ID, CURRENT_IPO_ID);
                for (InsurancePolicyP insurancePolicyP : insurancepolicypList) {
                    int IP_ID = insurancePolicyP.getIPP_IPID();
                    involvedParty = mInvolvedPartyDao.getInvolvedParty(IP_ID, AID_ID);
                    FNAME = involvedParty.getIP_FNAME() + " - " +
                            involvedParty.getIP_PTYPE();
                    switch (OCC) {
                        case 1: {
                            FNAME01 = FNAME;
                            break;
                        }

                        case 2: {
                            FNAME02 = FNAME;
                            break;
                        }
                        case 3: {
                            FNAME03 = FNAME;
                            break;
                        }
                        case 4: {
                            FNAME04 = FNAME;
                            break;
                        }
                        case 5: {
                            FNAME05 = FNAME;
                            break;
                        }
                        case 6: {
                            FNAME06 = FNAME;
                            break;
                        }
                        case 7: {
                            FNAME07 = FNAME;
                            break;
                        }
                        case 8: {
                            FNAME08 = FNAME;
                            break;
                        }
                        case 9: {
                            FNAME09 = FNAME;
                            break;
                        }
                        case 10: {
                            FNAME10 = FNAME;
                            break;
                        }
                        case 11: {
                            FNAME11 = FNAME;
                            break;
                        }
                        case 12: {
                            FNAME12 = FNAME;
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                    OCC++;
                }

                try {
                    PDTextField IP_FNAME01 = (PDTextField) acroForm.getField("IP_FNAME01");
                    IP_FNAME01.setValue(FNAME01);
                    IP_FNAME01.setPartialName("IP_FNAME01" + PageS);
                    IP_FNAME01.setReadOnly(false);


                } catch (IOException f) {
                    f.printStackTrace();
                }
                try {
                    PDTextField IP_FNAME02 = (PDTextField) acroForm.getField("IP_FNAME02");
                    IP_FNAME02.setValue(FNAME02);
                    IP_FNAME02.setPartialName("IP_FNAME02" + PageS);
                    IP_FNAME02.setReadOnly(false);


                } catch (IOException f) {
                    f.printStackTrace();
                }

                try {
                    PDTextField IP_FNAME03 = (PDTextField) acroForm.getField("IP_FNAME03");
                    IP_FNAME03.setValue(FNAME03);
                    IP_FNAME03.setPartialName("IP_FNAME03" + PageS);
                    IP_FNAME03.setReadOnly(false);


                } catch (IOException f) {
                    f.printStackTrace();
                }

                try {
                    PDTextField IP_FNAME04 = (PDTextField) acroForm.getField("IP_FNAME04");
                    IP_FNAME04.setValue(FNAME04);
                    IP_FNAME04.setPartialName("IP_FNAME04" + PageS);
                    IP_FNAME04.setReadOnly(false);


                } catch (IOException f) {
                    f.printStackTrace();
                }
                try {
                    PDTextField IP_FNAME05 = (PDTextField) acroForm.getField("IP_FNAME05");
                    IP_FNAME05.setValue(FNAME05);
                    IP_FNAME05.setPartialName("IP_FNAME05" + PageS);
                    IP_FNAME05.setReadOnly(false);


                } catch (IOException f) {
                    f.printStackTrace();
                }
                try {
                    PDTextField IP_FNAME06 = (PDTextField) acroForm.getField("IP_FNAME06");
                    IP_FNAME06.setValue(FNAME06);
                    IP_FNAME06.setPartialName("IP_FNAME06" + PageS);
                    IP_FNAME06.setReadOnly(false);


                } catch (IOException f) {
                    f.printStackTrace();
                }
                try {
                    PDTextField IP_FNAME07 = (PDTextField) acroForm.getField("IP_FNAME07");
                    IP_FNAME07.setValue(FNAME07);
                    IP_FNAME07.setPartialName("IP_FNAME07" + PageS);
                    IP_FNAME07.setReadOnly(false);


                } catch (IOException f) {
                    f.printStackTrace();
                }
                try {
                    PDTextField IP_FNAME08 = (PDTextField) acroForm.getField("IP_FNAME08");
                    IP_FNAME08.setValue(FNAME08);
                    IP_FNAME08.setPartialName("IP_FNAME08" + PageS);
                    IP_FNAME08.setReadOnly(false);


                } catch (IOException f) {
                    f.printStackTrace();
                }
                try {
                    PDTextField IP_FNAME09 = (PDTextField) acroForm.getField("IP_FNAME09");
                    IP_FNAME09.setValue(FNAME09);
                    IP_FNAME09.setPartialName("IP_FNAME09" + PageS);
                    IP_FNAME09.setReadOnly(false);


                } catch (IOException f) {
                    f.printStackTrace();
                }
                try {
                    PDTextField IP_FNAME10 = (PDTextField) acroForm.getField("IP_FNAME10");
                    IP_FNAME10.setValue(FNAME10);
                    IP_FNAME10.setPartialName("IP_FNAME10" + PageS);
                    IP_FNAME10.setReadOnly(false);


                } catch (IOException f) {
                    f.printStackTrace();
                }
                try {
                    PDTextField IP_FNAME11 = (PDTextField) acroForm.getField("IP_FNAME11");
                    IP_FNAME11.setValue(FNAME11);
                    IP_FNAME11.setPartialName("IP_FNAME11" + PageS);
                    IP_FNAME11.setReadOnly(false);


                } catch (IOException f) {
                    f.printStackTrace();
                }
                try {
                    PDTextField IP_FNAME12 = (PDTextField) acroForm.getField("IP_FNAME12");
                    IP_FNAME12.setValue(FNAME12);
                    IP_FNAME12.setPartialName("IP_FNAME12" + PageS);
                    IP_FNAME12.setReadOnly(false);


                } catch (IOException f) {
                    f.printStackTrace();
                }


                albumName = "AccidentReport";
                File mStorageDirectory = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), albumName);
                if (!mStorageDirectory.exists())

                {
                    mStorageDirectory.mkdirs();
                }

                Report = "/AccidentReport000" + AID + PageS + ".pdf";
                String path = mStorageDirectory.getAbsolutePath() + Report;
//			tv.setText("Saved Accident form to " + path);

                try

                {
                    document.save(path);
                    document.close();
                } catch (
                        IOException e)

                {
                    e.printStackTrace();
                }
            }

        }


    }

    private void viewPdf() {

        //  appendForm();
        doClose();

        intent = new Intent(this, PDFViewActivityNew.class);
        this.startActivity(intent);

    }
    public void disableButtons() {
        btnHelp.setEnabled(false);
        btnCreate.setEnabled(false);
        btnView.setEnabled(false);
        btnShare.setEnabled(false);
    //    btnCloud.setEnabled(false);  Enterprise


    }
    public void enableButtons() {
        btnHelp.setEnabled(true);
        btnCreate.setEnabled(true);
        btnView.setEnabled(true);
        btnShare.setEnabled(true);
   //     btnCloud.setEnabled(true);  Enterprise

    }

    public void showSequence0(View view) {
        KeyboardUtils.hideKeyboard(PdfPrint.this);

        disableButtons();
        final Toolbar tb = this.findViewById(R.id.my_toolbar);

        //int toolBarColorValue = Color.parseColor("#FF0288D1");
        //

        new MaterialTapTargetSequence()
                .addPrompt(new MaterialTapTargetPrompt.Builder(PdfPrint.this)
                        .setTarget(tb.getChildAt(2))


                        .setPrimaryText(res.getString(R.string.shield_icon2))
                        .setSecondaryText(res.getString(R.string.got_it))
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                )

                .addPrompt(new MaterialTapTargetPrompt.Builder(PdfPrint.this)
                        .setTarget(btnCreate)
                        

                        .setPrimaryText(res.getString(R.string.about_libs_help))
                        .setSecondaryText(res.getString(R.string.got_it))
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                        .setPromptFocal(new RectanglePromptFocal())
                )
                .addPrompt(new MaterialTapTargetPrompt.Builder(PdfPrint.this)
                        .setTarget(btnHelp)
                        .setPrimaryText(res.getString(R.string.btn_help) + TAG)
                        .setSecondaryText(res.getString(R.string.got_it))
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                        .setPromptFocal(new RectanglePromptFocal()).setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                        {
                            @Override
                            public void onPromptStateChanged(@NonNull MaterialTapTargetPrompt prompt, int state)
                            {
                                if (state != MaterialTapTargetPrompt.STATE_REVEALING && state != MaterialTapTargetPrompt.STATE_REVEALED  && state != MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)

                                {
                                    enableButtons();
                                    //makeTieFocusableTrue();
                                }
                            }
                        })
                )


                .show();
    }
    public void showSequence1(View view) {
        KeyboardUtils.hideKeyboard(PdfPrint.this);

        disableButtons();
        final Toolbar tb = this.findViewById(R.id.my_toolbar);

        //int toolBarColorValue = Color.parseColor("#FF0288D1");
        //

        new MaterialTapTargetSequence()
                .addPrompt(new MaterialTapTargetPrompt.Builder(PdfPrint.this)
                        .setTarget(tb.getChildAt(2))



                        .setPrimaryText(res.getString(R.string.shield_icon2))
                        .setSecondaryText(res.getString(R.string.got_it))
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator()))

                .addPrompt(new MaterialTapTargetPrompt.Builder(PdfPrint.this)
                        .setTarget(btnCreate)
                        

                        .setPrimaryText(res.getString(R.string.btn_create))
                        .setSecondaryText(res.getString(R.string.got_it))
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator()))
                .addPrompt(new MaterialTapTargetPrompt.Builder(PdfPrint.this)
                        .setTarget(btnView)
                        

                        .setPrimaryText(res.getString(R.string.btn_view))
                        .setSecondaryText(res.getString(R.string.got_it))
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator()))
                .addPrompt(new MaterialTapTargetPrompt.Builder(PdfPrint.this)
                        .setTarget(btnShare)
                        

                        .setPrimaryText(res.getString(R.string.btn_drive))
                        .setSecondaryText(res.getString(R.string.got_it))
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator()))

            //    .addPrompt(new MaterialTapTargetPrompt.Builder(PdfPrint.this)
              //          .setTarget(btnCloud)  Enterprise
                        

               //         .setPrimaryText(res.getString(R.string.btn_cloud))
                //        .setSecondaryText(res.getString(R.string.got_it))
                   //     .setAnimationInterpolator(new LinearOutSlowInInterpolator()))

                .addPrompt(new MaterialTapTargetPrompt.Builder(PdfPrint.this)
                        .setTarget(btnHelp)

                        .setPrimaryText(res.getString(R.string.btn_help) + TAG)
                        .setSecondaryText(res.getString(R.string.got_it))
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                        .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                        {
                            @Override
                            public void onPromptStateChanged(@NonNull MaterialTapTargetPrompt prompt, int state)
                            {
                                if (state != MaterialTapTargetPrompt.STATE_REVEALING && state != MaterialTapTargetPrompt.STATE_REVEALED  && state != MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)

                                {
                                    enableButtons();
                                    //makeTieFocusableTrue();
                                }
                            }
                        })
                )


                .show();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        View btnBack = toolbar.getChildAt(2);
        btnBack.clearAnimation();
    }
    private void schedulePdfCreate() {
        Handler handler = new Handler();
        handler.postDelayed(this::PdfCreate, 200);
    }
    private void PdfCreate() {
        
       
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setBackgroundColor(R.color.colorToolbarBlue)
                .setAnimationSpeed(10);
        hud.show();
        	hud.setBackgroundColor(R.color.colorToolbarBlue);
        scheduleDismiss();
    }
  
    private void scheduleviewPdf() {
        Handler handler = new Handler();
        handler.postDelayed(this::viewPdf, 200);
    }
    public void doClose() {
        mInsurancePolicyVDao.closeAll();
        //mDeviceImageStoreDao.closeAll();
        //mPartyTypeDao.closeAll();
        mInvolvedImageStoreDao.closeAll();
        //mVehicleTypeDao.closeAll();
        mAccidentNoteDao.closeAll();
        mInvolvedPartyDao.closeAll();
        //mDeviceUserDao.closeAll();
        mInsurancePolicyPDao.closeAll();
        mInvolvedVehicleDao.closeAll();
        //mAccidentIdDao.closeAll();
        mPersistenceObjDao.closeAll();
        //mPremiumAdvertiserDao.closeAll();
        mInsurancePolicyDao.closeAll();
        //mDeviceVehicleDao.closeAll();
        mVehicleManifestDao.closeAll();

    }
    private void scheduleDoHelp() {
        Handler handler = new Handler();
        handler.postDelayed(this::doHelp, 250);
    }

    private void doHelp() {
        if (totalPages == 0) {
            showSequence0(view);
        } else {
            showSequence1(view);
        }

    }
    @Override
    public void onBackPressed() {

            rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

            rotateAnimation.setDuration(100);
            rotateAnimation.setRepeatCount(1);
            rotateAnimation.setRepeatMode(Animation.RELATIVE_TO_SELF);
            View btnBack = toolbar.getChildAt(2);
            scheduleDismissToolbar();
            btnBack.startAnimation(rotateAnimation);


    }
    private void dismissIntent() {
        doClose();
        this.startActivity(intent);
    }

}