package com.resala.mosharkaty.fragments;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.fragments.HomeFragment.userCode;
import static com.resala.mosharkaty.fragments.HomeFragment.userName;
import static com.resala.mosharkaty.fragments.HomeFragment.userOfficialName;
import static com.resala.mosharkaty.fragments.TakyeemFragment.codeFound;
import static com.resala.mosharkaty.utility.classes.UtilityClass.BRANCHES_COUNT;
import static com.resala.mosharkaty.utility.classes.UtilityClass.branches;
import static com.resala.mosharkaty.utility.classes.UtilityClass.branchesSheets;
import static com.resala.mosharkaty.utility.classes.UtilityClass.myRules;
import static com.resala.mosharkaty.utility.classes.UtilityClass.userBranch;
import static com.resala.mosharkaty.utility.classes.UtilityClass.userId;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.resala.mosharkaty.ShowAllCourses;
import com.resala.mosharkaty.ui.adapters.EnrolledCoursesAdapter;
import com.resala.mosharkaty.utility.classes.Course;
import com.resala.mosharkaty.utility.classes.MosharkaItem;
import com.resala.mosharkaty.utility.classes.User;
import com.resala.mosharkaty.utility.classes.UtilityClass;
import com.resala.mosharkaty.utility.classes.Volunteer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class ProfileFragment extends androidx.fragment.app.Fragment implements AdapterView.OnItemSelectedListener {
    View view;
    ImageButton ApplyChanges;
    Button Courses_btn;
    EditText name;
    EditText code;
    Spinner branch;
    TextView currentMosharkatapp;
    TextView currentpercent;
    ProgressBar attendanceBar;

    FirebaseDatabase database;
    DatabaseReference usersRef;
    ValueEventListener userlistener;

    DatabaseReference mosharkatTab;
    ValueEventListener mosharkatTablistener;

    int month, mycounter;
    ValueEventListener Mosharkatlistener;
    DatabaseReference MosharkatRef;
    ValueEventListener CoursesListener;
    DatabaseReference CoursesRef;

    EnrolledCoursesAdapter adapter;
    ArrayList<Course> courseItems = new ArrayList<>();
    ArrayList<String> enrolledCourses = new ArrayList<>();

    DatabaseReference EnrollmentRef;
    ValueEventListener EnrollmentListener;

    /**
     * Called when the fragment is visible to the user and actively running.
     */
    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

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
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        database = FirebaseDatabase.getInstance();

        // define views
        name = view.findViewById(R.id.volDetail);
        code = view.findViewById(R.id.codeDetail);
        ApplyChanges = view.findViewById(R.id.applyChanges_btn2);
        Courses_btn = view.findViewById(R.id.Courses_btn);
        currentMosharkatapp = view.findViewById(R.id.current_from_app);
        currentpercent = view.findViewById(R.id.current_percent);

        branch = view.findViewById(R.id.far3Detail);
        branch.setOnItemSelectedListener(this);
        // Creating the ArrayAdapter instance having the country list
        ArrayAdapter<String> aa = new ArrayAdapter<>(requireContext(), R.layout.spinner_item2, branches);
        aa.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        branch.setAdapter(aa);
        branch.setSelection(Arrays.asList(branches).indexOf(userBranch));
//        branch.setText(userBranch);

        // buttons listeners
        Courses_btn.setOnClickListener(
                v -> {
                    startActivity(new Intent(getActivity(), ShowAllCourses.class));
                });
        ApplyChanges.setOnClickListener(
                v -> {
                    DatabaseReference usersRef = database.getReference("users");

                    String nameText = name.getText().toString();
                    String codeText = code.getText().toString().trim();
                    String branchText = branch.getSelectedItem().toString();
                    String[] words = nameText.split(" ", 5);
                    if (TextUtils.isEmpty(nameText)) {
                        name.setError("Required.");
                        return;
                    }
                    if (TextUtils.isEmpty(codeText)) {
                        name.setError("Required.");
                        return;
                    }
                    if (words.length < 3) {
                        name.setError("الاسم لازم يبقي ثلاثي علي الاقل.");
                        return;
                    }
                    if (code.getText().length() != UtilityClass.userCodeLength) {
                        code.setError("incorrect code entered .. " + UtilityClass.userCodeLength + " Digits required");
                        return;
                    }
                    if (userId.equals("-1")) {
                        Toast.makeText(getContext(), "خطا في حفظ التعديلات", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DatabaseReference currentUser = usersRef.child(userId);
                    currentUser.setValue(new User(branchText, codeText, nameText));
                    Toast.makeText(getContext(), "changes Saved..", Toast.LENGTH_SHORT).show();
                });

        // data base access
        usersRef = database.getReference("users").child(userId);

        RecyclerView recyclerView = view.findViewById(R.id.coursesRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EnrolledCoursesAdapter(courseItems, getContext());
        recyclerView.setAdapter(adapter);

        userlistener =
                usersRef.addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                if (user != null) {
                                    userName = user.name;
                                    userCode = user.code;
                                    userBranch = user.branch;

                                    name.setText(userName);
                                    code.setText(userCode);
//                                    branch.setText(userBranch);
                                    branch.setSelection(Arrays.asList(branches).indexOf(userBranch));
                                    getUserName();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });

        EnrollmentRef = database.getReference("enrollment");
        EnrollmentListener =
                EnrollmentRef.addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                enrolledCourses.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if (snapshot.hasChild(userId)) {
                                        boolean isEnrolled = snapshot.child(userId).getValue(Boolean.class);
                                        if (isEnrolled) enrolledCourses.add(snapshot.getKey());
                                    }
                                }
                                getEnrolledCourses();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

        return view;
    }

    private void getUserName() {
        String branchSheetLink =
                userBranch.equals(branches[BRANCHES_COUNT])
                        ? branchesSheets.get(branches[0])
                        : branchesSheets.get(userBranch);
        assert branchSheetLink != null;
        DatabaseReference liveSheet = database.getReference(branchSheetLink);
        mosharkatTab = liveSheet.child("month_mosharkat");
        mosharkatTablistener =
                mosharkatTab.addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                codeFound = false;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Volunteer user = snapshot.getValue(Volunteer.class);
                                    if (user != null && user.code.equalsIgnoreCase(userCode)) {
                                        userOfficialName = user.Volname;
                                        updateMosharkaty();
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

    private void updateMosharkaty() {
        MosharkatRef = database.getReference("mosharkat").child(userBranch);
        final Calendar cldr = Calendar.getInstance(Locale.US);
        month = cldr.get(Calendar.MONTH) + 1;
        Mosharkatlistener =
                MosharkatRef.child(String.valueOf(month))
                        .addValueEventListener(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        mycounter = 0;
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            MosharkaItem mosharka = snapshot.getValue(MosharkaItem.class);
                                            if (mosharka != null) {
                                                if (mosharka.getVolname().trim().equals(userName.trim())
                                                        || mosharka.getVolname().trim().equals(userOfficialName.trim())) {
                                                    mycounter++;
                                                }
                                            }
                                        }
                                        updateMosharkatUI();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Failed to read value
                                        Log.w(TAG, "Failed to read value.", error.toException());
                                    }
                                });
    }

    private void getEnrolledCourses() {
        CoursesRef = database.getReference("courses");
        CoursesListener =
                CoursesRef.addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                courseItems.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Course course = snapshot.getValue(Course.class);
                                    if (course != null && enrolledCourses.contains(snapshot.getKey())) {
                                        courseItems.add(course);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
    }

    private void updateMosharkatUI() {
        currentMosharkatapp.setText(String.valueOf(mycounter));
        attendanceBar = view.findViewById(R.id.determinateBar);
        float percentage = (float) Math.min(mycounter, 8) / 8 * 100;
        attendanceBar.setProgress(Math.round(percentage));
        currentpercent.setText(Math.round(percentage) + " %");
        try {
            if (percentage < (float) myRules.bad_average / 8 * 100) {
                currentpercent.setTextColor(getResources().getColor(R.color.red));
                currentMosharkatapp.setTextColor(getResources().getColor(R.color.red));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    attendanceBar.setProgressTintList(
                            ColorStateList.valueOf(getResources().getColor(R.color.red)));
                }
            } else if (percentage < (float) myRules.medium_average / 8 * 100) {
                currentpercent.setTextColor(getResources().getColor(R.color.ourBlue));
                currentMosharkatapp.setTextColor(getResources().getColor(R.color.ourBlue));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    attendanceBar.setProgressTintList(
                            ColorStateList.valueOf(getResources().getColor(R.color.ourBlue)));
                }
            } else { // bigger than both
                currentpercent.setTextColor(getResources().getColor(R.color.green));
                currentMosharkatapp.setTextColor(getResources().getColor(R.color.green));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    attendanceBar.setProgressTintList(
                            ColorStateList.valueOf(getResources().getColor(R.color.green)));
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (usersRef != null && userlistener != null) {
            usersRef.removeEventListener(userlistener);
        }
        if (mosharkatTab != null && mosharkatTablistener != null) {
            mosharkatTab.removeEventListener(mosharkatTablistener);
        }
        if (MosharkatRef != null && Mosharkatlistener != null) {
            MosharkatRef.removeEventListener(Mosharkatlistener);
        }
        if (CoursesRef != null && CoursesListener != null) {
            CoursesRef.removeEventListener(CoursesListener);
        }
        if (EnrollmentRef != null && EnrollmentListener != null) {
            EnrollmentRef.removeEventListener(EnrollmentListener);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
