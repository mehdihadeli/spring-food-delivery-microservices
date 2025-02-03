package com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence;

import com.github.mehdihadeli.buildingblocks.core.data.AuditableEntityDataModel;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "persist_messages")
public class PersistMessage extends AuditableEntityDataModel {
    @Column(nullable = false)
    private String dataType;

    // for long text without length limit we should use `@Lob` which is equal `TEXT` in postgres
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String data;

    @Column(nullable = false)
    private int retryCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus messageStatus = MessageStatus.Stored;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageDeliveryType deliveryType;

    // Default constructor for JPA
    public PersistMessage() {}

    public PersistMessage(UUID id, String dataType, String data, MessageDeliveryType deliveryType) {
        this.setId(id);
        this.dataType = dataType;
        this.data = data;
        this.deliveryType = deliveryType;
    }

    // Getters and setters
    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public MessageStatus getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

    public MessageDeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(MessageDeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    // Business methods
    public void changeState(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

    public void increaseRetry() {
        this.retryCount++;
    }
}
