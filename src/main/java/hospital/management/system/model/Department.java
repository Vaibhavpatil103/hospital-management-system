package hospital.management.system.model;

import java.time.LocalDateTime;

public class Department {
    private int deptId;
    private String deptName;
    private String headDoctor;
    private String phone;
    private LocalDateTime createdAt;

    public Department() {}

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getHeadDoctor() {
        return headDoctor;
    }

    public void setHeadDoctor(String headDoctor) {
        this.headDoctor = headDoctor;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
