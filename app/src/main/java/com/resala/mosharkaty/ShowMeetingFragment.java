package com.resala.mosharkaty;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.LoginActivity.userBranch;
import static com.resala.mosharkaty.Starter.myRules;

public class ShowMeetingFragment extends Fragment {
    View view;
    MeetingAdapter adapter;
    ArrayList<Meeting> meetingitems = new ArrayList<>();
    FirebaseDatabase database;
    int month;
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

        TextView current_month = view.findViewById(R.id.current_month);
        final Calendar cldr = Calendar.getInstance(Locale.US);
        month = cldr.get(Calendar.MONTH) + 1;
        current_month.setText(String.valueOf(month));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MeetingAdapter(meetingitems, getContext());
        recyclerView.setAdapter(adapter);
        MeetingsRef = database.getReference("meetings").child(userBranch);
        Meetingslistener =
                MeetingsRef.child(String.valueOf(month))
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
                                                Toast.makeText(getContext(), "something went wrong", Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        }
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Failed to read value
                                        Log.w(TAG, "Failed to read value.", error.toException());
                                    }
                                });

        export_meetings_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        exportExcel();
                    }
                });
        return view;
    }

    private void exportExcel() {
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
            Label label5 = new Label(5, 0, "الهدف من الاجتماع");
            Label label6 = new Label(6, 0, "ملاحظات");

            try {
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
                    Label label_reason = new Label(5, i + 1, meetingitems.get(i).reason);
                    Label label_location = new Label(6, i + 1, meetingitems.get(i).location);

                    sheet.addCell(label_type);
                    sheet.addCell(label_date);
                    sheet.addCell(label_head);
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
