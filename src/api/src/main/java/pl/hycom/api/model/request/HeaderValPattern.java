package pl.hycom.api.model.request;

/**
 * @author Jakub Muras <jakub.muras@hycom.pl>
 */
public class HeaderValPattern {

    private MatchingType matchingType;
    private String value;
    private String key;
    public HeaderValPattern(){}

    public HeaderValPattern(MatchingType matchingType) {
        this.matchingType = matchingType;
    }

    public HeaderValPattern(String value, MatchingType matchingType) {
        this.matchingType = matchingType;
        this.value = value;
    }

    public MatchingType getMatchingType() {
        return matchingType;
    }

    public void setMatchingType(MatchingType matchingType) {
        this.matchingType = matchingType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
