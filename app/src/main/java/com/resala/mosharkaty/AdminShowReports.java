package com.resala.mosharkaty;

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

import static com.resala.mosharkaty.LoginActivity.branchOrder;
import static com.resala.mosharkaty.LoginActivity.isMrkzy;
import static com.resala.mosharkaty.NewAccount.branches;
import static com.resala.mosharkaty.ProfileFragment.userBranch;

public class AdminShowReports extends androidx.fragment.app.Fragment {
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
        view = inflater.inflate(R.layout.admin_show_reports, container, false);
        spin = view.findViewById(R.id.branchSpinner3);
        Button change_branch = view.findViewById(R.id.change_branch);
        Button showSignatures_btn = view.findViewById(R.id.showSignatures_btn);
        Button showClosings_btn = view.findViewById(R.id.showClosings_btn);
        Button showMosharkat_btn = view.findViewById(R.id.showMosharkat_btn);
        Button showUsers_btn = view.findViewById(R.id.showUsers_btn);
        Button show_confirmations_btn = view.findViewById(R.id.show_confirmations_btn);

        ArrayAdapter aa = new ArrayAdapter(getContext(), R.layout.spinner_item, branches);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        spin.setSelection(Math.max(branchOrder, 0)); // just in case
        if (!isMrkzy) {
            change_branch.setEnabled(false);
            change_branch.setText("لا يمكن تغيير الفرع");
            change_branch.setBackgroundColor(
                    getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
            change_branch.setTextColor(getResources().getColor(R.color.new_text_black));
            change_branch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        }
        userBranch = spin.getSelectedItem().toString();

        change_branch.setOnClickListener(
                view -> {
                    userBranch = spin.getSelectedItem().toString();
                    Toast.makeText(getContext(), "تم تحديث الفرع لفرع " + userBranch, Toast.LENGTH_SHORT)
                            .show();
                });
        showClosings_btn.setOnClickListener(
                view -> startActivity(new Intent(getActivity(), AdminShowClosings.class)));
        showMosharkat_btn.setOnClickListener(
                view -> startActivity(new Intent(getActivity(), AdminShowStatistics.class)));
        showSignatures_btn.setOnClickListener(
                view -> startActivity(new Intent(getActivity(), AdminShowSignature.class)));
        showUsers_btn.setOnClickListener(
                view -> startActivity(new Intent(getActivity(), AdminShowUsers.class)));
        show_confirmations_btn.setOnClickListener(
                view -> startActivity(new Intent(getActivity(), AdminShowConfirmations.class)));
        return view;
    }
}
