package pl.hycom.mokka.util.validation;

/**
 * @author Jakub Muras <jakub.muras@hycom.pl>
 */
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
        return hash.equals(hashGenerator.generateHash(sb.toString(), HashAlgorithm.SHA256, key));
    }
}