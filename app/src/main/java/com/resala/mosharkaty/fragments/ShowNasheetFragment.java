package com.resala.mosharkaty.fragments;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.fragments.AdminShowMosharkatFragment.REQUEST;
import static com.resala.mosharkaty.utility.classes.UtilityClass.allVolunteersByName;
import static com.resala.mosharkaty.utility.classes.UtilityClass.userBranch;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.resala.mosharkaty.R;
import com.resala.mosharkaty.ui.adapters.UserNasheetHistoryAdapter;
import com.resala.mosharkaty.utility.classes.MosharkaItem;
import com.resala.mosharkaty.utility.classes.NasheetHistoryItem;
import com.resala.mosharkaty.utility.classes.NasheetVolunteer;
import com.resala.mosharkaty.utility.classes.NormalVolunteer;
import com.resala.mosharkaty.utility.classes.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ShowNasheetFragment extends androidx.fragment.app.Fragment {
    View view;
    FirebaseDatabase database;
    int month;
    int year;
    UserNasheetHistoryAdapter adapter;
    ArrayList<String> allNsheet = new ArrayList<>();
    ArrayList<NasheetHistoryItem> userHistoryItems = new ArrayList<>();
    DatabaseReference MosharkatRef;
    ValueEventListener mosharkatlistener;
    DatabaseReference nasheetRef;
    ValueEventListener nasheetlistener;
    Button export_nasheet_btn2;
    HashMap<String, Integer> nasheetMonths = new HashMap<>();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_show_nasheet, container, false);
        database = FirebaseDatabase.getInstance();
        RecyclerView recyclerView = view.findViewById(R.id.nasheetRecyclerView);
        TextView current_month = view.findViewById(R.id.current_month);
        final Calendar cldr = Calendar.getInstance(Locale.US);
        month = cldr.get(Calendar.MONTH) + 1;
        year = cldr.get(Calendar.YEAR);
        current_month.setText(String.valueOf(month));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserNasheetHistoryAdapter(userHistoryItems, getContext());
        recyclerView.setAdapter(adapter);
        export_nasheet_btn2 = view.findViewById(R.id.export_nasheet_btn2);
        nasheetRef = database.getReference("nasheet").child(userBranch);
        nasheetlistener =
                nasheetRef.addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                allNsheet.clear();
                                nasheetMonths = new HashMap<>();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    NasheetVolunteer nasheetVolunteer = snapshot.getValue(NasheetVolunteer.class);
                                    assert nasheetVolunteer != null;
                                    allNsheet.add(snapshot.getKey());
                                    String[] parts = nasheetVolunteer.first_month.split("/", 3);
                                    int months =
                                            (year - Integer.parseInt(parts[1])) * 12
                                                    + (month - Integer.parseInt(parts[0]));
                                    nasheetMonths.put(snapshot.getKey(), months);
                                }
                                getNasheetMosharkat();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });

        export_nasheet_btn2.setOnClickListener(
                view -> {
                    if (Build.VERSION.SDK_INT >= 23) {
                        String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        if (!UtilityClass.hasPermissions(PERMISSIONS, getContext())) {
                            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, REQUEST);
                        } else { // permession already granted
                            showDialog();
                        }
                    } else { // api below 23
                        showDialog();
                    }
                });
        return view;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showDialog();
            } else {
                Toast.makeText(
                                getContext(),
                                "The app was not allowed to write in your storage",
                                Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    private void writeExcel() {
//      String root =
//              Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mosharkaty/النشيط";
//      File dir = new File(root);
        String FolderName = "Mosharkaty/النشيط";
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
        String Fnamexls = ("/قائمة_نشيط_نشاط_الفرز_" + userBranch + ".xls");
        WorkbookSettings wbSettings = new WorkbookSettings();
        WritableWorkbook workbook;
        try {
            File file = new File(dir, Fnamexls);
            workbook = Workbook.createWorkbook(file, wbSettings);
            WritableSheet sheet = workbook.createSheet("sheet", 0);
            Label label0 = new Label(0, 0, "الاسم");
            Label label1 = new Label(1, 0, "نشيط ؟");
            Label label2 = new Label(2, 0, "غير موجود في الشيت");

            try {
                sheet.addCell(label2);
                sheet.addCell(label1);
                sheet.addCell(label0);
                int notFoundCounter = 0;
                for (int i = 0; i < userHistoryItems.size(); i++) {
                    NormalVolunteer vol = allVolunteersByName.get(userHistoryItems.get(i).getUsername());
                    int volID;
                    int colNum = 0;
                    int col2Num = 1;
                    if (vol != null) {
                        volID = vol.id;
                    } else {
                        volID = ++notFoundCounter;
                        colNum = 2;
                        col2Num = 3;
                    }
                    Label label_name = new Label(colNum, volID, userHistoryItems.get(i).getUsername());
                    Label label_nasheet = new Label(col2Num, volID, "نشيط");
                    sheet.addCell(label_name);
                    sheet.addCell(label_nasheet);
                }
            } catch (RowsExceededException e) {
                Toast.makeText(getContext(), "rows exceeding limit error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (WriteException e) {
                //        Toast.makeText(getContext(), "writing error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            workbook.write();
            Toast.makeText(getContext(), "تم حفظ الفايل في\n " + directoryName + FolderName + Fnamexls, Toast.LENGTH_LONG)
                    .show();
            //      sendEmail(root, Fnamexls);
            try {
                workbook.close();
            } catch (WriteException e) {
                //        Toast.makeText(getContext(), "writing error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            // createExcel(excelSheet);
        } catch (IOException e) {
            //      Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void getNasheetMosharkat() {
        MosharkatRef = database.getReference("mosharkat").child(userBranch);
        mosharkatlistener =
                MosharkatRef.child(String.valueOf(month))
                        .addValueEventListener(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        userHistoryItems.clear();
                                        HashMap<String, Integer> nameCounting = new HashMap<>();
                                        HashMap<String, String> nameHistory = new HashMap<>();
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            MosharkaItem mosharka = snapshot.getValue(MosharkaItem.class);
                                            String[] splittedDate;
                                            if (mosharka != null) {
                                                if (!allNsheet.contains(mosharka.getVolname()))
                                                    continue;
                                                splittedDate = mosharka.getMosharkaDate().split("/", 3);
                                                if (nameCounting.containsKey(mosharka.getVolname().trim())) {
                                                    // If char is present in charCountMap,
                                                    // incrementing it's count by 1
                                                    nameCounting.put(
                                                            mosharka.getVolname().trim(),
                                                            nameCounting.get(mosharka.getVolname().trim()) + 1);
                                                    nameHistory.put(
                                                            mosharka.getVolname().trim(),
                                                            nameHistory.get(mosharka.getVolname().trim())
                                                                    + ","
                                                                    + splittedDate[0]);
                                                } else {
                                                    // If char is not present in charCountMap,
                                                    // putting this char to charCountMap with 1 as it's value
                                                    nameCounting.put(mosharka.getVolname().trim(), 1);
                                                    nameHistory.put(mosharka.getVolname().trim(), splittedDate[0]);
                                                }
                                            }
                                        }
                                        for (Map.Entry<String, Integer> entry : nameCounting.entrySet()) {
                                            allNsheet.remove(entry.getKey());
                                            userHistoryItems.add(
                                                    new NasheetHistoryItem(
                                                            entry.getKey(),
                                                            nameHistory.get(entry.getKey()),
                                                            entry.getValue(),
                                                            nasheetMonths.get(entry.getKey())));
                                        }
                                        for (int i = 0; i < allNsheet.size(); i++) {
                                            userHistoryItems.add(
                                                    new NasheetHistoryItem(
                                                            allNsheet.get(i), "", 0, nasheetMonths.get(allNsheet.get(i))));
                                        }
                                        Collections.sort(userHistoryItems);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Failed to read value
                                        Log.w(TAG, "Failed to read value.", error.toException());
                                    }
                                });
    }

    private void showDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        alertDialogBuilder.setTitle(getString(R.string.chooseExcelForm));
        //    alertDialogBuilder.setMessage(
        //            getString(R.string.youAreNotUpdatedMessage)
        //                    + " "
        //                    + myRules.last_important_update
        //                    + getString(R.string.youAreNotUpdatedMessage1));
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton(
                R.string.nasheetColumn,
                (dialog, id) -> {
                    writeExcel();
                    dialog.cancel();
                });
        alertDialogBuilder.setNeutralButton(
                R.string.nasheetStats,
                (dialog, id) -> {
                    writeExcel2();
                    dialog.cancel();
                });
        alertDialogBuilder.show();
    }

    private void writeExcel2() {
//        String root =
//                Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mosharkaty/النشيط";
//        File dir = new File(root);
        String FolderName = "Mosharkaty/النشيط";
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
        String Fnamexls = ("/مشاركات_نشيط_حتي_الان_" + userBranch + ".xls");
        WorkbookSettings wbSettings = new WorkbookSettings();
        WritableWorkbook workbook;
        try {
            File file = new File(dir, Fnamexls);
            workbook = Workbook.createWorkbook(file, wbSettings);
            WritableSheet sheet = workbook.createSheet("sheet", 0);
            Label label0 = new Label(0, 0, "الاسم");
            Label label1 = new Label(1, 0, "عدد المشاركات");

            try {
                sheet.addCell(label1);
                sheet.addCell(label0);
                for (int i = 0; i < userHistoryItems.size(); i++) {
                    Label label_name = new Label(0, i + 1, userHistoryItems.get(i).getUsername());
                    Label label_nasheet =
                            new Label(1, i + 1, String.valueOf(userHistoryItems.get(i).getCount()));
                    sheet.addCell(label_name);
                    sheet.addCell(label_nasheet);
                }
            } catch (RowsExceededException e) {
                Toast.makeText(getContext(), "rows exceeding limit error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (WriteException e) {
                //        Toast.makeText(getContext(), "writing error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            workbook.write();
            Toast.makeText(getContext(), "تم حفظ الفايل في\n " + directoryName + FolderName + Fnamexls, Toast.LENGTH_LONG)
                    .show();
            //      sendEmail(root, Fnamexls);
            try {
                workbook.close();
            } catch (WriteException e) {
                //        Toast.makeText(getContext(), "writing error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            // createExcel(excelSheet);
        } catch (IOException e) {
            //      Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (MosharkatRef != null && mosharkatlistener != null) {
            MosharkatRef.removeEventListener(mosharkatlistener);
        }
        if (nasheetRef != null && nasheetlistener != null) {
            nasheetRef.removeEventListener(nasheetlistener);
        }
    }
}
