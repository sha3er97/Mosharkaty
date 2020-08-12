package com.resala.mosharkaty;

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

import java.util.Calendar;

import static com.resala.mosharkaty.ProfileFragment.userBranch;
import static com.resala.mosharkaty.TakyeemFragment.codeFound;

public class ContinueTakyeem extends AppCompatActivity {
  Button signBtn;
  EditText VolComment;
  FirebaseDatabase database;
  int thisMonth;
  String volName;
  Calendar cldr;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.continue_takyeem);
    GraphView graph = findViewById(R.id.graph);
    TextView totalTV = findViewById(R.id.totalTV);
    VolComment = findViewById(R.id.volComment_et);
    signBtn = findViewById(R.id.signTakyeem);
    cldr = Calendar.getInstance();
    thisMonth = cldr.get(Calendar.MONTH); // تقييم الشهر اللي فات
    int lastMonth = thisMonth - 1;
    int beforeLastMonth = thisMonth - 2;
    // special cases correction
    switch (thisMonth) {
      case 2:
        beforeLastMonth = 12;
        break;
      case 1:
        beforeLastMonth = 11;
        lastMonth = 12;
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
            new DataPoint[] {
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
  }

  public void signTakyeem(View view) {
    if (!codeFound)
    {
      Toast.makeText(getApplicationContext(),"لازم تحدث الكود بتاعك بالكود الصح عشان تقدر تشوف تقييمك و تمضيه",Toast.LENGTH_SHORT).show();
      signBtn.setEnabled(false);
      return;
    }
    signBtn.setEnabled(false);
    signBtn.setBackgroundColor(
        getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
    signBtn.setText("تم الامضاء");
      DatabaseReference signaturesRef = database.getReference("signatures").child(userBranch);
      DatabaseReference monthSignaturesRef = signaturesRef.child(String.valueOf(thisMonth));
    DatabaseReference volSignatureRef = monthSignaturesRef.child(volName);
    DatabaseReference dateRef = volSignatureRef.child("signatureDate");
    DatabaseReference commentRef = volSignatureRef.child("comment");
    DatabaseReference nameRef = volSignatureRef.child("volName");
    nameRef.setValue(volName);
    dateRef.setValue(cldr.getTime().toString());
    commentRef.setValue(VolComment.getText().toString());
      Toast.makeText(this, "شكرا لامضائك و استني تقييم الشهر الجاي ..", Toast.LENGTH_SHORT).show();
      finish();
  }
}
