package com.resala.mosharkaty;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.LoginActivity.allVolunteersByName;
import static com.resala.mosharkaty.LoginActivity.userBranch;

public class ShowNasheetFragment extends androidx.fragment.app.Fragment {
  View view;
  FirebaseDatabase database;
  int month;
  UserNasheetHistoryAdapter adapter;
  ArrayList<String> allNsheet = new ArrayList<>();
  ArrayList<UserHistoryItem> userHistoryItems = new ArrayList<>();
  DatabaseReference MosharkatRef;
  ValueEventListener mosharkatlistener;
  DatabaseReference nasheetRef;
  ValueEventListener nasheetlistener;
  Button export_nasheet_btn2;

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
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                          allNsheet.add(snapshot.getKey());
                        }
                        getNasheetMosharkat();
                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                      }
                    });

    export_nasheet_btn2.setOnClickListener(view -> writeExcel());
    return view;
  }

  private void writeExcel() {
    String root =
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mosharkaty/شيت_المتابعة";
    File dir = new File(root);
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

      try {
        sheet.addCell(label1);
        sheet.addCell(label0);
        for (int i = 0; i < allNsheet.size(); i++) {
          normalVolunteer vol = allVolunteersByName.get(allNsheet.get(i));
          assert vol != null;
          Label label_name = new Label(0, vol.id + 1, allNsheet.get(i));
          Label label_nasheet = new Label(1, vol.id + 1, "نشيط");
          sheet.addCell(label_name);
          sheet.addCell(label_nasheet);
        }
      } catch (RowsExceededException e) {
//        Toast.makeText(getContext(), "rows exceeding limit error", Toast.LENGTH_SHORT).show();
        e.printStackTrace();
      } catch (WriteException e) {
//        Toast.makeText(getContext(), "writing error", Toast.LENGTH_SHORT).show();
        e.printStackTrace();
      }

      workbook.write();
      Toast.makeText(getContext(), "تم حفظ الفايل في\n " + root + Fnamexls, Toast.LENGTH_LONG)
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
                                    if (!allNsheet.contains(mosharka.getVolname())) continue;
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
                                for (Map.Entry entry : nameCounting.entrySet()) {
                                  allNsheet.remove(entry.getKey().toString());
                                  userHistoryItems.add(
                                          new UserHistoryItem(
                                                  entry.getKey().toString(),
                                                  nameHistory.get(entry.getKey().toString()),
                                                  Integer.parseInt(entry.getValue().toString())));
                                  //                      System.out.println(entry.getKey() + " " +
                                  // entry.getValue());
                                }
                                for (int i = 0; i < allNsheet.size(); i++) {
                                  userHistoryItems.add(new UserHistoryItem(allNsheet.get(i), "", 0));
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
