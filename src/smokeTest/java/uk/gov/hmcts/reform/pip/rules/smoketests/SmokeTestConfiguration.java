package uk.gov.hmcts.reform.pip.rules.smoketests;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ComponentScan("uk.gov.hmcts.reform.pip.rules.smoketests")
@PropertySource("application.properties")
public class SmokeTestConfiguration {

  @Test
  public void exampleOfTest() {
      assertTrue(System.currentTimeMillis() > 0, "Example of Unit Test");
  }

}
