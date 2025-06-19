package com.hnc.mogak.global.monitoring;

public enum QueryType {
    SELECT,
    INSERT,
    UPDATE,
    DELETE,
    UNKNOWN;

    public static QueryType from(String sql) {
        if (sql == null || sql.isBlank()) {
            return UNKNOWN;
        }

        String upperCaseSql = sql.trim().toUpperCase();
        if (upperCaseSql.startsWith("SELECT")) {
            return SELECT;
        } else if (upperCaseSql.startsWith("INSERT")) {
            return INSERT;
        } else if (upperCaseSql.startsWith("UPDATE")) {
            return UPDATE;
        } else if (upperCaseSql.startsWith("DELETE")) {
            return DELETE;
        } else {
            return UNKNOWN;}
    }
}
