package com.ssafy.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Message {
    private String auth;
    private String remainTime;
    private String exerciseType;
    private String myCount;
    private String peerCount;
    private String peerToken;
    private String sender;    // 보내는 유저 UUID
    private String type;      // 메시지 타입
    private String receiver;  // 받는 사람
    private String room;      // roomId
    private Object candidate; // 상태
    private Object sdp;       // sdp 정보
    private Object allUsers;  // 해당 방에 본인을 제외한 전체 유저
}