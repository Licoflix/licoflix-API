package com.licoflix.core.domain.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsResponse {
    private Long id;
    private String email;
    private String nickname;
    private String token;
    private boolean authenticated;
    private String createdIn;
    private String changedIn;
    private String createdBy;
    private String changedBy;
    private boolean admin;
    private boolean deleted;

    @Override
    public String toString() {
        return "Id: " + id + ", " +
                "Email: " + email + ", " +
                "Nickname: " + nickname + ", " +
                "Authenticated: " + authenticated + ", " +
                "Deleted: " + deleted;
    }
}