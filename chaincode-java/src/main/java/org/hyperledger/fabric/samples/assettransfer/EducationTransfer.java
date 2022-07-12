package org.hyperledger.fabric.samples.assettransfer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Contract(
        name = "educationCC",
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
public final class EducationTransfer implements ContractInterface {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private enum AssetTransferErrors {
        ASSET_NOT_FOUND,
        ASSET_ALREADY_EXISTS
    }

    public EducationTransfer() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void init(final Context context) throws JsonProcessingException {
        ChaincodeStub stub = context.getStub();

        create(context, "education1", "title1", 1L, "publisher1", LocalDateTime.now(), "state1", "departmentInfo1");
        create(context, "education2", "title2", 2L, "publisher2", LocalDateTime.now(), "state2", "departmentInfo2");
        create(context, "education3", "title3", 3L, "publisher3", LocalDateTime.now(), "state3", "departmentInfo3");
        create(context, "education4", "title4", 4L, "publisher4", LocalDateTime.now(), "state4", "departmentInfo4");
        create(context, "education5", "title5", 5L, "publisher5", LocalDateTime.now(), "state5", "departmentInfo5");
        create(context, "education6", "title6", 6L, "publisher6", LocalDateTime.now(), "state6", "departmentInfo6");
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Education create(
            final Context context,
            final String id,
            final String title,
            final Long ownerId,
            final String publisher,
            final LocalDateTime publishedAt,
            final String state,
            final String departmentInfo
    ) throws JsonProcessingException {
        ChaincodeStub stub = context.getStub();

        if (exists(context, id)) {
            String errorMessage = String.format("Asset %s already exists", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_ALREADY_EXISTS.toString());
        }

        Education education = new Education(id, title, ownerId, publisher, publishedAt, LocalDateTime.now(), state, departmentInfo);
        String educationJson = objectMapper.writeValueAsString(education);
        Education deserialize = objectMapper.readValue(educationJson, Education.class);
        System.out.println(deserialize);
        stub.putStringState(id, educationJson);

        return education;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean exists(final Context context, final String id) {
        ChaincodeStub stub = context.getStub();
        String json = stub.getStringState(id);

        return (json != null && !json.isEmpty());
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Education read(final Context context, final String id) throws JsonProcessingException {
        ChaincodeStub stub = context.getStub();
        String json = stub.getStringState(id);

        if (!exists(context, id)) {
            String errorMessage = String.format("Asset %s does not exists", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_ALREADY_EXISTS.toString());
        }
        Education education = objectMapper.readValue(json, Education.class);
        return education;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getAll(final Context context) throws JsonProcessingException {
        ChaincodeStub stub = context.getStub();

        List<Education> queryResults = new ArrayList<>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result: results) {
            System.out.println(result.getStringValue());
            Education education = objectMapper.readValue(result.getStringValue(), Education.class);
            queryResults.add(education);
        }

        final String response = objectMapper.writeValueAsString(queryResults);

        return response;
    }
}
