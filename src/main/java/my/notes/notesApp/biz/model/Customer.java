package my.notes.notesApp.biz.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Customer {

    @Id
    @GeneratedValue
    private Long id;
    private String userName;
    private String email;

    protected Customer() {}

    public Customer(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%d, userName='%s', email='%s']",
                id, userName, email);
    }

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }
}