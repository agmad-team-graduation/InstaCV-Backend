package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.RemoteOkJobResponse;
import com.Graduation.InstaCv.gateways.externalJobs.RemoteOkApiClient;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;

import static com.Graduation.InstaCv.utils.DeveloperTags.DEV_TAGS;

@Service
@RequiredArgsConstructor
public class RemoteOKJobScrappingService {
    @Value("${external.jobs.api.remoteok}")
    private String REMOTE_OK_API_URL;
    private final RemoteOkApiClient remoteOkApiClient;

    public List<RemoteOkJobResponse> getItJobs(int lastDaysCount) {
        return getAllJobs().stream().filter(job -> isPostedWithinDaysCount(job, lastDaysCount)).toList();
    }

    private List<RemoteOkJobResponse> getAllJobs() {
        try {
            List<RemoteOkJobResponse> jobs = remoteOkApiClient.getRemoteOkJobs();
            if (jobs.isEmpty()) return Collections.emptyList();
            return jobs.stream().skip(1)
                    .filter(this::isDeveloperJob)
                    .peek(job -> {
                        job.setHtmlDescription(job.getDescription());
                        job.setDescription(cleanJobDescription(job.getDescription()));
                    }).toList();
        } catch (Exception e) {
            System.err.println("Error fetching dev jobs: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private String cleanJobDescription(String htmlDescription) {
        if (htmlDescription == null || htmlDescription.isEmpty()) {
            return "";
        }

        try {
            // Clean the HTML - keep only basic formatting
            String cleanedHtml = Jsoup.clean(htmlDescription, Safelist.basic());
            Document cleanDoc = Jsoup.parse(cleanedHtml);

            // Format lists specially
            for (Element li : cleanDoc.select("li")) {
                li.before(new TextNode("• "));
            }

            return cleanDoc.text()
                    .replaceAll("•", "\n• ") // Put each bullet point on a new line
                    .replaceAll("\\s{2,}", " ")  // Remove extra spaces
                    .replaceAll(" \\n", "\n")    // Clean up spaces before newlines
                    .replaceAll("\\n{3,}", "\n\n");
        } catch (Exception e) {
            System.err.println("Error cleaning job description: " + e.getMessage());
            return htmlDescription;
        }
    }

    private boolean isDeveloperJob(RemoteOkJobResponse job) {
        if (job.getTags() == null) return false;
        return job.getTags().stream().anyMatch(tag -> DEV_TAGS.contains(tag.toLowerCase()));
    }

    private boolean isPostedWithinDaysCount(RemoteOkJobResponse job, int lastDaysCount) {
        OffsetDateTime jobDate = job.getDate();
        if (jobDate == null) return false;
        OffsetDateTime thresholdDate = OffsetDateTime.now().minusDays(lastDaysCount);
        return jobDate.isAfter(thresholdDate);
    }
}