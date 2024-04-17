package Others;

import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;

import java.util.UUID;

public class UniqueIDGenerator {

    public static String generateUniqueID() {
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString().replace("-", ""); // Remove dashes from UUID
        String hash = Hashing.sha256().hashBytes(id.getBytes()).toString();
        return base64Encode(hash.getBytes()).substring(0, 15); // Take first 15 characters
    }

    private static String base64Encode(byte[] bytes) {
        return BaseEncoding.base64Url().omitPadding().encode(bytes);
    }
}

