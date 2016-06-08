package pl.hycom.api.converter.wiremock;

import com.github.tomakehurst.wiremock.matching.ValuePattern;
import pl.hycom.api.model.request.MatchingType;
import pl.hycom.api.model.request.ValPattern;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Jakub Muras <jakub.muras@hycom.pl>
 */
public class ValuePatternConverter {

    private ValuePatternConverter(){}

    public static ValPattern convertToValPattern(ValuePattern valuePattern){
        if(valuePattern == null){
            return null;
        }
        if(null != valuePattern.isAbsent() && valuePattern.isAbsent()){
            return new ValPattern(MatchingType.ABSENT);
        }
        if(null != valuePattern.getContains()){
            return new ValPattern(valuePattern.getContains(), MatchingType.CONTAINS);
        }
        if(null != valuePattern.getDoesNotMatch()){
            return new ValPattern(valuePattern.getDoesNotMatch(), MatchingType.DOES_NOT_MATCH);
        }
        if(null != valuePattern.getEqualTo()){
            return new ValPattern(valuePattern.getEqualTo(), MatchingType.EQUAL_TO);
        }
        if(null != valuePattern.getEqualToJson()){
            return new ValPattern(valuePattern.getEqualToJson(), MatchingType.EQUAL_TO_JSON);
        }
        if(null != valuePattern.getEqualToXml()){
            return new ValPattern(valuePattern.getEqualToXml(), MatchingType.EQUAL_TO_XML);
        }
        if(null != valuePattern.getMatchesXPath()){
            return new ValPattern(valuePattern.getMatchesXPath(), MatchingType.MATCHES_XPATH);
        }
        if(null != valuePattern.getMatches()){
            return new ValPattern(valuePattern.getMatches(), MatchingType.MATCHES);
        }
        return null;
    }

    public static ValuePattern convertToValuePattern(ValPattern valPattern){
        if(valPattern == null){
            return null;
        }if (valPattern.getMatchingType() == null){
            return new ValuePattern();
        }
        ValuePattern result = new ValuePattern();
        switch (valPattern.getMatchingType()){
            case ABSENT:
                result.setAbsent(true);
                break;
            case CONTAINS:
                result.setContains(valPattern.getValue());
                break;
            case DOES_NOT_MATCH:
                result.setDoesNotMatch(valPattern.getValue());
                break;
            case EQUAL_TO:
                result.setEqualTo(valPattern.getValue());
                break;
            case EQUAL_TO_JSON:
                result.setEqualToJson(valPattern.getValue());
                break;
            case EQUAL_TO_XML:
                result.setEqualToXml(valPattern.getValue());
                break;
            case MATCHES:
                result.setMatches(valPattern.getValue());
                break;
            case MATCHES_XPATH:
                result.setMatchesXPath(valPattern.getValue());
                break;
            default:
                return null;
        }
        return result;
    }

    public static List<ValuePattern> convertToValuePatternList(List<ValPattern> list){
        if(list == null || list.isEmpty()){
            return null;
        }
        List<ValuePattern> result = new LinkedList<>();
        for (ValPattern val: list) {
            result.add(convertToValuePattern(val));
        }
        return result;
    }

    public static List<ValPattern> convertToValPatternList(List<ValuePattern> list){
        if(list == null || list.isEmpty()){
            return null;
        }
        List<ValPattern> result = new LinkedList<>();
        for (ValuePattern val: list) {
            if(val != null && !("is absent".equals(val.toString()) && val.isAbsent() == null)){
                result.add(convertToValPattern(val));
            }
        }
        return result;
    }

    public static <T> Map<T, ValuePattern> convertToValuePatternMap(Map<T, ValPattern> map){
        if(map == null || map.isEmpty()){
            return null;
        }
        Map<T,ValuePattern> result = new HashMap<>();
        for (T key: map.keySet()) {
            result.put(key, convertToValuePattern(map.get(key)));
        }
        return result;
    }

    public static <T> Map<T, ValPattern> convertToValPatternMap(Map<T, ValuePattern> map){
        if(map == null || map.isEmpty()){
            return null;
        }
        Map<T,ValPattern> result = new HashMap<>();
        for (T key: map.keySet()) {
            result.put(key, convertToValPattern(map.get(key)));
        }
        return result;
    }
}
