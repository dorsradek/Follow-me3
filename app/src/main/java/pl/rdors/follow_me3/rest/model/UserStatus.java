package pl.rdors.follow_me3.rest.model;

import java.io.Serializable;

public class UserStatus implements Serializable {

    private Long id;
    private int status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}