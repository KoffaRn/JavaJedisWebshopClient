package models;

import lombok.*;

import java.util.ArrayList;
@Data
@ToString
public class UserDTO {
    private User user;
    private String jwt;
    @Data
    public static class User {
        private int id;
        private String username;
        private ArrayList<Role> roles;
        @Override
        public String toString() {
            return "User:\n" +
                    "username='" + username + '\n' +
                    "roles=" + roles;
        }
    }
    @Data
    public static class Role {
        private int id;
        private String authority;
        @Override
        public String toString() {
            return authority + ", ";
        }
    }
}
