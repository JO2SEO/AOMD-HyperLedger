package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.time.LocalDateTime;

@Contract(
        name = "aomd",
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
public final class LicenseTransfer implements ContractInterface {
    private final Genson genson = new Genson();

    private enum AssetTransferErrors {
        ASSET_NOT_FOUND,
        ASSET_ALREADY_EXISTS
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void initLedger(final Context context) {
        ChaincodeStub stub = context.getStub();
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
    ) {
        ChaincodeStub stub = context.getStub();

        if (exists(context, id)) {
            String errorMessage = String.format("Asset %s already exists", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_ALREADY_EXISTS.toString());
        }

        License license = new License(id, title, ownerId, publisher, publishedAt, LocalDateTime.now(), description, expireDate, qualificationNumber);
        String licenseJson = genson.serialize(license);
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
    public License read(final Context context, final String id) {
        ChaincodeStub stub = context.getStub();
        String json = stub.getStringState(id);

        if (!exists(context, id)) {
            String errorMessage = String.format("Asset %s does not exists", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_ALREADY_EXISTS.toString());
        }
        License license = genson.deserialize(json, License.class);
        return license;
    }
}
