package com.resala.mosharkaty.utility.classes;

import static com.resala.mosharkaty.AdminAddCourseActivity.courseColors;

import java.util.HashMap;

public class UtilityClass {
    public static String playStoreLink = "https://play.google.com/store/apps/details?id=com.resala.mosharkaty";
    public static final int userCodeLength = 5;
    public static final int BRANCHES_COUNT = 9;
    public static String[] months = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    public static String[] days = {
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17",
            "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"
    };
    public static String[] branches = {
            "المهندسين",
            "المعادي",
            "فيصل",
            "مدينة نصر",
            "مصر الجديدة",
            "اكتوبر",
            "حلوان",
            "اسكندرية",
            "المقطم",
            "مركزي"
    };
    public static String[] eventTypes = {
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
            "اجتماع",
            "اجتماع 2",
            "اجتماع 3",
            "اوتينج",
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
            "عزومة 2",
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
    public static String[] mosharkaTypes = {
            "استكشاف",
            "ولاد عم",
            "اجتماع",
            "اتصالات",
            "معرض / قافلة",
            "كرنفال",
            "نقل",
            "فرز",
            "سيشن / اورينتيشن",
            "دعايا",
            "اوتينج",
            "كامب",
            "عزومة",
            "توزيع مساعدات نفسك في ايه",
            "اداريات نفسك في ايه",
            "بلاغ مكافحة",
            "حملة مكافحة",
            "نزول الفرع",
            "اخري / بيت"
    };
    public static String[] meetingsTypesNormal = {
            "اجتماع لجنة فرق",
            "اجتماع لجنة معارض/اطفال",
            "اجتماع لجنة اطعام/مسنين",
            "اجتماع لجنة اعمار",
            "اجتماع لجنة ولاد عم",
            "اجتماع لجنة نفسك في ايه",
            "اجتماع لجنة hr",
            "اجتماع لجنة متابعة",
            "اجتماع لجنة اتصالات",
            "اجتماع لجنة اشبال",
            "اجتماع لجنة دعايا",
            "اجتماع لجنة مكافحة",
            "اجتماع لجنة نقل",
            "اجتماع لجان مجمع",
            "اجتماع ادارة الفرع",
            "اجتماع مسؤولين",
            "اجتماع نشيط",
            "اجتماع فريق عمل"
    };
    public static String[] meetingsTypesMarkzy = {
            "اجتماع مركزية فرق",
            "اجتماع مركزية معارض/اطفال",
            "اجتماع مركزية اطعام/مسنين",
            "اجتماع مركزية اعمار",
            "اجتماع مركزية ولاد عم",
            "اجتماع مركزية نفسك في ايه",
            "اجتماع مركزية hr",
            "اجتماع مركزية متابعة",
            "اجتماع مركزية اتصالات",
            "اجتماع مركزية اشبال",
            "اجتماع مركزية مكافحة",
            "اجتماع لجنة دعايا",
            "اجتماع مركزية نقل",
            "اجتماع ادارة التيكنيكال",
            "اجتماع ادارة التطوع",
            "اجتماع ادارة الجودة",
            "اجتماع هدود الفروع",
            "اجتماع ادارة فنية",
            "اجتماع مسؤولين النشاط",
            "اجتماع فريق عمل النشاط"
    };
    public static String[] meetingsReason = {
            "متابعة شغل الشهر", "تقييم الشغل", "حل مشكلة", "تواصل/ترفيه", "تخطيط"
    };
    public static String[] meetingsPlace = {"zoom النشاط", "الفرع", "اونلاين اخر"};
    public static HashMap<String, String> branchesSheets = new HashMap<>();
    public static HashMap<String, String> eventsImages = new HashMap<>();
    public static boolean isManager;
    public static String userId;
    public static String userBranch;
    public static int branchOrder;
    public static boolean isMrkzy;
    public static boolean isAdmin;
    public static HashMap<String, NormalVolunteer> allVolunteersByName = new HashMap<>();
    public static HashMap<String, NormalVolunteer> allVolunteersByPhone = new HashMap<>();
    public static HashMap<String, String> coursesImages = new HashMap<>();
    public static Rules myRules;

    public static void initializeConstantHashMaps() {
        fillEventsImages();
        fillSheets();
        fillCoursesImages();
    }

    public static String takyeemSheet = "1VuTdZ3el0o94Y9wqec0y90yxVG4Ko7PHTtrOm2EViOk";

    public static void fillSheets() {
        branchesSheets.put(branches[0], "1tsMZ5EwtKrBUGuLFVBvuwpU5ve0JKMsaqK1nNAONj-0"); // مهندسين
        branchesSheets.put(branches[1], "1IzyuwMrKap0uQutEKM0qcRpL3MlBxfd2tLv9jZ1M32o"); // معادي
//        branchesSheets.put(branches[2], "1twEafdr_bJgXoCmM26cQHZlPuCkYwntr8LJ5pPxreOI"); // فيصل
        branchesSheets.put(branches[2], "1X5D1sIfM7BR4ZtM6knHTt96STJMC8DK6_1a9O7DX5Ic"); // فيصل
//        branchesSheets.put(branches[3], "1x_yXMc32YxG62C_l1Bqis4Sjdy_PH2iGiAmL6w3DoTA"); // مدينة نصر
        branchesSheets.put(branches[3], "1c5Mayd4t7KThMomOTB4LJD4dTmKkv8g9TKbVM15Dsms"); // مدينة نصر
//        branchesSheets.put(branches[4], "1h8ApI25VevdkmRj_bRoYUwnbSdlGnm5lAVXpQlVte9Y"); // مصر الجديدة
        branchesSheets.put(branches[4], "1wramn0b32bE_tvW1VaT98vJ7_xDlQ22djEsRja_wzjc"); // مصر الجديدة
//        branchesSheets.put(branches[5], "1h6JOptK1SRHelRIHV-iqp1S3YhjdG8hHHLi0-phVulY"); // اكتوبر
        branchesSheets.put(branches[5], "1MkTi8BIrcfieEdlevXLuY3yEFpPXDI1P5mgUhmZJywM"); // اكتوبر
        branchesSheets.put(branches[6], "1o4MSMw7ip0BUNLkx5t0WOZrPM9SyNYsty6jJ4mtkyRo"); // حلوان
        branchesSheets.put(branches[7], "1U_F192CWif54Q9mWcYgPKM7662jCBXCU_COLPp0AaPQ"); // اسكندرية
        branchesSheets.put(branches[8], "1UrPaZyzfh7Vag_Cxb6tHkBXMKdHVThvDxya1SQYeKqI"); // المقطم
        // مركزي ملوش شيت
    }

//    public static boolean hasPermissions(String[] permissions, Context context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
//                && context != null
//                && permissions != null) {
//            for (String permission : permissions) {
//                if (ActivityCompat.checkSelfPermission(context, permission)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

    public static String timeToText(int sHour, int sMinute) {
        int Mhour;
        String Mminute;
        String am_pm;
        Mhour = sHour;
        Mminute = String.valueOf(sMinute);
        if (sMinute == 0) {
            Mminute = "00";
        }
        if (Mhour >= 12) {
            am_pm = "PM";
            Mhour = Mhour - 12;
        } else {
            am_pm = "AM";
        }
        return Mhour + ":" + Mminute + " " + am_pm;
    }

    public static String getPercentString(double percent) {
        return Math.round(percent * 10) / 10.0 + " %";
    }

    public static String dateToText(int dayOfMonth, int monthOfYear, int year) {
        return dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
    }

    public static boolean checkFutureDate(String date, int year, int month, int day) {
        String[] parts = date.split("/", 3);

        return Integer.parseInt(parts[2]) > year
                || Integer.parseInt(parts[2]) == year && Integer.parseInt(parts[1]) > month + 1
                || Integer.parseInt(parts[2]) == year && Integer.parseInt(parts[1]) == month + 1 && Integer.parseInt(parts[0]) > day;
    }

    public static boolean checkPastDate(String date, int year, int month, int day) {
        String[] parts = date.split("/", 3);

        return Integer.parseInt(parts[2]) < year || Integer.parseInt(parts[1]) < month + 1
                || (Integer.parseInt(parts[1]) == month + 1 && Integer.parseInt(parts[0]) < day);
    }

    public static boolean isBetween(int m, int start_month, int end_month, int d, int start_day, int end_day) {
        if (m > start_month && m < end_month) return true;
        if (m > end_month || m < start_month) return false;
        if (m == start_month && m != end_month) {
            return d >= start_day;
        } else if (m != start_month) {
            return d <= end_day;
        } else //same month
        {
            return d >= start_day && d <= end_day;
        }
    }

    public static int getWeekNum(int printStartDay) {
        switch (printStartDay) {
            case 1:
                return 1;
            case 8:
                return 2;
            case 15:
                return 3;
            case 22:
                return 4;
        }
        return 0;
    }

    public static void fillEventsImages() {
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

    private static void fillCoursesImages() {
        coursesImages.put(courseColors[0], "https://i.imgur.com/KziR4lT.jpeg");
        coursesImages.put(courseColors[1], "https://i.imgur.com/EVGYLud.jpeg");
        coursesImages.put(courseColors[2], "https://i.imgur.com/nopDz7z.jpeg");
        coursesImages.put(courseColors[3], "https://i.imgur.com/ssQjxN1.jpeg");
        coursesImages.put(courseColors[4], "https://i.imgur.com/g3URUe1.jpeg");
        coursesImages.put(courseColors[5], "https://i.imgur.com/T1j6SLM.jpeg");
        coursesImages.put(courseColors[6], "https://i.imgur.com/gYpp03z.jpeg");
        coursesImages.put(courseColors[7], "https://i.imgur.com/lpBuQrV.jpeg");
    }


}
