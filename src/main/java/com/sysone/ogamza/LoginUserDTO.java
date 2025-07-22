package com.sysone.ogamza;

public class LoginUserDTO {
    private int id;
    private int departmentId;
    private String position;
    private String email;
    private String name;
    private int isAdmin;
    private String cardUid;

    public LoginUserDTO(int id, int departmentId, String position, String email,
                        String name, int isAdmin, String cardUid)
    {
        this.id = id;
        this.departmentId = departmentId;
        this.position = position;
        this.email = email;
        this.name = name;
        this.isAdmin = isAdmin;
        this.cardUid = cardUid;
    }

    // Getter
    public int getId() {return id;}
    public int getDepartmentId() {return departmentId;}
    public String getPosition() {return position;}
    public String getEmail() {return email;}
    public int getIsAdmin() {return isAdmin;}
    public String getCaredUid() {return cardUid;}
    public String getName() {return name;}
}
