package com.sysone.ogamza;


public class LoginUserDTO {
    private int id;
    private String deptName;
    private String position;
    private String email;
    private String name;
    private int isAdmin;
    private String cardUid;
    private String profile;


    public LoginUserDTO(int id, String deptName, String position, String email,
                        String name, int isAdmin, String cardUid, String profile)
    {
        this.id = id;
        this.deptName = deptName;
        this.position = position;
        this.email = email;
        this.name = name;
        this.isAdmin = isAdmin;
        this.cardUid = cardUid;
        this.profile = profile;
    }

    // Getter
    public int getId() {return id;}
    public String getDeptName() {return deptName;}
    public String getPosition() {return position;}
    public String getEmail() {return email;}
    public int getIsAdmin() {return isAdmin;}
    public String getCaredUid() {return cardUid;}
    public String getName() {return name;}
    public String getProfile() {return profile;}
}
