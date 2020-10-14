package com.resala.mosharkaty.utility.classes;

import static com.resala.mosharkaty.NewAccountActivity.branches;

public class User {
    public String branch;
    public String code;
    public String name;

    public User() {
        this.branch = branches[9];
        this.code = " ";
        this.name = " ";
    }

    public User(String branch, String code, String name) {
        this.branch = branch;
        this.code = code;
        this.name = name;
    }
}
