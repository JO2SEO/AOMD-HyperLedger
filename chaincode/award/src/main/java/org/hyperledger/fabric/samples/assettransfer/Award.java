package org.hyperledger.fabric.samples.assettransfer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.time.LocalDateTime;
import java.util.Objects;

@DataType
@Getter
public final class Award extends CommonAsset {
    @Property
    private String rank;

    public Award(
            @JsonProperty("id") final String id,
            @JsonProperty("title") final String title,
            @JsonProperty("ownerId") final Long ownerId,
            @JsonProperty("publisher") final String publisher,
            @JsonProperty("publishedAt") final LocalDateTime publishedAt,
            @JsonProperty("createdAt") final LocalDateTime createdAt,
            @JsonProperty("rank") final String rank
    ) {
        super(id, title, ownerId, publisher, publishedAt, createdAt);
        this.rank = rank;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Award award = (Award) o;
        return rank.equals(award.rank);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), rank);
    }

    @Override
    public String toString() {
        return "Award{"
                + "id='"
                + getId()
                + '\''
                + ", title='"
                + getTitle()
                + '\''
                + ", ownerId="
                + getOwnerId()
                + ", publisher='"
                + getPublisher()
                + '\''
                + ", publishedAt="
                + getPublishedAt()
                + ", createdAt="
                + getCreatedAt()
                + ", rank='"
                + rank
                + '\''
                + '}';
    }
}
