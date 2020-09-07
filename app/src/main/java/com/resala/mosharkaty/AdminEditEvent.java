package com.resala.mosharkaty;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.LoginActivity.userBranch;

public class AdminEditEvent extends AppCompatActivity {
    HashMap<String, String> eventsImages = new HashMap<>();
    EventsAdapter adapter;
    ArrayList<EventItem> eventItems = new ArrayList<>();
    FirebaseDatabase database;
    ValueEventListener Eventslistener;
    DatabaseReference EventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_edit_event);
        fillEventsImages();
        database = FirebaseDatabase.getInstance();
        EventsRef = database.getReference("events");
        final TextView errorTV = findViewById(R.id.eventsErrorTV);

        RecyclerView recyclerView = findViewById(R.id.eventsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new EventsAdapter(eventItems, getApplicationContext());
        recyclerView.setAdapter(adapter);

        Eventslistener =
                EventsRef.addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                eventItems.clear();
                                errorTV.setVisibility(View.INVISIBLE);
                                boolean eventFound = false;
                                final Calendar cldr = Calendar.getInstance(Locale.US);
                                int day = cldr.get(Calendar.DAY_OF_MONTH);
                                int month = cldr.get(Calendar.MONTH) + 1;
                                if (userBranch != null && dataSnapshot.hasChild(userBranch)) {
                                    for (DataSnapshot snapshot : dataSnapshot.child(userBranch).getChildren()) {
                                        Event event = snapshot.getValue(Event.class);
                                        if (event != null) {
                                            String[] splittedDate = event.date.split("/", 2);
                                            if ((Integer.parseInt(splittedDate[0]) >= day
                                                    && Integer.parseInt(splittedDate[1]) == month)
                                                    || Integer.parseInt(splittedDate[1]) > month) {
                                                String url = eventsImages.get(event.type);
                                                eventItems.add(
                                                        new EventItem(
                                                                event.Eventname,
                                                                event.date,
                                                                url,
                                                                event.description,
                                                                event.location,
                                                                event.time,
                                                                snapshot.getKey()));
                                                eventFound = true;
                                            }
                                        }
                                    }
                                }
                                if (!eventFound) {
                                    Log.i(TAG, "onDataChange: no events found");
                                    errorTV.setVisibility(View.VISIBLE);
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventsRef != null && Eventslistener != null) {
            EventsRef.removeEventListener(Eventslistener);
        }
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
        // 2.1 additions
        eventsImages.put("اجتماع", "https://i.imgur.com/BCRwgrz.jpg");
        eventsImages.put("اوتينج", "https://i.imgur.com/Y0hURa5.jpg");
        eventsImages.put("عزومة 2", "https://i.imgur.com/cLuQSJU.jpg");
        eventsImages.put("ولاد عم", "https://i.imgur.com/7ccjKJy.jpg");
        eventsImages.put("اجتماع 2", "https://i.imgur.com/eHYve40.jpg");
        eventsImages.put("اجتماع 3", "https://i.imgur.com/mn0i5n8.jpg");
    }
}
