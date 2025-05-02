package com.Graduation.InstaCv.data.model.cv;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "education_sections")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<EducationCv> items;

    @Column(name = "order_index")
    private int orderIndex;
} 