package models;

import lombok.*;

import java.util.ArrayList;
@Data
@ToString
public class UserDTO {
    private User user;
    private String jwt;
    @Data
    public class User {
        private int id;
        private String username;
        private ArrayList<Role> roles;
    }
    @Data
    public class Role {
        private int id;
        private String authority;
    }
}
