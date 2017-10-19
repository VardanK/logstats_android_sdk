package org.yecup.logstatssdk.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import org.yecup.logstatssdk.data.LogEntry;

/**
 * Created by vkurkchiyan on 10/10/17.
 */

public class LogBufferRequest {
    @SerializedName("messages")
    private List<LogEntry> entries;

    @SerializedName("project")
    private String token;

    public LogBufferRequest(String token, List<LogEntry> entries){
        this.token = token;
        this.entries = entries;
    }
}
