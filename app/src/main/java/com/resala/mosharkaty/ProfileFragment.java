package com.resala.mosharkaty;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.LoginActivity.userId;
import static com.resala.mosharkaty.TakyeemFragment.codeFound;

public class ProfileFragment extends androidx.fragment.app.Fragment {
    public static String userName;
    public static String userCode;
    public static String userBranch;
    View view;
    private FirebaseAuth mAuth;
    Button signOut_btn;
    Button ApplyChanges;
    EditText name;
    EditText code;
    TextView branch;
    TextView currentMosharkat;
    CheckBox nameCheck;
    CheckBox codeCheck;
  FirebaseDatabase database;

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
   * @param inflater The LayoutInflater object that can be used to inflate any views in the
   *     fragment,
   * @param container If non-null, this is the parent view that the fragment's UI should be attached
   *     to. The fragment should not add the view itself, but this can be used to generate the
   *     LayoutParams of the view.
   * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
   *     saved state as given here.
   * @return Return the View for the fragment's UI, or null.
   */
  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.profile_fragment, container, false);
    mAuth = FirebaseAuth.getInstance();
    database = FirebaseDatabase.getInstance();

    // define views
    signOut_btn = view.findViewById(R.id.signOutBTN);
    name = view.findViewById(R.id.volDetail);
    code = view.findViewById(R.id.codeDetail);
    branch = view.findViewById(R.id.far3Detail);
    nameCheck = view.findViewById(R.id.nameCheckBox);
    codeCheck = view.findViewById(R.id.codeCheckBox);
    ApplyChanges = view.findViewById(R.id.applyChanges_btn);
    currentMosharkat = view.findViewById(R.id.current_mosharkatNum);

    name.setText(userName);
    branch.setText(userBranch);

    // check boxes
    nameCheck.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            name.setEnabled(nameCheck.isChecked());
          }
        });
    codeCheck.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            code.setEnabled(codeCheck.isChecked());
          }
        });

    // buttons listeners
    signOut_btn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              mAuth.signOut();
              userName = getString(R.string.dummy_volunteer);
              userCode = getString(R.string.dummy_code);
              userBranch = getString(R.string.dummy_far3);
              userId = "-1";
              startActivity(new Intent(getActivity(), LoginActivity.class));
          }
        });

    ApplyChanges.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
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
          }
        });

      // data base access
      DatabaseReference usersRef = database.getReference("users");
      DatabaseReference currentUser = usersRef.child(userId);
      DatabaseReference nameRef = currentUser.child("name");
      DatabaseReference codeRef = currentUser.child("code");
      DatabaseReference branchRef = currentUser.child("branch");

      nameRef.addValueEventListener(
              new ValueEventListener() {
                  @Override
                  public void onDataChange(DataSnapshot dataSnapshot) {
                      // This method is called once with the initial value and again
                      // whenever data at this location is updated.
                      userName = dataSnapshot.getValue(String.class);
                      name.setText(userName);
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError error) {
                      // Failed to read value
                      Log.w(TAG, "Failed to read value.", error.toException());
                  }
              });

      codeRef.addValueEventListener(
              new ValueEventListener() {
                  @Override
                  public void onDataChange(DataSnapshot dataSnapshot) {
                      // This method is called once with the initial value and again
                      // whenever data at this location is updated.
                      userCode = dataSnapshot.getValue(String.class);
                      code.setText(userCode);
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError error) {
                      // Failed to read value
                      Log.w(TAG, "Failed to read value.", error.toException());
                  }
              });

      branchRef.addValueEventListener(
              new ValueEventListener() {
                  @Override
                  public void onDataChange(DataSnapshot dataSnapshot) {
                      // This method is called once with the initial value and again
                      // whenever data at this location is updated.
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
      DatabaseReference mosharkatTab = liveSheet.child("month_mosharkat");
      mosharkatTab.addValueEventListener(
              new ValueEventListener() {
                  @Override
                  public void onDataChange(DataSnapshot dataSnapshot) {
                      codeFound = false;
                      for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                          Volunteer user = snapshot.getValue(Volunteer.class);
                          if (user.code.equalsIgnoreCase(userCode)) {
                              currentMosharkat.setText(String.valueOf(user.count));
                              Toast.makeText(getContext(), "تم تحديث مشاركاتك", Toast.LENGTH_SHORT).show();
                              codeFound = true;
                          }
                      }
                      if (!codeFound) {
                          currentMosharkat.setText("code not found yet");
                          currentMosharkat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                          currentMosharkat.setTextColor(getResources().getColor(R.color.red));
                      } else {
                          currentMosharkat.setTextColor(getResources().getColor(R.color.ourBlue));
                          currentMosharkat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 64);
                      }
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError error) {
                      // Failed to read value
                      Log.w(TAG, "Failed to read value.", error.toException());
                  }
              });

      return view;
  }
}
