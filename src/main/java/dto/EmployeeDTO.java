package dto;

public class EmployeeDTO {
    private int id;
    private String name;
    private String email;
    private int isAdmin;
    private int departmentId;

    public EmployeeDTO(int id, String name, String email, int isAdmin, int departmentId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.isAdmin = isAdmin;
        this.departmentId = departmentId;
    }

    public int getId() {return id;}
    public String getName() {return name;}
    public String getEmail() {return email;}
    public int getIsAdmin() {return isAdmin;}
    public int getDepartmentId() {return departmentId;}
}
