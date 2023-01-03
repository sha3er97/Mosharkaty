package com.resala.mosharkaty.fragments;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.fragments.HomeFragment.userCode;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.resala.mosharkaty.ContinueTakyeemActivity;
import com.resala.mosharkaty.R;
import com.resala.mosharkaty.utility.classes.Takyeem;

public class TakyeemFragment extends androidx.fragment.app.Fragment {
  View view;
  public static boolean codeFound = false;
  FirebaseDatabase database;
  Button contReading;
  int big_total;
  int last_month;
  int the_month_before;
  int this_month;
  String Volname;
  ValueEventListener Takyeemlistener;
  DatabaseReference takyeemTab;

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
    view = inflater.inflate(R.layout.fragment_takyeem, container, false);
    database = FirebaseDatabase.getInstance();
    contReading = view.findViewById(R.id.contRead);
      final TextView takyeemVolName = view.findViewById(R.id.takyeemVolName);

      final TextView communication = view.findViewById(R.id.communication);
      final TextView commitment = view.findViewById(R.id.commitment);
      final TextView problem_solving = view.findViewById(R.id.problem_solving);
      final TextView quality = view.findViewById(R.id.quality);
      final TextView creativity = view.findViewById(R.id.creativity);

      final TextView cooperation = view.findViewById(R.id.cooperation);
      final TextView ekhtlat = view.findViewById(R.id.ekhtlat);
      final TextView respect = view.findViewById(R.id.respect);
      final TextView humble = view.findViewById(R.id.humble);
      final TextView loyalty = view.findViewById(R.id.loyalty);
      final TextView execuses = view.findViewById(R.id.execues);

      final TextView totalTechTV = view.findViewById(R.id.totalTechTV);
      final TextView totalPersonalTV = view.findViewById(R.id.totalPersonalTV);
      final TextView head_bonus = view.findViewById(R.id.bonusHead);

      final TextView extra_comment = view.findViewById(R.id.extraComment);
      DatabaseReference liveSheet =
              database.getReference(
                      "1VuTdZ3el0o94Y9wqec0y90yxVG4Ko7PHTtrOm2EViOk"); // TODO :: شيت تقييمات لكل فرع
      takyeemTab = liveSheet.child("takyeem");
      Takyeemlistener =
              takyeemTab.addValueEventListener(
                      new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                              for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                  Takyeem takyeem = snapshot.getValue(Takyeem.class);
                                  assert takyeem != null;
                                  if (takyeem.code.equalsIgnoreCase(userCode)) {
                                      communication.setText(String.valueOf(takyeem.communication));
                                      commitment.setText(String.valueOf(takyeem.commitment));
                                      problem_solving.setText(String.valueOf(takyeem.problem_solving));
                                      quality.setText(String.valueOf(takyeem.quality));
                                      creativity.setText(String.valueOf(takyeem.creativity));
                                      cooperation.setText(String.valueOf(takyeem.cooperation));
                                      ekhtlat.setText(String.valueOf(takyeem.ekhtlat));
                                      respect.setText(String.valueOf(takyeem.respect));
                                      humble.setText(String.valueOf(takyeem.humble));
                                      loyalty.setText(String.valueOf(takyeem.loyalty));
                                      execuses.setText(String.valueOf(takyeem.execuses));
                                      totalTechTV.setText(String.valueOf(takyeem.total_technical));
                                      totalPersonalTV.setText(String.valueOf(takyeem.total_personal));
                                      head_bonus.setText(String.valueOf(takyeem.head_bonus));
                                      extra_comment.setText(String.valueOf(takyeem.extra_comment));
                                      big_total = takyeem.big_total;
                                      last_month = takyeem.last_month;
                                      the_month_before = takyeem.the_month_before;
                                      this_month = takyeem.this_month;
                                      Volname = takyeem.Volname;
                                      takyeemVolName.setText(Volname);
                                      //                    Toast.makeText(getContext(), "تم تحديث تقييمك",
                                      // Toast.LENGTH_SHORT).show();
                                      codeFound = true;
                                      break;
                                  }
                              }
                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError error) {
                              // Failed to read value
                              Log.w(TAG, "Failed to read value.", error.toException());
                          }
                      });
      // button listener
      contReading.setOnClickListener(
              v -> {
                  Intent intent = new Intent(getContext(), ContinueTakyeemActivity.class);
                  intent.putExtra("lastMonthnum", String.valueOf(last_month));
                  intent.putExtra("beforeLastMonthnum", String.valueOf(the_month_before));
                  intent.putExtra("thisMonthnum", String.valueOf(this_month));
                  intent.putExtra("total", String.valueOf(big_total));
                  intent.putExtra("Volname", Volname);
                  startActivity(intent);
              });
      return view;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (takyeemTab != null && Takyeemlistener != null) {
      takyeemTab.removeEventListener(Takyeemlistener);
    }
  }
}
