package spring.reborn.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@AllArgsConstructor
public class GetUserInformRes {
    private int userIdx;
    private String userNickname;
    private String userAddress;
    private String userBirthDate;
    @Enumerated(EnumType.STRING)
    private String userLikes;
}
