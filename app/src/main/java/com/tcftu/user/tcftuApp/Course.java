package com.tcftu.user.tcftuApp;

/**
 * Created by user on 2017/7/17.
 */

public class Course {
    // Labels table name
    public static final String TABLE = "Course";

    // Labels Table Columns names
    public static final String KEY_ROWID = "_id";
    public static final String KEY_courseID = "courseID";
    public static final String KEY_name = "name";
    public static final String KEY_teacher = "teacher";
    public static final String KEY_startApplyDate = "startApplyDate";
    public static final String KEY_applyDate = "applyDate";
    public static final String KEY_startEnd = "startEnd";
    public static final String KEY_classDay = "classDay";
    public static final String KEY_people = "people";
    public static final String KEY_totalHr = "totalHr";
    public static final String KEY_kAdd = "kAdd";
    public static final String KEY_sAdd = "sAdd";
    public static final String KEY_labor = "labor";
    public static final String KEY_contact = "contact";
    public static final String KEY_phone = "phone";
    public static final String KEY_aYear = "aYear";
    public static final String KEY_aMonth = "aMonth";
    public static final String KEY_aDay = "aDay";
    public static final String KEY_eYear = "eYear";
    public static final String KEY_eMonth = "eMonth";
    public static final String KEY_eDay = "eDay";


    // property help us to keep data
    public String courseID;
    public String name;
    public String teacher;
    public String applyDate;
    public String startApplyDate;
    public String startEnd;
    public String classDay;
    public String people;
    public String totalHr;
    public String kAdd;
    public String sAdd;
    public String labor;
    public String contact;
    public String phone;
    public int aYear;
    public int aMonth;
    public int aDay;
    public int eYear;
    public int eMonth;
    public int eDay;
}

