package pl.hycom.mokka.util.validation;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;
import sun.security.jca.ProviderList;
import sun.security.jca.Providers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
@Component(value = "hashGenerator")
public class DefaultHashGenerator implements HashGenerator {

    public static final String SEPARATOR = "|";

    @Override
    public String generateHash(String chain, HashAlgorithm algorithm, String key) {
        if (chain == null || algorithm == null || key == null) {
            return "";
        }
        Providers.setProviderList(ProviderList.add(Providers.getProviderList(), new BouncyCastleProvider()));
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(algorithm.name());
        } catch (NoSuchAlgorithmException e) {
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
