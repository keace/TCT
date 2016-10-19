package ua.kyslytsia.tct.mocks;

public class Person {
    private String surName;
    private String firstName;
    private String middleName;

    private int genderId;
    private String birthday;

    public Person(String surName, String firstName, String middleName, int genderId, String birthday) {
        this.surName = surName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.genderId = genderId;
        this.birthday = birthday;
    }

    public Person(String surName, String firstName) {
        this.surName = surName;
        this.firstName = firstName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public int getGenderId() {
        return genderId;
    }

    public void setGenderId(int genderId) {
        this.genderId = genderId;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "Person{" +
                "surName='" + surName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", genderId=" + genderId +
                ", birthday='" + birthday + '\'' +
                '}';
    }
}
