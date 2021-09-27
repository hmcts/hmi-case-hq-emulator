package uk.gov.hmcts.reform.casehqemulator.smoketests;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@ComponentScan("uk.gov.hmcts.reform.casehqemulator.smoketests")
@PropertySource("application.properties")
public class SmokeTestConfiguration {
}
