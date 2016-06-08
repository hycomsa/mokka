package pl.hycom.api.model.request;

/**
 * @author Jakub Muras <jakub.muras@hycom.pl>
 */
public class ValPattern {

    private MatchingType matchingType;
    private String value;

    public ValPattern(){}

    public ValPattern(MatchingType matchingType) {
        this.matchingType = matchingType;
    }

    public ValPattern(String value, MatchingType matchingType) {
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
}
