package com.LIB.MessagingSystem.Dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageCreatedResponseDto {
    private String status;
    private String message;
}
