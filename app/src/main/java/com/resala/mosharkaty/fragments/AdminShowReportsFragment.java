package com.resala.mosharkaty.fragments;

import static com.resala.mosharkaty.LoginActivity.branchOrder;
import static com.resala.mosharkaty.LoginActivity.isMrkzy;
import static com.resala.mosharkaty.LoginActivity.userBranch;
import static com.resala.mosharkaty.NewAccountActivity.branches;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.resala.mosharkaty.AdminCoursesActivity;
import com.resala.mosharkaty.AdminEditEventActivity;
import com.resala.mosharkaty.AdminEventsReportActivity;
import com.resala.mosharkaty.AdminMeetingsActivity;
import com.resala.mosharkaty.AdminMrkzyReportsActivity;
import com.resala.mosharkaty.AdminShowClosingsActivity;
import com.resala.mosharkaty.AdminShowConfirmationsActivity;
import com.resala.mosharkaty.AdminShowHomeMosharkatActivity;
import com.resala.mosharkaty.AdminShowSignatureActivity;
import com.resala.mosharkaty.AdminShowStatisticsActivity;
import com.resala.mosharkaty.AdminShowTakyeemActivity;
import com.resala.mosharkaty.AdminShowUsersActivity;
import com.resala.mosharkaty.AdminShowZeroesActivity;
import com.resala.mosharkaty.AdminWeeklyReportActivity;
import com.resala.mosharkaty.NasheetActivity;
import com.resala.mosharkaty.R;
import com.resala.mosharkaty.WithoutRepeatReportActivity;

public class AdminShowReportsFragment extends androidx.fragment.app.Fragment {
    View view;
    Spinner spin;

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
        view = inflater.inflate(R.layout.fragment_admin_show_reports, container, false);
        spin = view.findViewById(R.id.branchSpinner3);
        Button change_branch = view.findViewById(R.id.change_branch);
        Button showSignatures_btn = view.findViewById(R.id.showSignatures_btn);
        Button showClosings_btn = view.findViewById(R.id.showClosings_btn);
        Button showMosharkat_btn = view.findViewById(R.id.showMosharkat_btn);
        Button showUsers_btn = view.findViewById(R.id.showUsers_btn);
        Button show_confirmations_btn = view.findViewById(R.id.show_confirmations_btn);
        Button showHome_btn = view.findViewById(R.id.showHome_btn);
        Button showZeroes_btn = view.findViewById(R.id.showZeroes_btn);
        Button Meetings_btn = view.findViewById(R.id.Meetings_btn);
        Button editEvents_btn = view.findViewById(R.id.editEvents_btn);
        Button nasheet_btn = view.findViewById(R.id.nasheet_btn);
        Button weeklyReport_btn = view.findViewById(R.id.weeklyReport_btn);
        Button add_course_btn = view.findViewById(R.id.add_course_btn);
        Button show_takyeem_btn = view.findViewById(R.id.show_takyeem_btn);
        Button event_reports_btn = view.findViewById(R.id.event_reports_btn);
        Button showMrkzy_btn = view.findViewById(R.id.showMrkzy_btn);
        Button withoutRepeatReport_btn = view.findViewById(R.id.withoutRepeatReport_btn);

        if (isMrkzy) {
            showSignatures_btn.setEnabled(false);
            showClosings_btn.setEnabled(false);
            showMosharkat_btn.setEnabled(false);
            showUsers_btn.setEnabled(false);
            show_confirmations_btn.setEnabled(false);
            showHome_btn.setEnabled(false);
            showZeroes_btn.setEnabled(false);
            nasheet_btn.setEnabled(false);
            show_takyeem_btn.setEnabled(false);

            showSignatures_btn.setBackgroundColor(
                    getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
            showClosings_btn.setBackgroundColor(
                    getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
            showMosharkat_btn.setBackgroundColor(
                    getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
            showUsers_btn.setBackgroundColor(
                    getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
            show_confirmations_btn.setBackgroundColor(
                    getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
            showHome_btn.setBackgroundColor(
                    getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
            showZeroes_btn.setBackgroundColor(
                    getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
            nasheet_btn.setBackgroundColor(
                    getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
            show_takyeem_btn.setBackgroundColor(
                    getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
        } else { // لو مش مركزي
            change_branch.setEnabled(false);
            change_branch.setText("لا يمكن تغيير الفرع");
            change_branch.setBackgroundColor(
                    getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
            change_branch.setTextColor(getResources().getColor(R.color.new_text_black));
            change_branch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

            showMrkzy_btn.setVisibility(View.GONE);
            withoutRepeatReport_btn.setVisibility(View.GONE);

        }

        ArrayAdapter<String> aa = new ArrayAdapter<>(getContext(), R.layout.spinner_item, branches);
        aa.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        spin.setSelection(branchOrder);

        change_branch.setOnClickListener(
                view -> {
                    userBranch = spin.getSelectedItem().toString();
                    Toast.makeText(getContext(), "تم تحديث الفرع لفرع " + userBranch, Toast.LENGTH_SHORT)
                            .show();
                });
        showClosings_btn.setOnClickListener(
                view -> startActivity(new Intent(getActivity(), AdminShowClosingsActivity.class)));
        showMosharkat_btn.setOnClickListener(
                view -> startActivity(new Intent(getActivity(), AdminShowStatisticsActivity.class)));
        showSignatures_btn.setOnClickListener(
                view -> startActivity(new Intent(getActivity(), AdminShowSignatureActivity.class)));
        showUsers_btn.setOnClickListener(
                view -> startActivity(new Intent(getActivity(), AdminShowUsersActivity.class)));
        show_confirmations_btn.setOnClickListener(
                view -> startActivity(new Intent(getActivity(), AdminShowConfirmationsActivity.class)));
        showHome_btn.setOnClickListener(
                view -> startActivity(new Intent(getActivity(), AdminShowHomeMosharkatActivity.class)));
        showZeroes_btn.setOnClickListener(
                view -> startActivity(new Intent(getActivity(), AdminShowZeroesActivity.class)));
        editEvents_btn.setOnClickListener(
                view -> startActivity(new Intent(getActivity(), AdminEditEventActivity.class)));
        Meetings_btn.setOnClickListener(
                view -> startActivity(new Intent(getActivity(), AdminMeetingsActivity.class)));
        nasheet_btn.setOnClickListener(
                view -> startActivity(new Intent(getActivity(), NasheetActivity.class)));
        weeklyReport_btn.setOnClickListener(
                view -> startActivity(new Intent(getActivity(), AdminWeeklyReportActivity.class)));
        add_course_btn.setOnClickListener(
                view -> startActivity(new Intent(getActivity(), AdminCoursesActivity.class)));
        show_takyeem_btn.setOnClickListener(
                view -> startActivity(new Intent(getActivity(), AdminShowTakyeemActivity.class)));
        event_reports_btn.setOnClickListener(
                view -> startActivity(new Intent(getActivity(), AdminEventsReportActivity.class)));
        showMrkzy_btn.setOnClickListener(
                view -> startActivity(new Intent(getActivity(), AdminMrkzyReportsActivity.class)));
        withoutRepeatReport_btn.setOnClickListener(
                view -> startActivity(new Intent(getActivity(), WithoutRepeatReportActivity.class)));
        return view;
    }
}
