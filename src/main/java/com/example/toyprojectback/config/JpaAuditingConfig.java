package com.example.toyprojectback.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
// 역할 : JPA Auditing 활성화
//JPA Auditing? : 생성일, 수정일 자동화
public class JpaAuditingConfig {

}
