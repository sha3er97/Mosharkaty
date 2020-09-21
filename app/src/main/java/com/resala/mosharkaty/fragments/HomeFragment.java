package com.resala.mosharkaty.fragments;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.resala.mosharkaty.R;
import com.resala.mosharkaty.utility.classes.MosharkaItem;
import com.resala.mosharkaty.utility.classes.Volunteer;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.LoginActivity.isAdmin;
import static com.resala.mosharkaty.LoginActivity.userBranch;
import static com.resala.mosharkaty.LoginActivity.userId;
import static com.resala.mosharkaty.SplashActivity.myRules;
import static com.resala.mosharkaty.fragments.ProfileFragment.userCode;
import static com.resala.mosharkaty.fragments.ProfileFragment.userName;
import static com.resala.mosharkaty.fragments.ProfileFragment.userOfficialName;
import static com.resala.mosharkaty.fragments.TakyeemFragment.codeFound;

public class HomeFragment extends androidx.fragment.app.Fragment {
  View view;
  TextView branchTV;
  FirebaseDatabase database;
  ValueEventListener namelistener;
  ValueEventListener codelistener;
  ValueEventListener branchlistener;
  ValueEventListener mosharkatlistener;

  DatabaseReference nameRef;
  DatabaseReference codeRef;
  DatabaseReference branchRef;
  DatabaseReference mosharkatTab;

  DatabaseReference appMosharkatRef;
  DatabaseReference meetingsRef;

  ValueEventListener appMosharkatlistener;
  ValueEventListener meetingslistener;

