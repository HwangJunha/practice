package com.around.practice.dto;

import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.data.annotation.Id;

public class HttpTraceWrapper {
    private @Id String id;
    private HttpTrace httpTrace;

    public HttpTraceWrapper(HttpTrace httpTrace) { // <3>
        this.httpTrace = httpTrace;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HttpTrace getHttpTrace() {
        return httpTrace;
    }

    public void setHttpTrace(HttpTrace httpTrace) {
        this.httpTrace = httpTrace;
    }
}
