package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.annotation.JsonProperty;
import lombok.Getter;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.time.LocalDateTime;
import java.util.Objects;

@DataType
@Getter
public final class Education extends CommonAsset {
    @Property
    private String state;
    @Property
    private String departmentInfo;

    public Education(
            @JsonProperty("id") String id,
            @JsonProperty("title") String title,
            @JsonProperty("ownerId") Long ownerId,
            @JsonProperty("publisher") String publisher,
            @JsonProperty("publishedAt") LocalDateTime publishedAt,
            @JsonProperty("createdAt") LocalDateTime createdAt,
            @JsonProperty("state") String state,
            @JsonProperty("departmentInfo") String departmentInfo
    ) {
        super(id, title, ownerId, publisher, publishedAt, createdAt);
        this.state = state;
        this.departmentInfo = departmentInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Education education = (Education) o;
        return state.equals(education.state) && departmentInfo.equals(education.departmentInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), state, departmentInfo);
    }

    @Override
    public String toString() {
        return "Education{" +
                "id='" + getId() + '\'' +
                ", title='" + getTitle() + '\'' +
                ", ownerId=" + getOwnerId() +
                ", publisher='" + getPublisher() + '\'' +
                ", publishedAt=" + getPublishedAt() +
                ", createdAt=" + getCreatedAt() +
                ", state='" + state + '\'' +
                ", departmentInfo='" + departmentInfo + '\'' +
                '}';
    }
}
