package com.hnc.mogak;
import com.hnc.mogak.global.cloud.S3Config;
import com.hnc.mogak.global.cloud.S3Service;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MockConfig {

    @Bean
    public S3Config s3Config() {
        return Mockito.mock(S3Config.class);
    }
    @Bean
    public S3Service s3Service() {
        return Mockito.mock(S3Service.class);
    }

}