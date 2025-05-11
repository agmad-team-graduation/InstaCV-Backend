package com.Graduation.InstaCv.data.model.job;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @ElementCollection
    private List<String> tags;

    @OneToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @JsonIgnore
    public String getModifiedDescription() {
        StringBuilder description = new StringBuilder(job.getDescription());
        if (tags != null && !tags.isEmpty()) {
            description.append("\n\nTags:");
            for (String tag : tags) description.append(" ").append(tag);
        }
        return description.toString();
    }
}