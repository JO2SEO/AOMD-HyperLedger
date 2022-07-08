package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.annotation.JsonProperty;
import lombok.Getter;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.time.LocalDateTime;
import java.util.Objects;

@DataType
@Getter
public class License extends CommonAsset {
    @Property
    private String description;
    @Property
    private LocalDateTime expireDate;
    @Property
    private String qualificationNumber;

    public License(
            @JsonProperty("id") String id,
            @JsonProperty("title") String title,
            @JsonProperty("ownerId") Long ownerId,
            @JsonProperty("publisher") String publisher,
            @JsonProperty("publishedAt") LocalDateTime publishedAt,
            @JsonProperty("createdAt") LocalDateTime createdAt,
            @JsonProperty("description") String description,
            @JsonProperty("expireDate") LocalDateTime expireDate,
            @JsonProperty("qualificationNumber") String qualificationNumber
    ) {
        super(id, title, ownerId, publisher, publishedAt, createdAt);
        this.description = description;
        this.expireDate = expireDate;
        this.qualificationNumber = qualificationNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        License license = (License) o;
        return description.equals(license.description) && expireDate.equals(license.expireDate) && qualificationNumber.equals(license.qualificationNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), description, expireDate, qualificationNumber);
    }

    @Override
    public String toString() {
        return "License{" +
                "id='" + super.getId() + '\'' +
                ", title='" + super.getTitle() + '\'' +
                ", ownerId=" + super.getOwnerId() +
                ", publisher='" + super.getPublisher() + '\'' +
                ", publishedAt=" + super.getPublisher() +
                ", createdAt=" + super.getCreatedAt() +
                ", description='" + description + '\'' +
                ", expireDate=" + expireDate +
                ", qualificationNumber='" + qualificationNumber + '\'' +
                '}';
    }
}
