package com.schoolexam.dto;

public class AuthDtos {

    public static class LoginRequest {
        private String username;
        private String password;

        public LoginRequest() {}

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private String token;
        private String username;
        private String fullName;
        private String role;

        public LoginResponse() {}

        public LoginResponse(String token, String username, String fullName, String role) {
            this.token = token;
            this.username = username;
            this.fullName = fullName;
            this.role = role;
        }

        public static LoginResponseBuilder builder() {
            return new LoginResponseBuilder();
        }

        public static class LoginResponseBuilder {
            private String token;
            private String username;
            private String fullName;
            private String role;

            public LoginResponseBuilder token(String token) { this.token = token; return this; }
            public LoginResponseBuilder username(String username) { this.username = username; return this; }
            public LoginResponseBuilder fullName(String fullName) { this.fullName = fullName; return this; }
            public LoginResponseBuilder role(String role) { this.role = role; return this; }

            public LoginResponse build() {
                return new LoginResponse(token, username, fullName, role);
            }
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class RegisterRequest {
        private String username;
        private String password;
        private String fullName;
        private String role;

        public RegisterRequest() {}

        public RegisterRequest(String username, String password, String fullName, String role) {
            this.username = username;
            this.password = password;
            this.fullName = fullName;
            this.role = role;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
