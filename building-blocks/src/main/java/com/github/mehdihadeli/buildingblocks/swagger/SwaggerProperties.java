package com.github.mehdihadeli.buildingblocks.swagger;

import jakarta.validation.constraints.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties {

    /* The title of the API. */
    @NotBlank(message = "API title must not be blank")
    private String title;

    /* The version of the API. */
    @NotBlank(message = "API version must not be blank")
    private String version;

    /* A brief description of the API. */
    @NotBlank(message = "API description must not be blank")
    private String description;

    /* The name of the contact person for the API. */
    @NotBlank(message = "Contact name must not be blank")
    private String contactName;

    /* The email address of the contact person. */
    @Email(message = "Contact email must be a valid email address")
    private String contactEmail;

    /* The URL to more contact information (optional). */
    @Pattern(regexp = "^(http|https)://.*$", message = "Contact URL must be a valid URL starting with http or https")
    private String contactUrl;

    /* The license under which the API is offered (optional). */
    private String license;

    /* The URL pointing to the license information (optional). */
    @Pattern(regexp = "^(http|https)://.*$", message = "License URL must be a valid URL starting with http or https")
    private String licenseUrl;

    /* Flag to enable or disable Swagger. */
    @NotNull(message = "Swagger enabled flag must not be null")
    private Boolean enabled = true;

    /* The path for Swagger UI. Default is /swagger */
    private String uiPath = "/swagger";

    /* The path for OpenAPI specification. Default is /v3/openapi */
    private String openapiPath = "/v3/openapi";

    // Getters and Setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactUrl() {
        return contactUrl;
    }

    public void setContactUrl(String contactUrl) {
        this.contactUrl = contactUrl;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getUiPath() {
        return uiPath;
    }

    public void setUiPath(String uiPath) {
        this.uiPath = uiPath;
    }

    public String getOpenapiPath() {
        return openapiPath;
    }

    public void setOpenapiPath(String openapiPath) {
        this.openapiPath = openapiPath;
    }
}
