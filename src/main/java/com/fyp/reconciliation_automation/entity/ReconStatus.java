package com.fyp.reconciliation_automation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "recon", name = "recon_status")
public class ReconStatus {
    @Id
    @Column(name = "recon_id", nullable = false)
    private String reconId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "upload_date", nullable = false)
    private LocalDate uploadDate;
}