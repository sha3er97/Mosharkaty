package com.resala.mosharkaty;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;

import static com.resala.mosharkaty.ProfileFragment.userBranch;

public class AdminEvents extends androidx.fragment.app.Fragment
        implements AdapterView.OnItemSelectedListener {
    HashMap<String, String> eventsImages = new HashMap<>();
    View view;
    String[] types = {
            "فرز",
            "فرز 2",
            "فرز 3",
            "فرز 4",
            "تجهيزات رمضان",
            "ايفنت طوارئ",

            // نقل
            "نقل",
            "نقل 2",
            "نقل داخلي",
            "نقل خارجي",
            "نقل خارجي 2",
            "تحضير ايفنت/معارض",
            "نقلة بطاطين",
            "شغل مخزن",

            // اتصالات و اتش ار
            "سيشن",
            "اورينتيشن",
            "كرنفال",
            "كرنفال 2",
            "كرنفال 3",
            "كرنفال 4",
            "ورشة اتصالات",
            "ولاد عم",
            "افطار في الشارع",
            "حفلة داخل الفرع",

            // معارض و قوافل
            "معارض",
            "معارض 2",
            "معارض 3",
            "معرض كبير",
            "معرض داخل الفرع",
            "توزيع علي بيوت",
            "اعمار",
            "اعمار 2",
            "زيارات مسنين",
            "حفلة اطفال",
            "حفلة اطفال 2",
            "حفلة اطفال 3",
            "اطعام",
            "اطعام 2",
            "مائدة افطار",
            "مستشفي",
            "دار ايواء",

            // ايفنتات مركزية
            "كساء",
            "كساء 2",
            "مجزر",
            "معرض عرايس",
            "يوم اليتيم",
            "يوم اليتيم 2",
            "عيد الام",
            "عيد الام 2",
            "عزومة",
            "كامب مسؤولين فرز",
            "كامب مسؤولين فرز 2",
            "كامب مسؤولين فرز 3",
            "دوري كورة",
            "دوري كورة 2",
            "دوري كورة 3",

            // ايفنتات رسالة
            "كامب بنات",
            "كامب 48",
            "اجتماع الدكتور",
            "ميني كامب",
            "ميني كامب 2",
            "حفلة النشاط",
            "حفلة النشاط 2",

            // مكافحة
            "حملة مكافحة كبيرة",
            "حملة مكافحة",
            "حملة مكافحة 2",
            "حملة مكافحة 3",
            "مطبخ مكافحة"
    };
    DatePickerDialog picker;
    EditText eText;
    TimePickerDialog picker2;
    EditText eText2;
    Button addEvent_btn;
    FirebaseDatabase database;
    ImageView DemoImg;
    Spinner spin;
    int day;
    int month;
    int year;
    EditText EventName_et;
    EditText EventLocation_et;
    EditText EventDescription_et;

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
    fillEventsImages();
    database = FirebaseDatabase.getInstance();
    view = inflater.inflate(R.layout.admin_events_fragment, container, false);
    DemoImg = view.findViewById(R.id.demoImg);
    final DatabaseReference EventsRef = database.getReference("events").child(userBranch);

      EventName_et = view.findViewById(R.id.eventName_et);
      EventDescription_et = view.findViewById(R.id.eventDescription_et);
    EventLocation_et = view.findViewById(R.id.eventLocation_et);
    spin = view.findViewById(R.id.eventsTypeSpinner);
    eText = view.findViewById(R.id.eventDate_et);
      addEvent_btn = view.findViewById(R.id.add_event_btn);

      // buttons click listener
      addEvent_btn.setOnClickListener(
              v -> {
                  if (!validateForm()) return;
                  DatabaseReference currentEvent =
                          EventsRef.child(String.valueOf(System.currentTimeMillis() / 1000));
                  DatabaseReference dateRef = currentEvent.child("date");
                  DatabaseReference typeRef = currentEvent.child("type");
                  DatabaseReference descriptionRef = currentEvent.child("description");
                  DatabaseReference nameRef = currentEvent.child("Eventname");
                  DatabaseReference locRef = currentEvent.child("location");
                  DatabaseReference timeRef = currentEvent.child("time");

                  nameRef.setValue(EventName_et.getText().toString().trim());
                  descriptionRef.setValue(EventDescription_et.getText().toString());
                  dateRef.setValue(eText.getText().toString());
                  timeRef.setValue(eText2.getText().toString());
                  locRef.setValue(EventLocation_et.getText().toString().trim());
                  typeRef.setValue(spin.getSelectedItem().toString());

                  Toast.makeText(getContext(), "Event Added..", Toast.LENGTH_SHORT).show();
              });

      eText.setInputType(InputType.TYPE_NULL);
      eText.setOnClickListener(
              v -> {
                  final Calendar cldr = Calendar.getInstance();
                  day = cldr.get(Calendar.DAY_OF_MONTH);
                  month = cldr.get(Calendar.MONTH);
                  year = cldr.get(Calendar.YEAR);
                  // date picker dialog
                  picker =
                          new DatePickerDialog(
                                  getContext(),
                                  (view, year, monthOfYear, dayOfMonth) ->
                                          eText.setText(dayOfMonth + "/" + (monthOfYear + 1)),
                                  year,
                                  month,
                                  day);
                  picker.show();
              });

      eText2 = view.findViewById(R.id.eventTime_et);
      eText2.setInputType(InputType.TYPE_NULL);
      eText2.setOnClickListener(
              v -> {
                  final Calendar cldr = Calendar.getInstance();
                  int hour = cldr.get(Calendar.HOUR_OF_DAY);
                  int minutes = cldr.get(Calendar.MINUTE);
                  // time picker dialog
                  picker2 =
                          new TimePickerDialog(
                                  getContext(),
                                  (tp, sHour, sMinute) -> {
                                      int Mhour;
                                      String Mminute;
                                      String am_pm;
                                      Mhour = sHour;
                                      Mminute = String.valueOf(sMinute);
                                      if (sMinute == 0) {
                                          Mminute = "00";
                                      }
                                      if (Mhour > 12) {
                                          am_pm = "PM";
                                          Mhour = Mhour - 12;
                                      } else {
                                          am_pm = "AM";
                                      }
                                      eText2.setText(Mhour + ":" + Mminute + " " + am_pm);
                                  },
                                  hour,
                                  minutes,
                                  false);
                  picker2.show();
              });

      spin.setOnItemSelectedListener(this);
      // Creating the ArrayAdapter instance having the country list
      ArrayAdapter aa = new ArrayAdapter(getContext(), R.layout.spinner_item, types);
      aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Setting the ArrayAdapter data on the Spinner
    spin.setAdapter(aa);

    return view;
  }

  @Override
  public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
      String url = eventsImages.get(spin.getSelectedItem().toString());
    Picasso.get().load(url).into(DemoImg);
  }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private boolean validateForm() {
        boolean valid = true;
        String date = eText.getText().toString();
        String[] parts = date.split("/", 2);
        if (TextUtils.isEmpty(date)) {
            eText.setError("Required.");
            valid = false;
        } else if (Integer.parseInt(parts[1]) < month + 1
                || (Integer.parseInt(parts[1]) == month + 1 && Integer.parseInt(parts[0]) < day)) {
            eText.setError("you can't choose a date in the past.");
            valid = false;
        }
        String time = eText2.getText().toString();
        if (TextUtils.isEmpty(time)) {
            eText2.setError("Required.");
            valid = false;
        }
        String loc = EventLocation_et.getText().toString();
        if (TextUtils.isEmpty(loc)) {
            EventLocation_et.setError("Required.");
            valid = false;
        }
        String description = EventDescription_et.getText().toString();
        String name = EventName_et.getText().toString();
        if (TextUtils.isEmpty(description)) {
            EventDescription_et.setError("Required.");
      valid = false;
    }
    if (TextUtils.isEmpty(name)) {
      EventName_et.setError("Required.");
      valid = false;
    }
    if (valid) {
      eText.setError(null);
      EventDescription_et.setError(null);
      EventName_et.setError(null);
    }
    return valid;
  }

  private void fillEventsImages() {
    eventsImages.put("معارض", "https://i.imgur.com/cmxhBZJ.jpg");
    eventsImages.put("كساء", "https://i.imgur.com/HROESKW.jpg");
    eventsImages.put("معرض عرايس", "https://i.imgur.com/fPo06rN.jpg");
    eventsImages.put("سيشن", "https://i.imgur.com/N2YCTDD.jpg");
    eventsImages.put("كرنفال", "https://i.imgur.com/avUISuw.jpg");
    eventsImages.put("مجزر", "https://i.imgur.com/m73EbLz.jpg");
    eventsImages.put("اعمار 2", "https://i.imgur.com/QV7lwEo.jpg");
    eventsImages.put("فرز", "https://i.imgur.com/nCphOCO.jpg");
    eventsImages.put("حفلة اطفال", "https://i.imgur.com/Fc9YMkJ.jpg");
    eventsImages.put("كامب بنات", "https://i.imgur.com/xdZ5D21.jpg");
    eventsImages.put("اجتماع الدكتور", "https://i.imgur.com/Fh1mJLv.jpg");
    eventsImages.put("مستشفي", "https://i.imgur.com/MdmWtvW.jpg");
    eventsImages.put("تجهيزات رمضان", "https://i.imgur.com/bT65Hac.jpg");
    eventsImages.put("كامب مسؤولين فرز", "https://i.imgur.com/jkvqUad.jpg");
    eventsImages.put("ايفنت طوارئ", "https://i.imgur.com/I8rjgOc.jpg");
    eventsImages.put("معرض داخل الفرع", "https://i.imgur.com/Fc8LYxA.jpg");
    eventsImages.put("اطعام", "https://i.imgur.com/jpHNaYS.jpg");
    eventsImages.put("عزومة", "https://i.imgur.com/xqR6bmw.jpg");
    eventsImages.put("مائدة افطار", "https://i.imgur.com/zx2r5g9.jpg");
    eventsImages.put("توزيع علي بيوت", "https://i.imgur.com/T0RpU1r.jpg");
    eventsImages.put("حفلة النشاط", "https://i.imgur.com/LkpFrdY.jpg");
    eventsImages.put("معرض كبير", "https://i.imgur.com/88eJppq.jpg");
    eventsImages.put("اعمار", "https://i.imgur.com/rxfes8o.jpg");
    eventsImages.put("نقل", "https://i.imgur.com/p1KpNoc.jpg");
    eventsImages.put("فرز 2", "https://i.imgur.com/sfBxO1C.jpg");
    eventsImages.put("نقل خارجي", "https://i.imgur.com/T7rn3Ge.jpg");
    eventsImages.put("نقل خارجي 2", "https://i.imgur.com/SsNCr04.jpg");
    eventsImages.put("نقلة بطاطين", "https://i.imgur.com/5htd4Qr.jpg");
    eventsImages.put("شغل مخزن", "https://i.imgur.com/4w9YGRx.jpg");
    eventsImages.put("كرنفال 2", "https://i.imgur.com/1yxQODs.jpg");
    eventsImages.put("اطعام 2", "https://i.imgur.com/31EKUts.jpg");
    eventsImages.put("حملة مكافحة كبيرة", "https://i.imgur.com/RvbBpPc.jpg");
    eventsImages.put("حفلة اطفال 2", "https://i.imgur.com/CmN0U44.jpg");
    eventsImages.put("حفلة اطفال 3", "https://i.imgur.com/vRsOqRZ.jpg");
    eventsImages.put("يوم اليتيم", "https://i.imgur.com/zwvAmEh.jpg");
    eventsImages.put("كامب 48", "https://i.imgur.com/zGJu0KV.jpg");
    eventsImages.put("دوري كورة", "https://i.imgur.com/Gc51yBW.jpg");
    eventsImages.put("حملة مكافحة", "https://i.imgur.com/VgPtm1z.jpg");
    eventsImages.put("دار ايواء", "https://i.imgur.com/pF4EC0F.jpg");
    eventsImages.put("حملة مكافحة 2", "https://i.imgur.com/6aIgVxj.jpg");
    eventsImages.put("معارض 2", "https://i.imgur.com/xjydZEV.jpg");
    eventsImages.put("كامب مسؤولين فرز 2", "https://i.imgur.com/giqO8tF.jpg");
    eventsImages.put("نقل داخلي", "https://i.imgur.com/F7iRNwK.jpg");
    eventsImages.put("نقل 2", "https://i.imgur.com/Xo5Vbxk.jpg");
    eventsImages.put("ورشة اتصالات", "https://i.imgur.com/ddORToQ.jpg");
    eventsImages.put("كساء 2", "https://i.imgur.com/uanNgkT.jpg");
    eventsImages.put("كامب مسؤولين فرز 3", "https://i.imgur.com/x7y8yZd.jpg");
    eventsImages.put("زيارات مسنين", "https://i.imgur.com/lM5BD3A.jpg");
    eventsImages.put("فرز 3", "https://i.imgur.com/Qciv6Kh.jpg");
    eventsImages.put("كرنفال 3", "https://i.imgur.com/vWFXHr1.jpg");
    eventsImages.put("فرز 4", "https://i.imgur.com/mYQWcBr.jpg");
    eventsImages.put("عيد الام", "https://i.imgur.com/Wt7p4JB.jpg");
    eventsImages.put("عيد الام 2", "https://i.imgur.com/AeoHH6v.jpg");
    eventsImages.put("تحضير ايفنت/معارض", "https://i.imgur.com/IzQ6g55.jpg");
    eventsImages.put("يوم اليتيم 2", "https://i.imgur.com/1hNtlUS.jpg");
    eventsImages.put("حملة مكافحة 3", "https://i.imgur.com/n12h3sh.jpg");
    eventsImages.put("ميني كامب 2", "https://i.imgur.com/MttRKh9.jpg");
    eventsImages.put("ولاد عم", "https://i.imgur.com/4d8lS5v.jpg");
    eventsImages.put("معارض 3", "https://i.imgur.com/kJcrQEG.jpg");
    eventsImages.put("افطار في الشارع", "https://i.imgur.com/gmRfiU7.jpg");
    eventsImages.put("حفلة داخل الفرع", "https://i.imgur.com/QIc3ZDv.jpg");
    eventsImages.put("كرنفال 4", "https://i.imgur.com/5PZB1wc.jpg");
    eventsImages.put("توزيع حضار", "https://i.imgur.com/PLfqZgq.jpg");
    eventsImages.put("دوري كورة 2", "https://i.imgur.com/t2TFMm2.jpg");
    eventsImages.put("مطبخ مكافحة", "https://i.imgur.com/nzwgC1B.jpg");
    eventsImages.put("حفلة النشاط 2", "https://i.imgur.com/CV7werB.jpg");
    eventsImages.put("ميني كامب", "https://i.imgur.com/FwLGrvr.jpg");
    eventsImages.put("دوري كورة 3", "https://i.imgur.com/PLCx3ui.jpg");
    eventsImages.put("اورينتيشن", "https://i.imgur.com/GV3chTd.jpg");
  }
}
