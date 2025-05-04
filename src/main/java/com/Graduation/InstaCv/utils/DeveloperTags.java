package com.Graduation.InstaCv.utils;

import java.util.List;

/**
 * Constants class containing developer-related tags used for job filtering
 */
public final class DeveloperTags {

    /**
     * Comprehensive list of development-related tags for filtering developer jobs
     */
    public static final List<String> DEV_TAGS = List.of(
            // General Development Terms
            "dev", "developer", "software", "programming", "engineer", "coder", "programmer",
            "development", "engineering", "technical", "tech", "code", "coding",

            // Programming Languages
            "java", "javascript", "python", "ruby", "go", "golang", "php", "scala", "kotlin", "swift",
            "typescript", "c", "c++", "c#", "csharp", "rust", "dart", "perl", "r", "groovy",
            "bash", "shell", "powershell", "objective-c", "clojure", "haskell", "elixir", "erlang",
            "julia", "lua", "matlab", "fortran", "assembly", "cobol", "vba", "delphi", "pascal",
            "lisp", "scheme", "prolog", "ada", "solidity", "sql", "plsql", "tsql",

            // Web Development
            "web", "html", "css", "scss", "sass", "less", "stylus", "tailwind", "bootstrap",
            "frontend", "backend", "fullstack", "full-stack", "front-end", "back-end", "spa",
            "pwa", "amp", "responsive", "webgl", "dom", "websocket", "canvas", "svg",

            // Frameworks & Libraries
            "react", "angular", "vue", "svelte", "ember", "backbone", "jquery", "nextjs", "nuxt",
            "gatsby", "expressjs", "nestjs", "spring", "rails", "django", "flask", "laravel",
            "symfony", "yii", "phoenix", "fastapi", "asp.net", "dotnet", "struts", "play",

            // Mobile Development
            "mobile", "android", "ios", "swift", "reactnative", "flutter", "xamarin", "ionic",
            "cordova", "capacitor", "nativescript", "kotlin", "androidsdk", "objectivec",

            // DevOps & Infrastructure
            "devops", "sre", "infrastructure", "cloud", "aws", "azure", "gcp", "docker", "kubernetes",
            "k8s", "terraform", "ansible", "chef", "puppet", "jenkins", "gitlab", "cicd", "ci/cd",

            // Database & Data
            "database", "db", "sql", "nosql", "mongodb", "postgresql", "mysql", "oracle", "sqlserver",
            "redis", "elasticsearch", "cassandra", "dynamodb", "firebase", "neo4j", "couchdb",
            "mariadb", "sqlite", "graphql", "rest", "api", "microservices",

            // AI & Data Science
            "ai", "ml", "machinelearning", "datascience", "deeplearning", "nlp", "cv", "computervision",
            "tensorflow", "pytorch", "keras", "scikit", "pandas", "numpy", "jupyter", "hadoop", "spark",

            // Other Tech Domains
            "iot", "embedded", "gamedev", "gamedevelopment", "unity", "unreal", "blockchain", "crypto",
            "vr", "ar", "xr", "security", "cybersecurity", "pentesting", "qa", "testing", "automation"
    );

    // Private constructor to prevent instantiation
    private DeveloperTags() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}
