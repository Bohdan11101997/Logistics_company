package edu.netcracker.project.logistic.model;

public class OfficeSearchForm {

    private String department;
    private  String address;
    private Long sort_ids;

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getSort_ids() {
        return sort_ids;
    }

    public void setSort_ids(Long sort_ids) {
        this.sort_ids = sort_ids;
    }
}
