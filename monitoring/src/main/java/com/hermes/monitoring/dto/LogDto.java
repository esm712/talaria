package com.hermes.monitoring.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@NoArgsConstructor
public class LogDto {
    String ip;
    Date date;
    String httpMethod;
    String group;
    String url;
    String httpVersion;
    String httpStatusCode;
    Double requestTime;
    Double responseTime;

    public LogDto(String ip, Date date, String httpMethod, String group, String url, String httpVersion, String httpStatusCode, double requestTime, double responseTime) {
        this.ip = ip;
        this.date = date;
        this.httpMethod = httpMethod;
        this.group = group;
        this.url = url;
        this.httpVersion = httpVersion;
        this.httpStatusCode = httpStatusCode;
        this.requestTime = requestTime;
        this.responseTime = responseTime;
    }
}
