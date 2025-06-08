package com.Graduation.InstaCv.utils;


import com.Graduation.InstaCv.data.enums.JobSortField;
import com.Graduation.InstaCv.data.model.job.Job;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobsPaginationUtils {
    public Pageable buildPageable(int page, int size, JobSortField sortField, String direction) {
        int validatedSize = Math.min(size, 30);
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        if (sortField.isCustomSort())
            return PageRequest.of(page, validatedSize, Sort.by(sortDirection, sortField.name()));
        return PageRequest.of(page, validatedSize, Sort.by(sortDirection, sortField.getDbField()));
    }

    public Page<Job> createPageFromList(List<Job> sorted, Pageable pageable) {
        int total = sorted.size();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), total);

        List<Job> content;
        if (start > total) {
            content = List.of();
        } else {
            content = sorted.subList(start, end);
        }

        return new PageImpl<>(content, pageable, total);
    }
}
