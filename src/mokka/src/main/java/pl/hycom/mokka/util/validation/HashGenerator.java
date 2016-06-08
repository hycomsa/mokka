package pl.hycom.mokka.util.validation;

/**
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
@FunctionalInterface
public interface HashGenerator {
    /**
     * Method generates hash for given chain using specified algorithm and key
     * @param chain
     * @param algorithm
     * @param key
     * @return
     */
    String generateHash(String chain, HashAlgorithm algorithm, String key);
}
