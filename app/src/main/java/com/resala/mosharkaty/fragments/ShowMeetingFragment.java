package com.resala.mosharkaty.fragments;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.LoginActivity.userBranch;
import static com.resala.mosharkaty.SplashActivity.myRules;
import static com.resala.mosharkaty.fragments.AdminShowMosharkatFragment.REQUEST;
import static com.resala.mosharkaty.fragments.AdminShowMosharkatFragment.months;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.resala.mosharkaty.BuildConfig;
import com.resala.mosharkaty.R;
import com.resala.mosharkaty.ui.adapters.MeetingAdapter;
import com.resala.mosharkaty.utility.classes.Meeting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ShowMeetingFragment extends Fragment {
    View view;
    MeetingAdapter adapter;
    ArrayList<Meeting> meetingitems = new ArrayList<>();
    FirebaseDatabase database;
    int month;
    Spinner month_et;
    DatabaseReference MeetingsRef;
    ValueEventListener Meetingslistener;
    Button export_meetings_btn;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_show_meeting, container, false);
        database = FirebaseDatabase.getInstance();
        RecyclerView recyclerView = view.findViewById(R.id.meetingsRecyclerView);
        export_meetings_btn = view.findViewById(R.id.export_meetings_btn);
        month_et = view.findViewById(R.id.month_et);

        final Calendar cldr = Calendar.getInstance(Locale.US);
        month = cldr.get(Calendar.MONTH);
        ArrayAdapter<String> aa =
                new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, months);
        aa.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        month_et.setAdapter(aa);
        month_et.setSelection(Math.max(month, 0));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MeetingAdapter(meetingitems, getContext());
        recyclerView.setAdapter(adapter);

        // by default
        export_meetings_btn.setEnabled(false);
        export_meetings_btn.setBackgroundColor(
                getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));

        ImageButton refreshBtn = view.findViewById(R.id.refresh_btn);
        refreshBtn.setOnClickListener(
                v -> {
                    export_meetings_btn.setEnabled(true);
                    export_meetings_btn.setBackgroundResource(R.drawable.btn_green);
                    MeetingsRef = database.getReference("meetings").child(userBranch);
                    Meetingslistener =
                            MeetingsRef.child(month_et.getSelectedItem().toString())
                                    .addValueEventListener(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    meetingitems.clear();
                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                        Meeting meet = snapshot.getValue(Meeting.class);
                                                        if (meet != null) {
                                                            meet.setKey(snapshot.getKey());
                                                            meetingitems.add(meet);

                                                        } else {
                                                            Toast.makeText(
                                                                    getContext(), "something went wrong", Toast.LENGTH_SHORT)
                                                                    .show();
                                                        }
                                                    }
                                                    Collections.sort(meetingitems);
                                                    adapter.notifyDataSetChanged();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    // Failed to read value
                                                    Log.w(TAG, "Failed to read value.", error.toException());
                                                }
                                            });
                });

        export_meetings_btn.setOnClickListener(
                view -> {
                    if (Build.VERSION.SDK_INT >= 23) {
                        String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        if (!hasPermissions(PERMISSIONS)) {
                            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, REQUEST);
                        } else { // permession already granted
                            exportExcel(month_et.getSelectedItem().toString());
                        }
                    } else { // api below 23
                        exportExcel(month_et.getSelectedItem().toString());
                    }
                });
        return view;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportExcel(month_et.getSelectedItem().toString());

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

    private boolean hasPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && getContext() != null
                && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(getContext(), permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void exportExcel(String month) {
        String root =
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mosharkaty/اجتماعات";
        File dir = new File(root);
        dir.mkdirs();
        String Fnamexls = ("/تقرير_اجتماعات_" + userBranch + "_" + month + ".xls");
        WorkbookSettings wbSettings = new WorkbookSettings();
        WritableWorkbook workbook;
        try {
            File file = new File(dir, Fnamexls);
            workbook = Workbook.createWorkbook(file, wbSettings);
            // workbook.createSheet("Report", 0);
            WritableSheet sheet = workbook.createSheet("شهر " + month, 0);
            Label label0 = new Label(0, 0, "تاريخ الاجتماع");
            Label label1 = new Label(1, 0, "فرعي / مركزي");
            Label label2 = new Label(2, 0, "اللجنة");
            Label label3 = new Label(3, 0, "عدد الحاضرين");
            Label label4 = new Label(4, 0, "هيد الاجتماع");
            Label label5 = new Label(5, 0, "صفة هيد الاجتماع");
            Label label6 = new Label(6, 0, "الهدف من الاجتماع");
            Label label7 = new Label(7, 0, "ملاحظات");

            try {
                sheet.addCell(label7);
                sheet.addCell(label6);
                sheet.addCell(label5);
                sheet.addCell(label4);
                sheet.addCell(label3);
                sheet.addCell(label2);
                sheet.addCell(label1);
                sheet.addCell(label0);
                for (int i = 0; i < meetingitems.size(); i++) {
                    Label label_date = new Label(0, i + 1, meetingitems.get(i).date);
                    Label label_branch = new Label(1, i + 1, userBranch);
                    Label label_type = new Label(2, i + 1, meetingitems.get(i).type);
                    Label label_count = new Label(3, i + 1, meetingitems.get(i).count);
                    Label label_head = new Label(4, i + 1, meetingitems.get(i).head);
                    Label label_head_role = new Label(5, i + 1, meetingitems.get(i).head_role);
                    Label label_reason = new Label(6, i + 1, meetingitems.get(i).reason);
                    Label label_location = new Label(7, i + 1, meetingitems.get(i).location);

                    sheet.addCell(label_type);
                    sheet.addCell(label_date);
                    sheet.addCell(label_head);
                    sheet.addCell(label_head_role);
                    sheet.addCell(label_count);
                    sheet.addCell(label_location);
                    sheet.addCell(label_reason);
                    sheet.addCell(label_branch);
                }
            } catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }

            workbook.write();
            Toast.makeText(getContext(), "تم حفظ الفايل في\n " + root + Fnamexls, Toast.LENGTH_SHORT)
                    .show();
            sendEmail(root, Fnamexls);
            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }
            // createExcel(excelSheet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendEmail(String root, String Fnamexls) {
        File dir = new File(root);
        File file = new File(dir, Fnamexls);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{myRules.supervisor_email});
        //    emailIntent.putExtra(Intent.EXTRA_CC, new String[] {myRules.supervisor_email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, Fnamexls);
        //    emailIntent.putExtra(Intent.EXTRA_TEXT, myRules.daily_email_body);
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            Uri uri =
                    FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", file);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (MeetingsRef != null && Meetingslistener != null) {
            MeetingsRef.removeEventListener(Meetingslistener);
        }
    }
}
