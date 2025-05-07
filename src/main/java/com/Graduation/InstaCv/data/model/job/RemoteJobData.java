package com.Graduation.InstaCv.data.model.job;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "remote_job_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"job"})
@EqualsAndHashCode(exclude = {"job"})
public class RemoteJobData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "remote_id")
    private String remoteId;  // Original ID from RemoteOK

    @Column(name = "apply_url", length = 2000)
    private String applyUrl;

    private String date;

    @OneToOne
    @JoinColumn(name = "job_id")
    private Job job;
}