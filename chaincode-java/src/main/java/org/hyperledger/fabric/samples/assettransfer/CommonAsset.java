package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.annotation.JsonProperty;
import lombok.Getter;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.time.LocalDateTime;
import java.util.Objects;

@DataType
@Getter
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
            @JsonProperty("id") String id,
            @JsonProperty("title") String title,
            @JsonProperty("ownerId") Long ownerId,
            @JsonProperty("publisher") String publisher,
            @JsonProperty("publishedAt") LocalDateTime publishedAt,
            @JsonProperty("createdAt") LocalDateTime createdAt
    ) {
        this.id = id;
        this.title = title;
        this.ownerId = ownerId;
        this.publisher = publisher;
        this.publishedAt = publishedAt;
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommonAsset that = (CommonAsset) o;
        return id.equals(that.id) && title.equals(that.title) && ownerId.equals(that.ownerId) && publisher.equals(that.publisher) && publishedAt.equals(that.publishedAt) && createdAt.equals(that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, ownerId, publisher, publishedAt, createdAt);
    }
}
