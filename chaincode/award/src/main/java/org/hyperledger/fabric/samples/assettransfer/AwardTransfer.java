package org.hyperledger.fabric.samples.assettransfer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Contract(
        name = "awardCC",
        info = @Info(
                title = "Asset Transfer",
                description = "Aomd asset transfer",
                version = "1.0.0",
                license = @org.hyperledger.fabric.contract.annotation.License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "aomd@gmail.com",
                        name = "Jo2Seo",
                        url = "https://aomd.com"
                )
        )
)
@Default
public class AwardTransfer implements ContractInterface {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private enum AssetTransferErrors {
        ASSET_NOT_FOUND,
        ASSET_ALREADY_EXISTS
    }

    public AwardTransfer() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void init(final Context context) throws JsonProcessingException {
        ChaincodeStub stub = context.getStub();

        create(context, "award1", "title1", 1L, "publisher1", LocalDateTime.now(), "rank1");
        create(context, "award2", "title2", 2L, "publisher2", LocalDateTime.now(), "rank2");
        create(context, "award3", "title3", 3L, "publisher3", LocalDateTime.now(), "rank3");
        create(context, "award4", "title4", 4L, "publisher4", LocalDateTime.now(), "rank4");
        create(context, "award5", "title5", 5L, "publisher5", LocalDateTime.now(), "rank5");
        create(context, "award6", "title6", 6L, "publisher6", LocalDateTime.now(), "rank6");
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Award create(
            final Context context,
            final String id,
            final String title,
            final Long ownerId,
            final String publisher,
            final LocalDateTime publishedAt,
            final String rank
    ) throws JsonProcessingException {
        ChaincodeStub stub = context.getStub();

        if (exists(context, id)) {
            String errorMessage = String.format("Asset %s already exists", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_ALREADY_EXISTS.toString());
        }

        Award award = new Award(id, title, ownerId, publisher, publishedAt, LocalDateTime.now(), rank);
        String awardJson = objectMapper.writeValueAsString(award);
        Award deserialize = objectMapper.readValue(awardJson, Award.class);
        System.out.println(deserialize);
        stub.putStringState(id, awardJson);

        return award;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean exists(final Context context, final String id) {
        ChaincodeStub stub = context.getStub();
        String json = stub.getStringState(id);

        return (json != null && !json.isEmpty());
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Award read(final Context context, final String id) throws JsonProcessingException {
        ChaincodeStub stub = context.getStub();
        String json = stub.getStringState(id);

        if (!exists(context, id)) {
            String errorMessage = String.format("Asset %s does not exists", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_ALREADY_EXISTS.toString());
        }
        Award award = objectMapper.readValue(json, Award.class);
        return award;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getAll(final Context context) throws JsonProcessingException {
        ChaincodeStub stub = context.getStub();

        List<Award> queryResults = new ArrayList<>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result: results) {
            System.out.println(result.getStringValue());
            Award award = objectMapper.readValue(result.getStringValue(), Award.class);
            queryResults.add(award);
        }

        final String response = objectMapper.writeValueAsString(queryResults);

        return response;
    }
}
