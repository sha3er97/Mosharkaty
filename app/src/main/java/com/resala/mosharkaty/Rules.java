package com.resala.mosharkaty;

class Rules {
    public int attendance_bad;
    public int attendance_medium;
    public boolean show_official;
    public int last_important_update;
    public int mas2ool_points;
    public int mashroo3_points;
    public int medium_average;
    public int bad_average;
    public int big_font;
    public String vol_mohndseen;
    public String supervisor_email;
    public String daily_email_body;
    public boolean takyeem_available;
    public String manager_password;
    public String from_home;
    public String from_far3;

    public Rules() {
        // in case of any error
        attendance_bad = 50;
        attendance_medium = 80;
        bad_average = 3;
        medium_average = 5;
        big_font = 64;
        show_official = false;
        last_important_update = 0;
        mas2ool_points = 0;
        mashroo3_points = 0;
        vol_mohndseen = "";
        supervisor_email = "";
        daily_email_body = "";
        takyeem_available = false;
        manager_password = "manager";
        from_home = "في البيت";
        from_far3 = "في الفرع";
    }
}
