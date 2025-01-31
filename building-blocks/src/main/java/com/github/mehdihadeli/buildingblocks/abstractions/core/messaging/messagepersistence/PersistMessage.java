package com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "persist_messages")
public class PersistMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String dataType;

    @Column(nullable = false)
    private String data;

    @Column(nullable = false)
    private LocalDateTime created = LocalDateTime.now();

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
        this.id = id;
        this.dataType = dataType;
        this.data = data;
        this.deliveryType = deliveryType;
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
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
