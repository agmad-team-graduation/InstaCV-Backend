package com.Graduation.InstaCv.data.model.cv.sections;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "summary_sections")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SummarySection extends CvSection {
    @Column(name = "summary", length = 2000)
    String summary;
}
