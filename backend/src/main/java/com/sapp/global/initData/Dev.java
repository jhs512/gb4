package com.sapp.global.initData;

import com.sapp.global.app.AppConfig;
import com.sapp.standard.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

@Profile("dev")
@Configuration
@RequiredArgsConstructor
public class Dev {
    @Bean
    @Order
    ApplicationRunner initDev() {
        return args -> {
            String backUrl = AppConfig.getSiteBackUrl();

            String downloadFilePath = Ut.file.downloadFileByHttp(backUrl + "/v3/api-docs/apiV1", ".");
            Ut.file.moveFile(downloadFilePath, "apiV1.json");

            StringBuilder sb = new StringBuilder();
            sb.append("npx --package typescript --package openapi-typescript openapi-typescript apiV1.json -o ../frontend/src/lib/backend/apiV1/schema.d.ts");
            sb.append(" && ");
            sb.append("rm -f apiV1.json");

            Ut.cmd.runAsync(sb.toString());
        };
    }
}