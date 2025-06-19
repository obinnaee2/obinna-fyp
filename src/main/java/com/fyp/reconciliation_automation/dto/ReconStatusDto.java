package com.fyp.reconciliation_automation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReconStatusDto {
    private String reconId;
    private String status;
    private LocalDate uploadDate;

}