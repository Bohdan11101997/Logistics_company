package edu.netcracker.project.logistic.model;

public class Office {
    private Long officeId;
    private String  name;
    private Address address;
    private Long  count;



    public Office(String name, Address address) {

        this.name = name;
        this.address = address;
    }

    public Office(Long officeId, String name, Address address) {
        this.officeId = officeId;
        this.name = name;
        this.address = address;
    }

    public Office()
    {


    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Office{" +
                "officeId=" + officeId +
                ", name='" + name + '\'' +
                ", address=" + address +
                '}';
    }
}