  HashMap<String, String> teamDegrees = new HashMap<>();
  private ProgressDialog progress;
  int arrivedCounter;
  int teamCounter;
  int mas2oleenCounter;
  int msharee3Counter;
  int month;
  int mas2oleenMosharkat;
  int msharee3Mosharkat;
  TextView percent;
  TextView totalTeam;
  TextView arrivedTeam;
  TextView points;
  TextView meetings;
  TextView average_fari2;
  TextView average_mas2oleen;
  TextView average_msharee3;
  ProgressBar attendanceBar;

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
    if (!isAdmin) {
      progress = new ProgressDialog(getContext());
      progress.setTitle("Loading");
      progress.setMessage("لحظات معانا...");
      progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
      progress.show();
      DatabaseReference usersRef = database.getReference("users");
      DatabaseReference currentUser = usersRef.child(userId);
      nameRef = currentUser.child("name");
      codeRef = currentUser.child("code");
      branchRef = currentUser.child("branch");

      namelistener =
              nameRef.addValueEventListener(
                      new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                          userName = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                          // Failed to read value
                          Log.w(TAG, "Failed to read value.", error.toException());
                        }
                      });

      codelistener =
              codeRef.addValueEventListener(
                      new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                          userCode = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                          // Failed to read value
                          Log.w(TAG, "Failed to read value.", error.toException());
                        }
                      });

      branchlistener =
              branchRef.addValueEventListener(
                      new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                          userBranch = dataSnapshot.getValue(String.class);
                          branchTV.setText(userBranch);
                          // To dismiss the dialog
                          progress.dismiss();
                          refreshReports();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                          // Failed to read value
                          Log.w(TAG, "Failed to read value.", error.toException());
                        }
                      });
      DatabaseReference liveSheet =
              database.getReference("1tsMZ5EwtKrBUGuLFVBvuwpU5ve0JKMsaqK1nNAONj-0");
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
                            }
                          }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                          // Failed to read value
                          Log.w(TAG, "Failed to read value.", error.toException());
                        }
                      });
    } else {
      branchTV.setText(userBranch);
      refreshReports();
    }

    return view;
  }

  private void refreshReports() {
    average_fari2 = view.findViewById(R.id.average_fari2);
    average_mas2oleen = view.findViewById(R.id.average_mas2oleen);
    average_msharee3 = view.findViewById(R.id.average_msharee3);
    points = view.findViewById(R.id.points);
    meetings = view.findViewById(R.id.meetings);
    percent = view.findViewById(R.id.percent);
    totalTeam = view.findViewById(R.id.totalTeam);
    arrivedTeam = view.findViewById(R.id.arrivedTeam);
    attendanceBar = view.findViewById(R.id.determinateBar);
    DatabaseReference liveSheet =
            database.getReference("1tsMZ5EwtKrBUGuLFVBvuwpU5ve0JKMsaqK1nNAONj-0");
    mosharkatTab = liveSheet.child("month_mosharkat");
    mosharkatTab.addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teamCounter = 0;
                mas2oleenCounter = 0;
                msharee3Counter = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                  Volunteer user = snapshot.getValue(Volunteer.class);
                  if (user != null) {
                    if (!user.degree.matches("(.*)مجمد(.*)")) {
                      teamDegrees.put(user.Volname, user.degree);
                      teamCounter++;
                    }
                    if (user.degree.matches("(.*)مسؤول(.*)")) mas2oleenCounter++;
                    else if (user.degree.matches("(.*)مشروع(.*)")) msharee3Counter++;
                  }
                }
                totalTeam.setText(String.valueOf(teamCounter));
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
              }
            });
    Log.d("Home", "finished official data");
    final Calendar cldr = Calendar.getInstance(Locale.US);
    month = cldr.get(Calendar.MONTH) + 1;
    meetingsRef = database.getReference("meetings").child(userBranch);
    meetingslistener =
            meetingsRef
                    .child(String.valueOf(month))
                    .addValueEventListener(
                            new ValueEventListener() {
                              @Override
                              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                meetings.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                              }

                              @Override
                              public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                              }
                            });

    appMosharkatRef = database.getReference("mosharkat").child(userBranch);
    appMosharkatlistener =
            appMosharkatRef
                    .child(String.valueOf(month))
                    .addValueEventListener(
                            new ValueEventListener() {
                              @Override
                              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HashMap<String, Integer> nameCounting = new HashMap<>();
                                mas2oleenMosharkat = 0;
                                msharee3Mosharkat = 0;
                                arrivedCounter = 0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                  MosharkaItem mosharka = snapshot.getValue(MosharkaItem.class);
                                  if (mosharka != null) {
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

                                for (Map.Entry entry : nameCounting.entrySet()) {
                                  String degree = teamDegrees.get(entry.getKey().toString());
                                  if (teamDegrees.containsKey(entry.getKey().toString())) {
                                    assert degree != null;
                                    if (!degree.matches("(.*)مجمد(.*)")) arrivedCounter++;
                                    if (degree.matches("(.*)مسؤول(.*)"))
                                      mas2oleenMosharkat += Integer.parseInt(entry.getValue().toString());
                                    if (degree.matches("(.*)مشروع(.*)"))
                                      msharee3Mosharkat += Integer.parseInt(entry.getValue().toString());
                                  }
                                }
                                updateAttendance();
                                updatePoints();
                                updateAverages();
                                //                    Toast.makeText(getContext(), "Report updated",
                                // Toast.LENGTH_SHORT).show();
                              }

                              @Override
                              public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                              }
                            });
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
    points.setText(String.valueOf(pointsCalculated));
    TextView points_word = view.findViewById(R.id.points_word);
    int points_bad =
            myRules.bad_average * mas2oleenCounter * myRules.mas2ool_points
                    + myRules.bad_average * msharee3Counter * myRules.mashroo3_points;
    int points_medium =
            myRules.medium_average * mas2oleenCounter * myRules.mas2ool_points
                    + myRules.medium_average * msharee3Counter * myRules.mashroo3_points;
    try {
      if (pointsCalculated < points_bad) {
        points.setTextColor(getActivity().getResources().getColor(R.color.red));
        points_word.setTextColor(getActivity().getResources().getColor(R.color.red));
      } else if (pointsCalculated < points_medium) {
        points.setTextColor(getActivity().getResources().getColor(R.color.ourBlue));
        points_word.setTextColor(getActivity().getResources().getColor(R.color.ourBlue));
      } else { // bigger than both
        points.setTextColor(getActivity().getResources().getColor(R.color.green));
        points_word.setTextColor(getActivity().getResources().getColor(R.color.green));
      }
    } catch (Exception ignored) {

    }
  }

  private void updateAttendance() {
    arrivedTeam.setText(String.valueOf(arrivedCounter));
    float percentage = (float) arrivedCounter / teamCounter * 100;
    attendanceBar.setProgress(Math.round(percentage));
    percent.setText(Math.round(percentage) + " %");
    try {
      if (percentage < myRules.attendance_bad) {
        percent.setTextColor(getActivity().getResources().getColor(R.color.red));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          attendanceBar.setProgressTintList(
                  ColorStateList.valueOf(getActivity().getResources().getColor(R.color.red)));
        }
      } else if (percentage < myRules.attendance_medium) {
        percent.setTextColor(getActivity().getResources().getColor(R.color.ourBlue));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          attendanceBar.setProgressTintList(
                  ColorStateList.valueOf(getActivity().getResources().getColor(R.color.ourBlue)));
        }
      } else { // bigger than both
        percent.setTextColor(getActivity().getResources().getColor(R.color.green));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          attendanceBar.setProgressTintList(
                  ColorStateList.valueOf(getActivity().getResources().getColor(R.color.green)));
        }
      }
    } catch (Exception ignored) {

    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (nameRef != null && namelistener != null) {
      nameRef.removeEventListener(namelistener);
    }
    if (codeRef != null && codelistener != null) {
      codeRef.removeEventListener(codelistener);
    }
    if (branchRef != null && branchlistener != null) {
      branchRef.removeEventListener(branchlistener);
    }
    if (mosharkatTab != null && mosharkatlistener != null) {
      mosharkatTab.removeEventListener(mosharkatlistener);
    }
    if (appMosharkatRef != null && appMosharkatlistener != null) {
      appMosharkatRef.removeEventListener(appMosharkatlistener);
    }
    if (meetingsRef != null && meetingslistener != null) {
      meetingsRef.removeEventListener(meetingslistener);
    }
  }
}
