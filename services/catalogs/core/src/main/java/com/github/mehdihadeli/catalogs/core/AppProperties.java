package com.github.mehdihadeli.catalogs.core;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * Seed properties.
     */
    @NotNull(message = "Seed properties must not be null")
    private final Seed seed = new Seed();
    /**
     * The version of the application.
     */
    @NotBlank(message = "Application version must not be blank")
    private String version;
    /**
     * The name of the application.
     */
    @NotBlank(message = "Application name must not be blank")
    private String name;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Seed getSeed() {
        return seed;
    }

    public static class Seed {

        /**
         * Indicates whether seeding is enabled.
         */
        @NotNull(message = "Seed enabled flag must not be null")
        private Boolean enabled;

        public Boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }
}
