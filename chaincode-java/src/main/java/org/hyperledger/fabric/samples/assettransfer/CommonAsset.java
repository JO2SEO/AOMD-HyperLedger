package org.hyperledger.fabric.samples.assettransfer;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.time.LocalDateTime;
import java.util.Objects;

@DataType
public abstract class CommonAsset {
    @Property
    private String id;
    @Property
    private String title;
    @Property
    private Long ownerId;
    @Property
    private String publisher;
    @Property
    private LocalDateTime publishedAt;
    @Property
    private LocalDateTime createdAt;

    public CommonAsset(
            @JsonProperty("id") final String id,
            @JsonProperty("title") final String title,
            @JsonProperty("ownerId") final Long ownerId,
            @JsonProperty("publisher") final String publisher,
            @JsonProperty("publishedAt") final LocalDateTime publishedAt,
            @JsonProperty("createdAt") final LocalDateTime createdAt
    ) {
        this.id = id;
        this.title = title;
        this.ownerId = ownerId;
        this.publisher = publisher;
        this.publishedAt = publishedAt;
        this.createdAt = createdAt;
    }

    /**
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CommonAsset that = (CommonAsset) o;
        return id.equals(that.id) && title.equals(that.title) && ownerId.equals(that.ownerId) && publisher.equals(that.publisher) && publishedAt.equals(that.publishedAt) && createdAt.equals(that.createdAt);
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, title, ownerId, publisher, publishedAt, createdAt);
    }

    /**
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @return
     */
    public Long getOwnerId() {
        return ownerId;
    }

    /**
     *
     * @return
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     *
     * @return
     */
    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    /**
     *
     * @return
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
