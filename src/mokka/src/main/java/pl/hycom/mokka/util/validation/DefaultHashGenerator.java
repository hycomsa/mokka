package pl.hycom.mokka.util.validation;


import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

/**
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
@Component(value = "hashGenerator")
@Slf4j
public class DefaultHashGenerator implements HashGenerator {

    public static final String SEPARATOR = "|";

    @Override
    public String generateHash(String chain, HashAlgorithm algorithm, String key) {
        if (chain == null || algorithm == null || key == null) {
            return "";
        }
        Security.addProvider(new BouncyCastleProvider());
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithm.name());
        } catch (NoSuchAlgorithmException e) {
            log.debug("NoSuchAlgorithmException: ", e);
            return "";
        }

        String message = chain + SEPARATOR + key;

        byte[] bytes = message.getBytes();
        byte[] mdbytes = md.digest(bytes);
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < mdbytes.length; i++) {
            hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
        }
        return hexString.toString();
    }
}
