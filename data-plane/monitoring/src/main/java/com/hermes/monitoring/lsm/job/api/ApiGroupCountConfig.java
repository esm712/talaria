package com.hermes.monitoring.lsm.job.api;

import com.hermes.monitoring.lsm.dto.LogDto;
import com.hermes.monitoring.lsm.dto.api.ApiCountDto;
import com.hermes.monitoring.lsm.parser.LogParser;
import com.hermes.monitoring.lsm.service.api.ApiGroupDbInsertService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ApiGroupCountConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final LogParser logParser = new LogParser();
    private final ApiGroupDbInsertService apiGroupDbInsertService;
    private List<LogDto> logDtoList;
    private Map<String, ApiCountDto> statusCount;


    @Value("${log.access.old.root}")
    String logFolderPath;

    @Bean
    public Job apiGroupCountJob() {
        // STEP 1: statusCode log file을 DtoList로 불러온다.
        // STEP 2: DtoList를 url,method,group,http status code로 분리한다.
        // STEP 3: DB에 넣는다.
        return jobBuilderFactory.get("apiGroupCount")
                .incrementer(new RunIdIncrementer())
                .start(apiGroupLogFileReaderStep(null))
                .next(mappingGroupStep())
                .next(dbInsertGroupStep(null))
                .build();
    }

    // STEP 1: log file을 DtoList로 불러온다.
    @Bean
    @JobScope
    public Step apiGroupLogFileReaderStep(@Value("#{jobParameters[statusCode]}") Long statusCode){
        return stepBuilderFactory.get("apiGroupLogFileReader")
                .tasklet((contribution, chunkContext) -> {
                    // 파일을 읽어 logDtoList로 변환
                    String filepath = logFolderPath + "access_" + statusCode.intValue() + ".log.1";
                    logDtoList = logParser.parseLog(filepath);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }


    // STEP 2: DtoList를 url,method,group,http status code로 분리한다.
    @Bean
    public Step mappingGroupStep(){
        return stepBuilderFactory.get("mappingGroup")
                .tasklet((contribution, chunkContext) -> {
                    // http status code로 분류
                    statusCount = new LinkedHashMap<>();
                    for(LogDto logDto : logDtoList){
                        String group = logDto.getGroup();
                        String statusCode = logDto.getHttpStatusCode();
                        String url = logDto.getUrl();
                        String method = logDto.getHttpMethod();
                        String key = group +" " + statusCode + " " + url + " " + method;
                        ApiCountDto dto = statusCount.get(key);
                        if(dto != null){
                            dto.up();
                        } else {
                            dto = new ApiCountDto(group, statusCode, url, method);
                            statusCount.put(key,dto);
                        }
                    }
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    @JobScope
    // STEP 3: DB에 넣는다.
    public Step dbInsertGroupStep(@Value("#{jobParameters[statusCode]}") Long statusCode){
        return stepBuilderFactory.get("dbInsertGroup")
                .tasklet((contribution, chunkContext) -> {
                    for(String key : statusCount.keySet()){
                        ApiCountDto apiCountDto = statusCount.get(key);
                        apiGroupDbInsertService.insert(statusCode, apiCountDto);
                    }
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
