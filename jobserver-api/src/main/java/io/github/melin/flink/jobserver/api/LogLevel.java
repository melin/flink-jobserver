package io.github.melin.flink.jobserver.api;

/**
 * Created by admin on 2018/7/30.
 */
public enum LogLevel {
    STDOUT("Stdout"), INFO("Info"), WARN("Warn"), ERROR("Error"), APPINFO("AppInfo");

    private final String type;

    LogLevel(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}
