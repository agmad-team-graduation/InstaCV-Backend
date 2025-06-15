package com.Graduation.InstaCv.data.model.cv.sections;

import com.Graduation.InstaCv.data.model.cv.TailoredCv;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class CvSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "section_title")
    private String sectionTitle;

    @Column(name = "is_hidden")
    @Builder.Default
    private boolean isHidden = false;
}
