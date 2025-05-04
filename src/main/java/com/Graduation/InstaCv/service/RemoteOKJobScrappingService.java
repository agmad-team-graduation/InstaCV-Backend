package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.RemoteOkJobDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.Graduation.InstaCv.utils.DeveloperTags.DEV_TAGS;

@Service
public class RemoteOKJobScrappingService {

    private final RestTemplate restTemplate;
    private static final String REMOTE_OK_API_URL = "https://remoteok.com/api";

    @Autowired
    public RemoteOKJobScrappingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    // Helper method to check if a job is a developer job
    private boolean isDeveloperJob(RemoteOkJobDto job) {
        if (job.getTags() == null) return false;

        for (String tag : job.getTags()) {
            String lowerTag = tag.toLowerCase();
            if (DEV_TAGS.stream().anyMatch(lowerTag::contains)) {
                return true;
            }
        }

        return false;
    }

    // Helper method to check if a job was posted within the last two weeks
    private boolean isPostedWithinLastTwoWeeks(RemoteOkJobDto job) {
        if (job.getDate() == null || job.getDate().isEmpty()) return false;

        try {
            // Parse ISO 8601 date string
            LocalDateTime jobDate = LocalDateTime.parse(
                    job.getDate().replace("Z", "").replace("+00:00", ""),
                    DateTimeFormatter.ISO_DATE_TIME);

            // Get date from two weeks ago
            LocalDateTime twoWeeksAgo = LocalDateTime.now().minus(2, ChronoUnit.WEEKS);

            // Return true if job date is after two weeks ago
            return jobDate.isAfter(twoWeeksAgo);
        } catch (Exception e) {
            System.err.println("Error parsing date: " + job.getDate() + " - " + e.getMessage());
            return false; // If we can't parse the date, we exclude it from recent jobs
        }
    }

    public List<RemoteOkJobDto> getDevJobs() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<RemoteOkJobDto[]> response = restTemplate.exchange(
                    REMOTE_OK_API_URL,
                    HttpMethod.GET,
                    entity,
                    RemoteOkJobDto[].class
            );

            RemoteOkJobDto[] jobs = response.getBody();

            if (jobs == null || jobs.length == 0) {
                return Collections.emptyList();
            }

            // Skip the first element (it's metadata), filter for dev jobs
            return processJobDescriptions(Arrays.stream(jobs)
                    .skip(1)
                    .filter(this::isDeveloperJob)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error fetching dev jobs: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    public List<RemoteOkJobDto> getFilteredDevJobs(String tech, boolean recent) {
        // First get the base list depending on whether we want recent or all jobs
        List<RemoteOkJobDto> baseJobList;
        if (recent) {
            baseJobList = getDevJobs().stream()
                    .filter(this::isPostedWithinLastTwoWeeks)
                    .collect(Collectors.toList());
        } else {
            baseJobList = getDevJobs();
        }

        // Apply technology filter if needed
        List<RemoteOkJobDto> filteredJobs;
        if (tech != null && !tech.trim().isEmpty()) {
            filteredJobs = baseJobList.stream()
                    .filter(job -> job.getTags() != null &&
                            job.getTags().stream()
                                    .anyMatch(tag -> tag.toLowerCase().contains(tech.toLowerCase())))
                    .collect(Collectors.toList());
        } else {
            filteredJobs = baseJobList;
        }
        return filteredJobs;
    }

    private String cleanJobDescription(String htmlDescription) {
        if (htmlDescription == null || htmlDescription.isEmpty()) {
            return "";
        }

        try {
            // Parse the HTML
            Document doc = Jsoup.parse(htmlDescription);

            // Clean the HTML - keep only basic formatting
            String cleanedHtml = Jsoup.clean(htmlDescription, Safelist.basic());
            Document cleanDoc = Jsoup.parse(cleanedHtml);

            // Format lists specially
            for (Element li : cleanDoc.select("li")) {
                li.before(new TextNode("• "));
            }

            // Get text and improve formatting
            String plainText = cleanDoc.text()
                    .replaceAll("•", "\n• ") // Put each bullet point on a new line
                    .replaceAll("\\s{2,}", " ")  // Remove extra spaces
                    .replaceAll(" \\n", "\n")    // Clean up spaces before newlines
                    .replaceAll("\\n{3,}", "\n\n"); // Limit consecutive newlines

            return plainText;
        } catch (Exception e) {
            System.err.println("Error cleaning job description: " + e.getMessage());
            // Return the original if parsing fails
            return htmlDescription;
        }
    }

    private List<RemoteOkJobDto> processJobDescriptions(List<RemoteOkJobDto> jobs) {
        jobs.forEach(job -> {
            if (job.getDescription() != null) {
                job.setDescription(cleanJobDescription(job.getDescription()));
            }
        });
        return jobs;
    }
}