package com.resala.mosharkaty.fragments;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.fragments.AddEventReportFragment.reportsTypes;
import static com.resala.mosharkaty.fragments.TakyeemFragment.codeFound;
import static com.resala.mosharkaty.utility.classes.UtilityClass.BRANCHES_COUNT;
import static com.resala.mosharkaty.utility.classes.UtilityClass.branches;
import static com.resala.mosharkaty.utility.classes.UtilityClass.branchesSheets;
import static com.resala.mosharkaty.utility.classes.UtilityClass.days;
import static com.resala.mosharkaty.utility.classes.UtilityClass.isAdmin;
import static com.resala.mosharkaty.utility.classes.UtilityClass.mosharkaTypes;
import static com.resala.mosharkaty.utility.classes.UtilityClass.myRules;
import static com.resala.mosharkaty.utility.classes.UtilityClass.userBranch;
import static com.resala.mosharkaty.utility.classes.UtilityClass.userId;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.resala.mosharkaty.R;
import com.resala.mosharkaty.ui.adapters.Top5Adapter;
import com.resala.mosharkaty.utility.classes.EventReport;
import com.resala.mosharkaty.utility.classes.MosharkaItem;
import com.resala.mosharkaty.utility.classes.NormalVolunteer;
import com.resala.mosharkaty.utility.classes.Top5Item;
import com.resala.mosharkaty.utility.classes.User;
import com.resala.mosharkaty.utility.classes.UtilityClass;
import com.resala.mosharkaty.utility.classes.Volunteer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends androidx.fragment.app.Fragment {
    public static String userName;
    public static String userCode;
    public static String userOfficialName;
    View view;
    TextView branchTV;
    FirebaseDatabase database;
    ValueEventListener userListener;
    ValueEventListener mosharkatlistener;

    DatabaseReference usersRef;
    DatabaseReference mosharkatTab;

    DatabaseReference appMosharkatRef;
    DatabaseReference meetingsRef;

    ValueEventListener appMosharkatlistener;
    ValueEventListener meetingsListener;

    DatabaseReference ReportsRef;
    ValueEventListener ReportsListener;

    DatabaseReference liveSheet;
    public static HashMap<String, String> teamDegrees = new HashMap<>();
    public static ArrayList<String> globalFari2Names = new ArrayList<>();
    private ProgressDialog progress;
    int arrivedCounter;
    int mas2oleenCounter;
    int msharee3Counter;
    int month;
    int week;
    int day;
    int mas2oleenMosharkat;
    int msharee3Mosharkat;
    int allMosharkat;
    int noobsMosharkat;
    int da5el_2_Mosharkat;
    int da5el_aboveMosharkat;
    int otherMosharkat;
    TextView percent;
    TextView totalTeam;
    TextView arrivedTeam;
    TextView points_fari2;
    TextView points_other;
    TextView points_word;
    TextView points_word2;
    TextView meetings;
    TextView meetings_word;
    TextView average_fari2;
    TextView average_mas2oleen;
    TextView average_msharee3;
    TextView totalMosharkatCount;
    TextView totslReportsCount;

    ProgressBar attendanceBar;
    ArrayList<Top5Item> top5Items = new ArrayList<>();
    Top5Adapter adapter;
    HashMap<String, Integer> mosharkatTypesCounter = new HashMap<>();

    ArrayList<Top5Item> top5Items2 = new ArrayList<>();
    Top5Adapter adapter2;
    HashMap<String, Integer> mosharkatDaysCounter = new HashMap<>();

    ArrayList<Top5Item> top5Items3 = new ArrayList<>();
    Top5Adapter adapter3;
    HashMap<String, Integer> eventsTypeCounter = new HashMap<>();

    /**
     * Called when the fragment is visible to the user and actively running.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (userBranch != null) branchTV.setText(userBranch);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        database = FirebaseDatabase.getInstance();

        branchTV = view.findViewById(R.id.branch);

        RecyclerView recyclerView = view.findViewById(R.id.top5TypesRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Top5Adapter(top5Items, getContext());
        recyclerView.setAdapter(adapter);

        RecyclerView recyclerView2 = view.findViewById(R.id.top5DaysRecyclerView);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter2 = new Top5Adapter(top5Items2, getContext());
        recyclerView2.setAdapter(adapter2);

        RecyclerView recyclerView3 = view.findViewById(R.id.eventsReportsRecyclerView);
        recyclerView3.setHasFixedSize(true);
        recyclerView3.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter3 = new Top5Adapter(top5Items3, getContext());
        recyclerView3.setAdapter(adapter3);

        if (!isAdmin) {
            progress = new ProgressDialog(getContext());
            progress.setTitle("Loading");
            progress.setMessage("لحظات معانا...");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();
            usersRef = database.getReference("users").child(userId);

            userListener =
                    usersRef.addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    if (user != null) {
                                        userName = user.name;
                                        userCode = user.code;
                                        userBranch = user.branch;

                                        branchTV.setText(userBranch);
                                        String branchSheetLink =
                                                userBranch.equals(branches[BRANCHES_COUNT])
                                                        ? branchesSheets.get(branches[0])
                                                        : branchesSheets.get(userBranch);
                                        assert branchSheetLink != null;
                                        liveSheet = !myRules.useOnlineSheets ? database.getReference(branchSheetLink) : database.getReference("sheets").child(userBranch);
//                                        liveSheet = database.getReference(branchSheetLink);
                                        getUserName();
                                        refreshReports();
                                    }
                                    progress.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Failed to read value
                                    Log.w(TAG, "Failed to read value.", error.toException());
                                }
                            });

        } else {
            String branchSheetLink =
                    userBranch.equals(branches[BRANCHES_COUNT])
                            ? branchesSheets.get(branches[0])
                            : branchesSheets.get(userBranch);
            assert branchSheetLink != null;
            liveSheet = !myRules.useOnlineSheets ? database.getReference(branchSheetLink) : database.getReference("sheets").child(userBranch);
//            liveSheet = database.getReference(branchSheetLink);
            branchTV.setText(userBranch);
            refreshReports();
        }
        return view;
    }

    private void getUserName() {
        mosharkatTab = liveSheet.child("month_mosharkat");
        mosharkatlistener =
                mosharkatTab.addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                codeFound = false;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Volunteer user = snapshot.getValue(Volunteer.class);
                                    if (user != null && user.code.equalsIgnoreCase(userCode)) {
                                        userOfficialName = user.Volname;
                                        codeFound = true;
                                        break;
                                    }
                                }
                                if (!codeFound) userOfficialName = "";
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
    }

    private void refreshReports() {
        average_fari2 = view.findViewById(R.id.average_fari2);
        average_mas2oleen = view.findViewById(R.id.average_mas2oleen);
        average_msharee3 = view.findViewById(R.id.average_msharee3);
        points_fari2 = view.findViewById(R.id.points_fari2);
        points_other = view.findViewById(R.id.points_other);
        meetings = view.findViewById(R.id.meetings);
        meetings_word = view.findViewById(R.id.meetings_word);
        percent = view.findViewById(R.id.percent);
        totalTeam = view.findViewById(R.id.totalTeam);
        arrivedTeam = view.findViewById(R.id.arrivedTeam);
        totalMosharkatCount = view.findViewById(R.id.totslMosharkatCount);
        totslReportsCount = view.findViewById(R.id.totslReportsCount);

        attendanceBar = view.findViewById(R.id.determinateBar);
        mosharkatTab = liveSheet.child("all");
        mosharkatTab.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mas2oleenCounter = 0;
                        msharee3Counter = 0;
                        globalFari2Names.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            NormalVolunteer user = snapshot.getValue(NormalVolunteer.class);
                            if (user != null) {
                                Log.d(TAG, "onDataChange: fetched  user # " + user.id + " with name : " + user.Volname + " and motabaa : " + user.motabaa);
                                if (user.motabaa.equals("مشروع مسئول")) {
                                    teamDegrees.put(user.Volname, "مشروع");
                                    globalFari2Names.add(user.Volname);
                                    msharee3Counter++;
                                } else if (user.motabaa.equals("مسئول")) {
                                    mas2oleenCounter++;
                                    teamDegrees.put(user.Volname, "مسؤول");
                                    globalFari2Names.add(user.Volname);
                                } else if (user.motabaa.contains("داخل")) {
                                    teamDegrees.put(user.Volname, "داخل" + "&" + user.months_count);
                                }
                            }
                        }
                        Collections.sort(globalFari2Names);
                        totalTeam.setText(String.valueOf(globalFari2Names.size()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });

        final Calendar cldr = Calendar.getInstance(Locale.US);
        month = cldr.get(Calendar.MONTH) + 1;
        week = cldr.get(Calendar.WEEK_OF_MONTH);
        day = cldr.get(Calendar.DAY_OF_MONTH);
        updateMeetings();
        updateEventsReports();
        appMosharkatRef = database.getReference("mosharkat").child(userBranch);
        appMosharkatlistener =
                appMosharkatRef
                        .child(String.valueOf(month))
                        .addValueEventListener(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        HashMap<String, Integer> nameCounting = new HashMap<>();
                                        allMosharkat = 0;
                                        mas2oleenMosharkat = 0;
                                        msharee3Mosharkat = 0;
                                        arrivedCounter = 0;
                                        da5el_2_Mosharkat = 0;
                                        da5el_aboveMosharkat = 0;
                                        noobsMosharkat = 0;
                                        otherMosharkat = 0;
                                        mosharkatTypesCounter = new HashMap<>();
                                        mosharkatDaysCounter = new HashMap<>();
                                        top5Items.clear();
                                        top5Items2.clear();

                                        for (String mosharkaType : mosharkaTypes) {
                                            // initialize map
                                            mosharkatTypesCounter.put(mosharkaType, 0);
                                        }
                                        for (String day : days) {
                                            // initialize map
                                            mosharkatDaysCounter.put(day, 0);
                                        }
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            MosharkaItem mosharka = snapshot.getValue(MosharkaItem.class);

                                            if (mosharka != null) {
                                                allMosharkat++;
                                                try {
                                                    mosharkatTypesCounter.put(
                                                            mosharka.getMosharkaType(),
                                                            mosharkatTypesCounter.get(mosharka.getMosharkaType()) + 1);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                try {
                                                    String[] splittedDate = mosharka.getMosharkaDate().split("/", 3);
                                                    mosharkatDaysCounter.put(
                                                            splittedDate[0], mosharkatDaysCounter.get(splittedDate[0]) + 1);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                if (nameCounting.containsKey(mosharka.getVolname().trim())) {
                                                    nameCounting.put(
                                                            mosharka.getVolname().trim(),
                                                            Math.min(nameCounting.get(mosharka.getVolname().trim()) + 1, 8));
                                                } else {
                                                    nameCounting.put(mosharka.getVolname().trim(), 1);
                                                }
                                            }
                                        }
                                        Log.d("Home", "nameCounting size " + nameCounting.size());
                                        Log.d("Home", "teamDegrees size " + teamDegrees.size());

                                        for (Map.Entry<String, Integer> entry : nameCounting.entrySet()) {
                                            String degree = teamDegrees.get(entry.getKey());
                                            if (teamDegrees.containsKey(entry.getKey())) {
                                                assert degree != null;
                                                if (degree.equals("مسؤول")) {
                                                    mas2oleenMosharkat += entry.getValue();
                                                    arrivedCounter++;
                                                } else if (degree.equals("مشروع")) {
                                                    msharee3Mosharkat += entry.getValue();
                                                    arrivedCounter++;
                                                } else if (degree.contains("داخل")) {
                                                    String[] splittedDegree = degree.split("&", 2);
                                                    switch (Integer.parseInt(splittedDegree[1])) {
                                                        case 0:
                                                            noobsMosharkat += entry.getValue();
                                                            break;
                                                        case 1:
                                                            da5el_2_Mosharkat += entry.getValue();
                                                            break;
                                                        default:
                                                            if (Integer.parseInt(splittedDegree[1]) >= 8 && myRules.ignoreAbove)
                                                                break;
                                                            da5el_aboveMosharkat += entry.getValue();
                                                            break;
                                                    }
                                                }
                                            } else { //خارج متابعة
                                                otherMosharkat += entry.getValue();
                                            }
                                        }
                                        updateAttendance();
                                        updatePoints();
                                        updateAverages();
                                        updateTopTypes();
                                        updateTopDays();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Failed to read value
                                        Log.w(TAG, "Failed to read value.", error.toException());
                                    }
                                });
    }

    private void updateMeetings() {
        meetingsRef = database.getReference("meetings").child(userBranch);
        meetingsListener =
                meetingsRef
                        .child(String.valueOf(month))
                        .addValueEventListener(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int meetingsCount = (int) dataSnapshot.getChildrenCount();
                                        meetings.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                                        try {
                                            if (meetingsCount < (week * myRules.meetings_bad)) {
                                                meetings.setTextColor(requireActivity().getResources().getColor(R.color.red));
                                                meetings_word.setTextColor(
                                                        requireActivity().getResources().getColor(R.color.red));
                                            } else if (meetingsCount < (week * myRules.meetings_medium)) {
                                                meetings.setTextColor(
                                                        requireActivity().getResources().getColor(R.color.ourBlue));
                                                meetings_word.setTextColor(
                                                        requireActivity().getResources().getColor(R.color.ourBlue));
                                            } else { // bigger than both
                                                meetings.setTextColor(requireActivity().getResources().getColor(R.color.green));
                                                meetings_word.setTextColor(
                                                        requireActivity().getResources().getColor(R.color.green));
                                            }
                                        } catch (Exception ignored) {

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Failed to read value
                                        Log.w(TAG, "Failed to read value.", error.toException());
                                    }
                                });
    }

    private void updateEventsReports() {
        for (String type : reportsTypes) {
            // initialize map
            eventsTypeCounter.put(type, 0);
        }
        ReportsRef = database.getReference("reports").child(userBranch);
        ReportsListener =
                ReportsRef.child(String.valueOf(month))
                        .addValueEventListener(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        top5Items3.clear();
                                        int reportsCount = (int) dataSnapshot.getChildrenCount();
                                        totslReportsCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));

                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            EventReport event = snapshot.getValue(EventReport.class);
                                            if (event != null) {
                                                try {
                                                    eventsTypeCounter.put(event.type, eventsTypeCounter.get(event.type) + 1);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                        ArrayList<Top5Item> top5ItemsArray = new ArrayList<>();
                                        for (Map.Entry<String, Integer> entry : eventsTypeCounter.entrySet()) {
                                            top5ItemsArray.add(
                                                    new Top5Item(
                                                            entry.getKey(),
                                                            entry.getValue(),
                                                            Math.round(((float) entry.getValue() / reportsCount) * 100),
                                                            false));
                                        }
                                        Collections.sort(top5ItemsArray);
                                        for (int i = 0; i < top5ItemsArray.size(); i++) {
                                            if (top5ItemsArray.get(i).total > 0)
                                                top5Items3.add(top5ItemsArray.get(i));
                                        }
                                        adapter3.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Failed to read value
                                        Log.w(TAG, "Failed to read value.", error.toException());
                                    }
                                });
    }

    private void updateTopTypes() {
        totalMosharkatCount.setText(String.valueOf(allMosharkat));
        ArrayList<Top5Item> top5ItemsArray = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : mosharkatTypesCounter.entrySet()) {
            top5ItemsArray.add(new Top5Item(entry.getKey(), entry.getValue(), 0, false));
        }
        Collections.sort(top5ItemsArray);
        int top5Sum = 0;
        for (int i = 0; i < Math.min(myRules.top_types_count, mosharkaTypes.length); i++) {
            top5Items.add(top5ItemsArray.get(i));
            top5Sum += top5ItemsArray.get(i).total;
        }
        for (int i = 0; i < top5Items.size(); i++) {
            top5Items.get(i).setProgress(Math.round(((float) top5Items.get(i).total / top5Sum) * 100));
        }
        adapter.notifyDataSetChanged();
    }

    private void updateTopDays() {
        ArrayList<Top5Item> top5ItemsArray = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : mosharkatDaysCounter.entrySet()) {
            top5ItemsArray.add(new Top5Item(entry.getKey(), entry.getValue(), 0, true));
        }
        Collections.sort(top5ItemsArray);
        int top5Sum = 0;
        for (int i = 0; i < Math.min(myRules.top_days_count, days.length); i++) {
            top5Items2.add(top5ItemsArray.get(i));
            top5Sum += top5ItemsArray.get(i).total;
        }
        for (int i = 0; i < top5Items2.size(); i++) {
            top5Items2.get(i).setProgress(Math.round(((float) top5Items2.get(i).total / top5Sum) * 100));
        }
        adapter2.notifyDataSetChanged();
    }

    private void updateAverages() {
        average_mas2oleen.setText(
                String.valueOf(Math.round((float) mas2oleenMosharkat * 10 / mas2oleenCounter) / 10.0));
        average_msharee3.setText(
                String.valueOf(Math.round((float) msharee3Mosharkat * 10 / msharee3Counter) / 10.0));
        average_fari2.setText(
                String.valueOf(
                        Math.round(
                                (float) (mas2oleenMosharkat + msharee3Mosharkat)
                                        * 10
                                        / (msharee3Counter + mas2oleenCounter))
                                / 10.0));
    }

    private void updatePoints() {
        int pointsCalculated =
                msharee3Mosharkat * myRules.mashroo3_points + myRules.mas2ool_points * mas2oleenMosharkat;
        points_fari2.setText(String.valueOf(pointsCalculated));
        int otherPointsCalculated = otherMosharkat + noobsMosharkat + da5el_2_Mosharkat * 2 + da5el_aboveMosharkat * 3;
        points_other.setText(String.valueOf(otherPointsCalculated));

        points_word = view.findViewById(R.id.points_word);
        points_word2 = view.findViewById(R.id.points_word2);

        int points_bad =
                myRules.bad_average * mas2oleenCounter * myRules.mas2ool_points
                        + myRules.bad_average * msharee3Counter * myRules.mashroo3_points;
        int points_medium =
                myRules.medium_average * mas2oleenCounter * myRules.mas2ool_points
                        + myRules.medium_average * msharee3Counter * myRules.mashroo3_points;
        try {
            if (pointsCalculated < (points_bad * ((float) day / 30))) {
                points_fari2.setTextColor(requireActivity().getResources().getColor(R.color.red));
                points_other.setTextColor(requireActivity().getResources().getColor(R.color.red));
                points_word.setTextColor(requireActivity().getResources().getColor(R.color.red));
                points_word2.setTextColor(requireActivity().getResources().getColor(R.color.red));

            } else if (pointsCalculated < (points_medium * ((float) day / 30))) {
                points_fari2.setTextColor(requireActivity().getResources().getColor(R.color.ourBlue));
                points_other.setTextColor(requireActivity().getResources().getColor(R.color.ourBlue));
                points_word.setTextColor(requireActivity().getResources().getColor(R.color.ourBlue));
                points_word2.setTextColor(requireActivity().getResources().getColor(R.color.ourBlue));

            } else { // bigger than both
                points_fari2.setTextColor(requireActivity().getResources().getColor(R.color.green));
                points_other.setTextColor(requireActivity().getResources().getColor(R.color.green));
                points_word.setTextColor(requireActivity().getResources().getColor(R.color.green));
                points_word2.setTextColor(requireActivity().getResources().getColor(R.color.green));

            }
        } catch (Exception ignored) {

        }
    }

    private void updateAttendance() {
        arrivedTeam.setText(String.valueOf(arrivedCounter));
        float percentage = (float) arrivedCounter / globalFari2Names.size() * 100;
        attendanceBar.setProgress(Math.round(percentage));
        percent.setText(UtilityClass.getPercentString(percentage));
        try {
            if (percentage < myRules.attendance_bad) {
                percent.setTextColor(requireActivity().getResources().getColor(R.color.red));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    attendanceBar.setProgressTintList(
                            ColorStateList.valueOf(requireActivity().getResources().getColor(R.color.red)));
                }
            } else if (percentage < myRules.attendance_medium) {
                percent.setTextColor(requireActivity().getResources().getColor(R.color.ourBlue));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    attendanceBar.setProgressTintList(
                            ColorStateList.valueOf(requireActivity().getResources().getColor(R.color.ourBlue)));
                }
            } else { // bigger than both
                percent.setTextColor(requireActivity().getResources().getColor(R.color.green));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    attendanceBar.setProgressTintList(
                            ColorStateList.valueOf(requireActivity().getResources().getColor(R.color.green)));
                }
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (usersRef != null && userListener != null) {
            usersRef.removeEventListener(userListener);
        }
        if (mosharkatTab != null && mosharkatlistener != null) {
            mosharkatTab.removeEventListener(mosharkatlistener);
        }
        if (appMosharkatRef != null && appMosharkatlistener != null) {
            appMosharkatRef.removeEventListener(appMosharkatlistener);
        }
        if (meetingsRef != null && meetingsListener != null) {
            meetingsRef.removeEventListener(meetingsListener);
        }
        if (ReportsRef != null && ReportsListener != null) {
            ReportsRef.removeEventListener(ReportsListener);
        }
    }
}
