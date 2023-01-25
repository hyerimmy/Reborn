package spring.reborn.domain.reborn.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class GetHistoryRes {
    private int rebornTaskIdx;
    private String storeName;
    private float storeScore;
    private String storeAddress;
    private String createdAt;
}
