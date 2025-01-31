package com.github.mehdihadeli.buildingblocks.validation;

import com.github.mehdihadeli.buildingblocks.problemdetails.ProblemDetailsConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "validation")
@ConditionalOnBean({ProblemDetailsConfiguration.class})
public class ValidationProperties {

    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
