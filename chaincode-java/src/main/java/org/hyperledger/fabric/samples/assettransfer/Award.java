package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.annotation.JsonProperty;
import lombok.Getter;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.time.LocalDateTime;
import java.util.Objects;

@DataType
@Getter
public class Award extends CommonAsset{
    @Property
    private String rank;

    public Award(
            @JsonProperty("id") String id,
            @JsonProperty("title") String title,
            @JsonProperty("ownerId") Long ownerId,
            @JsonProperty("publisher") String publisher,
            @JsonProperty("publishedAt") LocalDateTime publishedAt,
            @JsonProperty("createdAt") LocalDateTime createdAt,
            @JsonProperty("rank") String rank
    ) {
        super(id, title, ownerId, publisher, publishedAt, createdAt);
        this.rank = rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Award award = (Award) o;
        return rank.equals(award.rank);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), rank);
    }

    @Override
    public String toString() {
        return "Award{" +
                "id='" + super.getId() + '\'' +
                ", title='" + super.getTitle() + '\'' +
                ", ownerId=" + super.getOwnerId() +
                ", publisher='" + super.getPublisher() + '\'' +
                ", publishedAt=" + super.getPublisher() +
                ", createdAt=" + super.getCreatedAt() +
                ", rank='" + rank + '\'' +
                '}';
    }
}
