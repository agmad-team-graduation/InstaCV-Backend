package com.Graduation.InstaCv.data.model.cv;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "experience_sections")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<ExperienceCv> items;

    @Column(name = "order_index")
    private int orderIndex;
} 