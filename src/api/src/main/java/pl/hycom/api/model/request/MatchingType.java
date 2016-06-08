package pl.hycom.api.model.request;

/**
 * @author Jakub Muras <jakub.muras@hycom.pl>
 */
public enum MatchingType {
    EQUAL_TO("equals to"),
    EQUAL_TO_JSON("equals to json"),
    EQUAL_TO_XML("equals to xml"),
    CONTAINS("contains"),
    MATCHES("matches"),
    DOES_NOT_MATCH("do not match"),
    ABSENT("absent"),
    MATCHES_XPATH("matches xpath");


    private final String name;

    MatchingType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
