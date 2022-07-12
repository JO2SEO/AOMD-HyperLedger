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
        name = "licenseCC",
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
                        url = "https://aomd.com")))
@Default
public final class LicenseTransfer implements ContractInterface {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private enum AssetTransferErrors {
        ASSET_NOT_FOUND,
        ASSET_ALREADY_EXISTS
    }
    
    public LicenseTransfer() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void init(final Context context) throws JsonProcessingException {
        ChaincodeStub stub = context.getStub();

        create(context, "license1", "title1", 1L, "publisher1", LocalDateTime.now(), "desc1", LocalDateTime.now(), "qualificationNumber1");
        create(context, "license2", "title2", 2L, "publisher2", LocalDateTime.now(), "desc2", LocalDateTime.now(), "qualificationNumber2");
        create(context, "license3", "title3", 3L, "publisher3", LocalDateTime.now(), "desc3", LocalDateTime.now(), "qualificationNumber3");
        create(context, "license4", "title4", 4L, "publisher4", LocalDateTime.now(), "desc4", LocalDateTime.now(), "qualificationNumber4");
        create(context, "license5", "title5", 5L, "publisher5", LocalDateTime.now(), "desc5", LocalDateTime.now(), "qualificationNumber5");
        create(context, "license6", "title6", 6L, "publisher6", LocalDateTime.now(), "desc6", LocalDateTime.now(), "qualificationNumber6");
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public License create(
            final Context context,
            final String id,
            final String title,
            final Long ownerId,
            final String publisher,
            final LocalDateTime publishedAt,
            final String description,
            final LocalDateTime expireDate,
            final String qualificationNumber
    ) throws JsonProcessingException {
        ChaincodeStub stub = context.getStub();

        if (exists(context, id)) {
            String errorMessage = String.format("Asset %s already exists", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_ALREADY_EXISTS.toString());
        }

        License license = new License(id, title, ownerId, publisher, publishedAt, LocalDateTime.now(), description, expireDate, qualificationNumber);
        String licenseJson = objectMapper.writeValueAsString(license);
        stub.putStringState(id, licenseJson);

        return license;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean exists(final Context context, final String id) {
        ChaincodeStub stub = context.getStub();
        String json = stub.getStringState(id);

        return (json != null && !json.isEmpty());
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public License read(final Context context, final String id) throws JsonProcessingException {
        ChaincodeStub stub = context.getStub();
        String json = stub.getStringState(id);

        if (!exists(context, id)) {
            String errorMessage = String.format("Asset %s does not exists", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_ALREADY_EXISTS.toString());
        }
        License license = objectMapper.readValue(json, License.class);
        return license;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getAll(final Context context) throws JsonProcessingException {
        ChaincodeStub stub = context.getStub();

        List<License> queryResults = new ArrayList<>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result: results) {
            License license = objectMapper.readValue(result.getStringValue(), License.class);
            queryResults.add(license);
            System.out.println(license.toString());
        }

        final String response = objectMapper.writeValueAsString(queryResults);

        return response;
    }
}
