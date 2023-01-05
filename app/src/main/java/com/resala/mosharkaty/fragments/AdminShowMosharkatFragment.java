package com.resala.mosharkaty.fragments;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.utility.classes.UtilityClass.allVolunteersByName;
import static com.resala.mosharkaty.utility.classes.UtilityClass.days;
import static com.resala.mosharkaty.utility.classes.UtilityClass.months;
import static com.resala.mosharkaty.utility.classes.UtilityClass.myRules;
import static com.resala.mosharkaty.utility.classes.UtilityClass.userBranch;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opencsv.CSVWriter;
import com.resala.mosharkaty.BuildConfig;
import com.resala.mosharkaty.R;
import com.resala.mosharkaty.ui.adapters.MosharkatAdapter;
import com.resala.mosharkaty.utility.classes.MosharkaItem;
import com.resala.mosharkaty.utility.classes.NormalVolunteer;
import com.resala.mosharkaty.utility.classes.UtilityClass;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class AdminShowMosharkatFragment extends androidx.fragment.app.Fragment {
    View view;
    MosharkatAdapter adapter;
    ArrayList<MosharkaItem> mosharkaItems = new ArrayList<>();
    FirebaseDatabase database;
    int day;
    int month;
    int year;
    public static final int REQUEST = 112;
    Spinner month_et;
    Spinner day_et;

    /**
     * Called to have the fragment instantiate its user interface view. This is optional, and
     * non-graphical fragments can return null. This will be called between {@link #onCreate(Bundle)}
     * and {@link #onActivityCreated(Bundle)}.
     *
     * <p>It is recommended to <strong>only</strong> inflate the layout in this method and move logic
     * that operates on the returned View to {@link #onViewCreated(View, Bundle)}.
     *
     * <p>If you return a View from here, you will later be called in {@link #onDestroyView} when the
     * view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the
     *                           fragment,
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached
     *                           to. The fragment should not add the view itself, but this can be used to generate the
     *                           LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin_show_mosharkat, container, false);
        database = FirebaseDatabase.getInstance();
        final DatabaseReference MosharkatRef = database.getReference("mosharkat").child(userBranch);
        final DatabaseReference ClosingRef = database.getReference("closings").child(userBranch);
        RecyclerView recyclerView = view.findViewById(R.id.mosharkatRecyclerView);
        ImageButton refreshBtn = view.findViewById(R.id.refresh_btn);
        final Button close_day_btn = view.findViewById(R.id.close_day_btn);
        final Button daily_report_btn = view.findViewById(R.id.daily_report_btn);
        // by default
        daily_report_btn.setEnabled(false);
        daily_report_btn.setBackgroundColor(
                getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));

        month_et = view.findViewById(R.id.month_et);
        day_et = view.findViewById(R.id.day_et);
        final Calendar cldr = Calendar.getInstance(Locale.US);
        day = cldr.get(Calendar.DAY_OF_MONTH);
        month = cldr.get(Calendar.MONTH);
        year = cldr.get(Calendar.YEAR);

        // setting spinner
        ArrayAdapter<String> aa =
                new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, months);
        aa.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        month_et.setAdapter(aa);
        month_et.setSelection(Math.max(month, 0));

        ArrayAdapter<String> ab =
                new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, days);
        ab.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        day_et.setAdapter(ab);
        day_et.setSelection(Math.max(day - 1, 0));

        final TextView count = view.findViewById(R.id.mosharkatMonthCount);

        recyclerView.setHasFixedSize(true);
        //    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        //    mLayoutManager.setReverseLayout(true);
        //    mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MosharkatAdapter(mosharkaItems, getContext());
        recyclerView.setAdapter(adapter);

        // button listener
        close_day_btn.setOnClickListener(
                v -> {
                    final int month = Integer.parseInt(month_et.getSelectedItem().toString());
                    final int day = Integer.parseInt(day_et.getSelectedItem().toString());
                    ClosingRef.child(String.valueOf(month)).child(String.valueOf(day)).setValue(1);
                    close_day_btn.setText(R.string.day_already_closed);
                    close_day_btn.setEnabled(false);
                    close_day_btn.setBackgroundColor(
                            getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
                    close_day_btn.setTextColor(getResources().getColor(R.color.new_text_black));
                });

        daily_report_btn.setOnClickListener(
                v -> {
                    final int month = Integer.parseInt(month_et.getSelectedItem().toString());
                    final int day = Integer.parseInt(day_et.getSelectedItem().toString());
                    if (Build.VERSION.SDK_INT >= 23) {
                        String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        if (!UtilityClass.hasPermissions(PERMISSIONS, getContext())) {
                            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, REQUEST);
                        } else { // permession already granted
                            //              writeCSV(month, day);
                            showDialog(month, day);
                            //              writeExcel(month, day);
                        }
                    } else { // api below 23
                        //            writeCSV(month, day);
                        showDialog(month, day);
                    }
                });

        refreshBtn.setOnClickListener(
                v -> {
                    daily_report_btn.setEnabled(true);
                    daily_report_btn.setBackgroundResource(R.drawable.btn_green);

                    close_day_btn.setEnabled(true);
                    close_day_btn.setBackgroundResource(R.drawable.btn_gradient_blue);
                    close_day_btn.setText(R.string.close_btn_txt);
                    final int month = Integer.parseInt(month_et.getSelectedItem().toString());
                    final int day = Integer.parseInt(day_et.getSelectedItem().toString());

                    MosharkatRef.child(String.valueOf(month))
                            .addValueEventListener(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            mosharkaItems.clear();
                                            int counter = 0;
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                MosharkaItem mosharka = snapshot.getValue(MosharkaItem.class);
                                                String[] splittedDate;
                                                if (mosharka != null) {
                                                    splittedDate = mosharka.getMosharkaDate().split("/", 3);
                                                    if (Integer.parseInt(splittedDate[0]) == day) {
                                                        mosharka.setKey(snapshot.getKey());
                                                        mosharkaItems.add(mosharka);
                                                        counter++;
                                                    }
                                                } else {
                                                    Toast.makeText(getContext(), "something went wrong", Toast.LENGTH_SHORT)
                                                            .show();
                                                }
                                            }
                                            Collections.sort(mosharkaItems);
                                            adapter.notifyDataSetChanged();
                                            count.setText(String.valueOf(counter));
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            // Failed to read value
                                            Log.w(TAG, "Failed to read value.", error.toException());
                                        }
                                    });

                    ClosingRef.addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(String.valueOf(month))) {
                                        ClosingRef.child(String.valueOf(month))
                                                .addValueEventListener(
                                                        new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.hasChild(String.valueOf(day))) {
                                                                    int isClosed =
                                                                            dataSnapshot
                                                                                    .child(String.valueOf(day))
                                                                                    .getValue(Integer.class);
                                                                    if (isClosed == 1) {
                                                                        close_day_btn.setText("اليوم مقفول بالفعل");
                                                                        close_day_btn.setEnabled(false);
                                                                        close_day_btn.setBackgroundResource(R.drawable.border_only_btn);
                                                                    }
                                                                } else {
                                                                    close_day_btn.setEnabled(true);
                                                                    close_day_btn.setBackgroundResource(R.drawable.btn_gradient_blue);
                                                                    close_day_btn.setText(R.string.close_btn_txt);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                                // Failed to read value
                                                                Log.w(TAG, "Failed to read value.", error.toException());
                                                            }
                                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Failed to read value
                                    Log.w(TAG, "Failed to read value.", error.toException());
                                }
                            });
                });
        return view;
    }

    private void showDialog(int month, int day) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        alertDialogBuilder.setTitle(getString(R.string.chooseExcelForm));
        //    alertDialogBuilder.setMessage(
        //            getString(R.string.youAreNotUpdatedMessage)
        //                    + " "
        //                    + myRules.last_important_update
        //                    + getString(R.string.youAreNotUpdatedMessage1));
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton(
                R.string.dailyReport,
                (dialog, id) -> {
                    writeExcel(month, day);
                    dialog.cancel();
                });
        alertDialogBuilder.setNeutralButton(
                R.string.motab3asheet,
                (dialog, id) -> {
                    writeExcel2(month, day);
                    dialog.cancel();
                });
        alertDialogBuilder.show();
    }

    private void writeCSV(int month, int day) {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mosharkaty/csv";
        File dir = new File(root);
        dir.mkdirs();
        String csv = ("/متابعة_يومية_" + userBranch + "_" + day + "_" + month + ".csv");
        CSVWriter writer;

        try {
            File file = new File(dir, csv);
            writer = new CSVWriter(new FileWriter(file));

            ArrayList<String[]> data = new ArrayList<>();
            data.add(new String[]{"الاسم", "الرقم", "النوع"});
            for (int i = 0; i < mosharkaItems.size(); i++) {
                data.add(
                        new String[]{
                                mosharkaItems.get(i).getVolname(), " ", mosharkaItems.get(i).getMosharkaType()
                        });
            }

            writer.writeAll(data); // data is adding to csv
            //      Toast.makeText(getContext(), "تم حفظ الفايل في\n " + root + csv,
            // Toast.LENGTH_SHORT).show();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeExcel(int month, int day) {
        ProgressDialog progress = new ProgressDialog(getContext());
        progress.setTitle("Loading");
        progress.setMessage("لحظات معانا...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
//    String root =
//            Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mosharkaty/متابعة_يومية";
//    File dir = new File(root);
        String directoryName;
        String FolderName = "Mosharkaty/شيت_المتابعة اليومية";
        File dir;
        directoryName = Objects.requireNonNull(requireContext().getExternalFilesDir(null)).toString();
        dir = new File(requireContext().getExternalFilesDir(null) + "/" + FolderName);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            directoryName = Environment.getDownloadCacheDirectory().toString();
//            dir = new File(Environment.getDownloadCacheDirectory() + "/" + FolderName);
//
////            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + FolderName);
//        } else {
//            directoryName = Environment.getExternalStorageDirectory().toString();
//
//            dir = new File(Environment.getExternalStorageDirectory() + "/" + FolderName);
//        }

        dir.mkdirs();
        String Fnamexls = ("/متابعة_يومية_نشاط_الفرز_" + userBranch + "_" + day + "_" + month + ".xls");
        WorkbookSettings wbSettings = new WorkbookSettings();
        WritableWorkbook workbook;
        try {
            File file = new File(dir, Fnamexls);
            workbook = Workbook.createWorkbook(file, wbSettings);
            // workbook.createSheet("Report", 0);
            WritableSheet sheet = workbook.createSheet(day + "_" + month, 0);
            Label label0 = new Label(0, 0, "الاسم");
            Label label1 = new Label(1, 0, "الرقم");
            Label label2 = new Label(2, 0, "النوع");

            try {
                sheet.addCell(label2);
                sheet.addCell(label1);
                sheet.addCell(label0);
                for (int i = 0; i < mosharkaItems.size(); i++) {
                    Label label_name = new Label(0, i + 1, mosharkaItems.get(i).getVolname());
                    String phone =
                            allVolunteersByName.get(mosharkaItems.get(i).getVolname()) == null
                                    ? ""
                                    : allVolunteersByName.get(mosharkaItems.get(i).getVolname()).phone_text;
                    Label label_num = new Label(1, i + 1, phone);
                    Label label_type = new Label(2, i + 1, mosharkaItems.get(i).getMosharkaType());
                    sheet.addCell(label_name);
                    sheet.addCell(label_num);
                    sheet.addCell(label_type);
                }
            } catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }

            workbook.write();
            Toast.makeText(getContext(), "تم حفظ الفايل في\n " + directoryName + FolderName + Fnamexls, Toast.LENGTH_LONG)
                    .show();
            // To dismiss the dialog
            progress.dismiss();
            sendEmail(FolderName, Fnamexls);
            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }
            // createExcel(excelSheet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // To dismiss the dialog
        progress.dismiss();
    }

    private void writeExcel2(int month, int day) {
        ProgressDialog progress = new ProgressDialog(getContext());
        progress.setTitle("Loading");
        progress.setMessage("لحظات معانا...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        String FolderName = "Mosharkaty/شيت_المتابعة";
        String directoryName;
        File dir;
        directoryName = Objects.requireNonNull(requireContext().getExternalFilesDir(null)).toString();
        dir = new File(requireContext().getExternalFilesDir(null) + "/" + FolderName);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            directoryName = Environment.getDownloadCacheDirectory().toString();
//            dir = new File(Environment.getDownloadCacheDirectory() + "/" + FolderName);
//
////            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + FolderName);
//        } else {
//            directoryName = Environment.getExternalStorageDirectory().toString();
//
//            dir = new File(Environment.getExternalStorageDirectory() + "/" + FolderName);
//        }
        dir.mkdirs();
        String Fnamexls = ("/شيت_متابعة_نشاط_الفرز_" + userBranch + "_" + day + "_" + month + ".xls");
        WorkbookSettings wbSettings = new WorkbookSettings();
        WritableWorkbook workbook;
        try {
            File file = new File(dir, Fnamexls);
            workbook = Workbook.createWorkbook(file, wbSettings);
            WritableSheet sheet = workbook.createSheet(day + "_" + month, 0);
            Label label0 = new Label(0, 0, "الاسم");
            Label label1 = new Label(1, 0, String.valueOf(day));
            Label label2 = new Label(2, 0, "غير موجود في الشيت");

            try {
                sheet.addCell(label2);
                sheet.addCell(label1);
                sheet.addCell(label0);
                int notFoundCounter = 0;
                for (int i = 0; i < mosharkaItems.size(); i++) {
                    NormalVolunteer vol = allVolunteersByName.get(mosharkaItems.get(i).getVolname());
                    int colNum = 0;
                    int col2Num = 1;
                    int volID;
                    if (vol != null) {
                        volID = vol.id;
                    } else {
                        colNum = 2;
                        col2Num = 3;
                        volID = ++notFoundCounter;
                    }
                    Label label_name = new Label(colNum, volID, mosharkaItems.get(i).getVolname());
                    String type =
                            mosharkaItems.get(i).getMosharkaType().matches("(.*)بيت(.*)")
                                    ? myRules.from_home
                                    : myRules.from_far3;
                    Label label_type = new Label(col2Num, volID, type);
                    sheet.addCell(label_name);
                    sheet.addCell(label_type);
                }
            } catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }

            workbook.write();
            Toast.makeText(getContext(), "تم حفظ الفايل في\n " + directoryName + FolderName + Fnamexls, Toast.LENGTH_LONG)
                    .show();
            // To dismiss the dialog
            progress.dismiss();
            //      sendEmail(root, Fnamexls);
            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }
            // createExcel(excelSheet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // To dismiss the dialog
        progress.dismiss();
    }

    private void sendEmail(String root, String Fnamexls) {
        File dir = new File(root);
        File file = new File(dir, Fnamexls);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{myRules.vol_mohndseen});
        emailIntent.putExtra(Intent.EXTRA_CC, new String[]{myRules.supervisor_email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, Fnamexls);
        emailIntent.putExtra(Intent.EXTRA_TEXT, myRules.daily_email_body);
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            Uri uri =
                    FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".provider", file);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //            writeCSV(month, day);
                    final int month = Integer.parseInt(month_et.getSelectedItem().toString());
                    final int day = Integer.parseInt(day_et.getSelectedItem().toString());
                    showDialog(month, day);
                } else {
                    Toast.makeText(
                                    getContext(),
                                    "The app was not allowed to write in your storage",
                                    Toast.LENGTH_LONG)
                            .show();
                }
            }
        }
    }
}
