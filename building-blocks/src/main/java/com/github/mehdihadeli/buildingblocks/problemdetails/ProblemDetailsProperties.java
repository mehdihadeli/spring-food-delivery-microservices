package com.github.mehdihadeli.buildingblocks.problemdetails;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "problem-details")
@ConditionalOnBean({ProblemDetailsConfiguration.class})
public class ProblemDetailsProperties {

    private boolean enabled = true;
    private boolean includeStackTrace = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isIncludeStackTrace() {
        return includeStackTrace;
    }

    public void setIncludeStackTrace(boolean includeStackTrace) {
        this.includeStackTrace = includeStackTrace;
    }
}
