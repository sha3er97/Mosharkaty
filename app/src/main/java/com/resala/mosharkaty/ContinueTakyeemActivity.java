package com.resala.mosharkaty;

import static com.resala.mosharkaty.fragments.TakyeemFragment.codeFound;
import static com.resala.mosharkaty.utility.classes.UtilityClass.myRules;
import static com.resala.mosharkaty.utility.classes.UtilityClass.userBranch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.resala.mosharkaty.utility.classes.Sig;

import java.util.Calendar;
import java.util.Locale;

public class ContinueTakyeemActivity extends AppCompatActivity {
    Button signBtn;
    EditText VolComment;
    FirebaseDatabase database;
    int thisMonth;
    String volName;
    Calendar cldr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue_takyeem);
        GraphView graph = findViewById(R.id.graph);
        TextView totalTV = findViewById(R.id.totalTV);
        VolComment = findViewById(R.id.volComment_et);
        signBtn = findViewById(R.id.signTakyeem);
        cldr = Calendar.getInstance(Locale.US);
        thisMonth = cldr.get(Calendar.MONTH); // تقييم الشهر اللي فات
        int lastMonth = thisMonth - 1;
        int beforeLastMonth = thisMonth - 2;
        // special cases correction
        switch (thisMonth) {
            case 2:
//                beforeLastMonth = 12;
                beforeLastMonth = 0;
                break;
            case 1:
//                beforeLastMonth = 11;
//                lastMonth = 12;
                beforeLastMonth = -1;
                lastMonth = 0;
                break;
        }
        Intent intent = getIntent();
        int lastMonthnum = Integer.parseInt(intent.getStringExtra("lastMonthnum"));
        int beforeLastMonthnum = Integer.parseInt(intent.getStringExtra("beforeLastMonthnum"));
        int thisMonthnum = Integer.parseInt(intent.getStringExtra("thisMonthnum"));

        volName = intent.getStringExtra("Volname");

        totalTV.setText(intent.getStringExtra("total"));
        int[] months = {thisMonth, lastMonth, beforeLastMonth};
        int[] mosharkat = {thisMonthnum, lastMonthnum, beforeLastMonthnum};

        LineGraphSeries<DataPoint> series =
                new LineGraphSeries<>(
                        new DataPoint[]{
                                new DataPoint(months[2], mosharkat[2]),
                                new DataPoint(months[1], mosharkat[1]),
                                new DataPoint(months[0], mosharkat[0])
                        });
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setThickness(8);
        graph.addSeries(series);
        //    graph.getGridLabelRenderer().setNumVerticalLabels(3);
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
        graph.getGridLabelRenderer().setHumanRounding(false, true);
        graph.getViewport().setMaxY(8);
        graph.getViewport().setMinY(0);
        // data base
        database = FirebaseDatabase.getInstance();
        if (!myRules.takyeem_available) {
            signBtn.setEnabled(false);
            signBtn.setBackgroundColor(
                    getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
            signBtn.setText("التقييم غير متوفر بعد");
            signBtn.setTextColor(getResources().getColor(R.color.new_text_black));
        }
    }

    public void signTakyeem(View view) {
        if (!codeFound) {
            Toast.makeText(
                            getApplicationContext(),
                            "لازم تحدث الكود بتاعك بالكود الصح عشان تقدر تشوف تقييمك و تمضيه",
                            Toast.LENGTH_SHORT)
                    .show();
            signBtn.setEnabled(false);
            return;
        }
        DatabaseReference signaturesRef =
                database
                        .getReference("signatures")
                        .child(userBranch)
                        .child(String.valueOf(thisMonth))
                        .child(volName);

        signaturesRef.setValue(
                new Sig(VolComment.getText().toString().trim(), cldr.getTime().toString(), volName));
        signBtn.setEnabled(false);
        signBtn.setBackgroundColor(
                getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
        signBtn.setText("تم الامضاء");
        signBtn.setTextColor(getResources().getColor(R.color.new_text_black));
        Toast.makeText(this, "شكرا لامضائك و استني تقييم الشهر الجاي ..", Toast.LENGTH_SHORT).show();
    }
}
