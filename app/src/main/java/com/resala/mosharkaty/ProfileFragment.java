package com.resala.mosharkaty;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.LoginActivity.userBranch;
import static com.resala.mosharkaty.LoginActivity.userId;
import static com.resala.mosharkaty.Splash.myRules;
import static com.resala.mosharkaty.TakyeemFragment.codeFound;

public class ProfileFragment extends androidx.fragment.app.Fragment {
    public static String userName;
    public static String userCode;
    public static String userOfficialName;
    View view;
    Button ApplyChanges;
    EditText name;
    EditText code;
    TextView branch;
    TextView currentMosharkatOfficial;
    TextView currentMosharkatapp;
    TextView currentpercent;
    ProgressBar attendanceBar;

    CheckBox nameCheck;
    CheckBox codeCheck;
    FirebaseDatabase database;
    ValueEventListener namelistener;
    ValueEventListener codelistener;
    ValueEventListener branchlistener;
    ValueEventListener mosharkatlistener;

    DatabaseReference nameRef;
    DatabaseReference codeRef;
    DatabaseReference branchRef;
    DatabaseReference mosharkatTab;

    int month, mycounter;
    ValueEventListener Mosharkatlistener;
    DatabaseReference MosharkatRef;

    /**
     * Called when the fragment is visible to the user and actively running.
     */
    @Override
    public void onResume() {
        super.onResume();
        ConstraintLayout official = view.findViewById(R.id.constraintLayout6);
        if (myRules.show_official) {
            official.setVisibility(View.VISIBLE);
        } else official.setVisibility(View.GONE);
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
        view = inflater.inflate(R.layout.profile_fragment, container, false);
        database = FirebaseDatabase.getInstance();

        // define views
        name = view.findViewById(R.id.volDetail);
        code = view.findViewById(R.id.codeDetail);
        branch = view.findViewById(R.id.far3Detail);
        nameCheck = view.findViewById(R.id.nameCheckBox);
        codeCheck = view.findViewById(R.id.codeCheckBox);
        ApplyChanges = view.findViewById(R.id.applyChanges_btn);
        currentMosharkatOfficial = view.findViewById(R.id.current_official);
        currentMosharkatapp = view.findViewById(R.id.current_from_app);
        currentpercent = view.findViewById(R.id.current_percent);
        name.setText(userName);
        branch.setText(userBranch);

        // check boxes
        nameCheck.setOnClickListener(v -> name.setEnabled(nameCheck.isChecked()));
        codeCheck.setOnClickListener(v -> code.setEnabled(codeCheck.isChecked()));

        // buttons listeners
        ApplyChanges.setOnClickListener(
                v -> {
                    DatabaseReference usersRef = database.getReference("users");
                    DatabaseReference currentUser = usersRef.child(userId);
                    DatabaseReference nameRef = currentUser.child("name");
                    DatabaseReference codeRef = currentUser.child("code");
                    DatabaseReference branchRef = currentUser.child("branch");

                    String nameText = name.getText().toString();
                    String codeText = code.getText().toString().trim();
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
                    if (code.getText().length() != 5) {
                        code.setError("incorrect code entered .. 5 digits required");
                        return;
                    }
                    if (userId.equals("-1")) {
                        Toast.makeText(getContext(), "خطا في حفظ التعديلات", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    nameRef.setValue(nameText);
                    codeRef.setValue(codeText);
                    branchRef.setValue(branch.getText().toString());
                    Toast.makeText(getContext(), "changes Saved..", Toast.LENGTH_SHORT).show();
                });

        // data base access
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
                                name.setText(userName);
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
                                code.setText(userCode);
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
                                branch.setText(userBranch);
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
                                        //                    Toast.makeText(getContext(), "تم تحديث مشاركاتك",
                                        // Toast.LENGTH_SHORT).show();
                                        codeFound = true;
                                        break;
                                    }
                                }
                                if (!codeFound) {
                                    currentMosharkatOfficial.setText("code not found yet");
                                    currentMosharkatOfficial.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                                    currentMosharkatOfficial.setTextColor(getResources().getColor(R.color.red));
                                    userOfficialName = " ";
                                } else {
                                    currentMosharkatOfficial.setTextColor(getResources().getColor(R.color.ourBlue));
                                    currentMosharkatOfficial.setTextSize(
                                            TypedValue.COMPLEX_UNIT_SP, myRules.big_font);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
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
        return view;
    }

    private void updateMosharkatUI() {
        currentMosharkatapp.setText(String.valueOf(mycounter));
        currentMosharkatapp.setTextColor(getResources().getColor(R.color.ourBlue));
        currentMosharkatapp.setTextSize(TypedValue.COMPLEX_UNIT_SP, myRules.big_font);
        attendanceBar = view.findViewById(R.id.determinateBar);
        float percentage = (float) Math.min(mycounter, 8) / 8 * 100;
        attendanceBar.setProgress(Math.round(percentage));
        currentpercent.setText(Math.round(percentage) + " %");
        currentpercent.setTextColor(getResources().getColor(R.color.ourBlue));
        currentpercent.setTextSize(TypedValue.COMPLEX_UNIT_SP, myRules.big_font);
        if (percentage < (float) myRules.bad_average / 8 * 100) {
            currentpercent.setTextColor(getResources().getColor(R.color.red));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                attendanceBar.setProgressTintList(
                        ColorStateList.valueOf(getResources().getColor(R.color.red)));
            }
        } else if (percentage < (float) myRules.medium_average / 8 * 100) {
            currentpercent.setTextColor(getResources().getColor(R.color.ourBlue));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                attendanceBar.setProgressTintList(
                        ColorStateList.valueOf(getResources().getColor(R.color.ourBlue)));
            }
        } else { // bigger than both
            currentpercent.setTextColor(getResources().getColor(R.color.green));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                attendanceBar.setProgressTintList(
                        ColorStateList.valueOf(getResources().getColor(R.color.green)));
            }
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
        if (MosharkatRef != null && Mosharkatlistener != null) {
            MosharkatRef.removeEventListener(Mosharkatlistener);
        }
    }
}
