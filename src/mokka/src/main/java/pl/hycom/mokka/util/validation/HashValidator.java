package pl.hycom.mokka.util.validation;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jakub Muras <jakub.muras@hycom.pl>
 */
@Slf4j
public class HashValidator {

    private HashValidator() {
    }

    public static boolean validate(String hash, String key, String... args) {
        StringBuilder sb = new StringBuilder();
        for (String s : args) {
            if (s != null && !s.isEmpty()) {
                sb.append(s + DefaultHashGenerator.SEPARATOR);
            }

        }
        sb.deleteCharAt(sb.length()-1);
        HashGenerator hashGenerator = new DefaultHashGenerator();
        String expectedHash = hashGenerator.generateHash(sb.toString(), HashAlgorithm.SHA256, key);

        log.info("Expected hash [{}], received [{}]", expectedHash, hash);
        return hash.equals(expectedHash);
    }
}
