package com.Graduation.InstaCv.utils;

import java.util.List;
import java.util.Set;

/**
 * Constants class containing developer-related tags used for job filtering
 */
public final class DeveloperTags {

    /**
     * Comprehensive list of development-related tags for filtering developer jobs
     */
    public static final Set<String> DEV_TAGS = Set.of(
            "developer", "backend", "front‑end", "full‑stack", "sys‑admin",
            "ops", "devops", "cloud", "serverless",
            "api", "software", "analyst", "architect",
            "javascript", "python", "java", "golang",
            "c", "c‑plus‑plus", "c‑sharp", "ruby", "php",
            "scala", "objective‑c", "node", "angular", "react",
            "react‑native", "vue", "graphql", "linux", "docker",
            "git", "sql", "nosql", "postgres", "mongo", "apache",
            "testing", "quality‑assurance", "infosec", "data‑science", "machine‑learning",
            "blockchain", "web3", "saas", "ecommerce", "mobile", "ios", "android", "web"
    );

    // Private constructor to prevent instantiation
    private DeveloperTags() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}
