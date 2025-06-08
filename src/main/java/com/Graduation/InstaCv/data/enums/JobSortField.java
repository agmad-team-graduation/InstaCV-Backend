package com.Graduation.InstaCv.data.enums;

public enum JobSortField {
    DATE("remoteJobData.date"),
    MATCH_SCORE(null), // special case: sorted in memory
    TITLE("title");

    private final String dbField;

    JobSortField(String dbField) {
        this.dbField = dbField;
    }

    public String getDbField() {
        return dbField;
    }

    public boolean isCustomSort() {
        return this == MATCH_SCORE;
    }
}
