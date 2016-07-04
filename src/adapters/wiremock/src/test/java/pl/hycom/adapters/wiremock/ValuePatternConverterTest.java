package pl.hycom.adapters.wiremock;

import com.github.tomakehurst.wiremock.matching.ValuePattern;
import org.junit.Assert;
import org.junit.Test;
import pl.hycom.api.model.request.MatchingType;
import pl.hycom.api.model.request.ValPattern;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Jakub Muras <jakub.muras@hycom.pl>
 */
public class ValuePatternConverterTest {

    @Test
    public void testConvertToValPattern() {
        ValuePattern valuePattern = new ValuePattern();
        valuePattern.setMatches("matches");
        Assert.assertEquals(MatchingType.MATCHES, ValuePatternConverter.convertToValPattern(valuePattern)
                .getMatchingType());
        Assert.assertEquals("matches", ValuePatternConverter.convertToValPattern(valuePattern)
                .getValue());
        valuePattern = new ValuePattern();
        valuePattern.setMatchesXPath("MatchesXPath");
        Assert.assertEquals(MatchingType.MATCHES_XPATH, ValuePatternConverter.convertToValPattern(valuePattern)
                .getMatchingType());
        Assert.assertEquals("MatchesXPath", ValuePatternConverter.convertToValPattern(valuePattern)
                .getValue());
        valuePattern = new ValuePattern();
        valuePattern.setAbsent(true);
        Assert.assertEquals(MatchingType.ABSENT, ValuePatternConverter.convertToValPattern(valuePattern)
                .getMatchingType());
        Assert.assertNull(ValuePatternConverter.convertToValPattern(valuePattern)
                .getValue());
        valuePattern = new ValuePattern();
        valuePattern.setEqualToXml("EqualToXml");
        Assert.assertEquals(MatchingType.EQUAL_TO_XML, ValuePatternConverter.convertToValPattern(valuePattern)
                .getMatchingType());
        Assert.assertEquals("EqualToXml", ValuePatternConverter.convertToValPattern(valuePattern)
                .getValue());
        valuePattern = new ValuePattern();
        valuePattern.setEqualTo("EqualTo");
        Assert.assertEquals(MatchingType.EQUAL_TO, ValuePatternConverter.convertToValPattern(valuePattern)
                .getMatchingType());
        Assert.assertEquals("EqualTo", ValuePatternConverter.convertToValPattern(valuePattern)
                .getValue());
        valuePattern = new ValuePattern();
        valuePattern.setEqualToJson("EqualToJson");
        Assert.assertEquals(MatchingType.EQUAL_TO_JSON, ValuePatternConverter.convertToValPattern(valuePattern)
                .getMatchingType());
        Assert.assertEquals("EqualToJson", ValuePatternConverter.convertToValPattern(valuePattern)
                .getValue());
        valuePattern = new ValuePattern();
        valuePattern.setContains("Contains");
        Assert.assertEquals(MatchingType.CONTAINS, ValuePatternConverter.convertToValPattern(valuePattern)
                .getMatchingType());
        Assert.assertEquals("Contains", ValuePatternConverter.convertToValPattern(valuePattern)
                .getValue());
        valuePattern = new ValuePattern();
        valuePattern.setDoesNotMatch("DoesNotMatch");
        Assert.assertEquals(MatchingType.DOES_NOT_MATCH, ValuePatternConverter.convertToValPattern(valuePattern)
                .getMatchingType());
        Assert.assertEquals("DoesNotMatch", ValuePatternConverter.convertToValPattern(valuePattern)
                .getValue());
        Assert.assertNull(ValuePatternConverter.convertToValPattern(null));
    }

    @Test
    public void testConvertToValuePattern() {
        ValPattern valPattern = new ValPattern("value", MatchingType.CONTAINS);
        ValuePattern res = ValuePatternConverter.convertToValuePattern(valPattern);
        Assert.assertEquals("value", res.getContains());
        Assert.assertNull(res.getEqualToXml());
        Assert.assertNull(res.getEqualTo());
        Assert.assertNull(res.getMatchesXPath());
        Assert.assertNull(res.getMatches());
        Assert.assertNull(res.getEqualToJson());
        Assert.assertNull(res.getDoesNotMatch());
        Assert.assertNull(res.isAbsent());
        valPattern.setMatchingType(MatchingType.DOES_NOT_MATCH);
        res = ValuePatternConverter.convertToValuePattern(valPattern);
        Assert.assertEquals("value", res.getDoesNotMatch());
        Assert.assertNull(res.getContains());
        Assert.assertNull(res.getEqualToXml());
        Assert.assertNull(res.getEqualTo());
        Assert.assertNull(res.getMatchesXPath());
        Assert.assertNull(res.getMatches());
        Assert.assertNull(res.getEqualToJson());
        Assert.assertNull(res.isAbsent());
        valPattern.setMatchingType(MatchingType.EQUAL_TO);
        res = ValuePatternConverter.convertToValuePattern(valPattern);
        Assert.assertEquals("value", res.getEqualTo());
        Assert.assertNull(res.getContains());
        Assert.assertNull(res.getEqualToXml());
        Assert.assertNull(res.getDoesNotMatch());
        Assert.assertNull(res.getMatchesXPath());
        Assert.assertNull(res.getMatches());
        Assert.assertNull(res.getEqualToJson());
        Assert.assertNull(res.isAbsent());
        valPattern.setMatchingType(MatchingType.EQUAL_TO_JSON);
        res = ValuePatternConverter.convertToValuePattern(valPattern);
        Assert.assertEquals("value", res.getEqualToJson());
        Assert.assertNull(res.getContains());
        Assert.assertNull(res.getEqualToXml());
        Assert.assertNull(res.getEqualTo());
        Assert.assertNull(res.getMatchesXPath());
        Assert.assertNull(res.getMatches());
        Assert.assertNull(res.getDoesNotMatch());
        Assert.assertNull(res.isAbsent());
        valPattern.setMatchingType(MatchingType.EQUAL_TO_XML);
        res = ValuePatternConverter.convertToValuePattern(valPattern);
        Assert.assertEquals("value", res.getEqualToXml());
        Assert.assertNull(res.getContains());
        Assert.assertNull(res.getDoesNotMatch());
        Assert.assertNull(res.getEqualTo());
        Assert.assertNull(res.getMatchesXPath());
        Assert.assertNull(res.getMatches());
        Assert.assertNull(res.getEqualToJson());
        Assert.assertNull(res.isAbsent());
        valPattern.setMatchingType(MatchingType.MATCHES);
        res = ValuePatternConverter.convertToValuePattern(valPattern);
        Assert.assertEquals("value", res.getMatches());
        Assert.assertNull(res.getContains());
        Assert.assertNull(res.getEqualToXml());
        Assert.assertNull(res.getEqualTo());
        Assert.assertNull(res.getMatchesXPath());
        Assert.assertNull(res.getDoesNotMatch());
        Assert.assertNull(res.getEqualToJson());
        Assert.assertNull(res.isAbsent());
        valPattern.setMatchingType(MatchingType.MATCHES_XPATH);
        res = ValuePatternConverter.convertToValuePattern(valPattern);
        Assert.assertEquals("value", res.getMatchesXPath());
        Assert.assertNull(res.getContains());
        Assert.assertNull(res.getEqualToXml());
        Assert.assertNull(res.getEqualTo());
        Assert.assertNull(res.getMatches());
        Assert.assertNull(res.getDoesNotMatch());
        Assert.assertNull(res.getEqualToJson());
        Assert.assertNull(res.isAbsent());
        valPattern.setMatchingType(MatchingType.ABSENT);
        res = ValuePatternConverter.convertToValuePattern(valPattern);
        Assert.assertTrue(res.isAbsent());
        Assert.assertNull(res.getContains());
        Assert.assertNull(res.getEqualToXml());
        Assert.assertNull(res.getEqualTo());
        Assert.assertNull(res.getMatches());
        Assert.assertNull(res.getDoesNotMatch());
        Assert.assertNull(res.getEqualToJson());
        Assert.assertNull(res.getMatchesXPath());
    }

    @Test
    public void testConvertToValuePatternList() {
        List<ValPattern> list = new LinkedList<>();
        list.add(new ValPattern("value", MatchingType.CONTAINS));
        list.add(new ValPattern("value", MatchingType.DOES_NOT_MATCH));
        list.add(new ValPattern("value", MatchingType.EQUAL_TO));
        list.add(new ValPattern("value", MatchingType.EQUAL_TO_JSON));
        list.add(new ValPattern("value", MatchingType.EQUAL_TO_XML));
        list.add(new ValPattern("value", MatchingType.MATCHES));
        list.add(new ValPattern("value", MatchingType.MATCHES_XPATH));
        list.add(new ValPattern("value", MatchingType.ABSENT));
        List<ValuePattern> result = ValuePatternConverter.convertToValuePatternList(list);
        Assert.assertEquals("value",result.get(0).getContains());
        Assert.assertNull(result.get(0).getDoesNotMatch());
        Assert.assertNull(result.get(0).getEqualTo());
        Assert.assertNull(result.get(0).getEqualToJson());
        Assert.assertNull(result.get(0).getEqualToXml());
        Assert.assertNull(result.get(0).getMatches());
        Assert.assertNull(result.get(0).getMatchesXPath());
        Assert.assertNull(result.get(0).isAbsent());
        Assert.assertEquals("value",result.get(1).getDoesNotMatch());
        Assert.assertNull(result.get(1).getContains());
        Assert.assertNull(result.get(1).getEqualTo());
        Assert.assertNull(result.get(1).getEqualToJson());
        Assert.assertNull(result.get(1).getEqualToXml());
        Assert.assertNull(result.get(1).getMatches());
        Assert.assertNull(result.get(1).getMatchesXPath());
        Assert.assertNull(result.get(1).isAbsent());
        Assert.assertEquals("value",result.get(2).getEqualTo());
        Assert.assertNull(result.get(2).getContains());
        Assert.assertNull(result.get(2).getDoesNotMatch());
        Assert.assertNull(result.get(2).getEqualToJson());
        Assert.assertNull(result.get(2).getEqualToXml());
        Assert.assertNull(result.get(2).getMatches());
        Assert.assertNull(result.get(2).getMatchesXPath());
        Assert.assertNull(result.get(2).isAbsent());
        Assert.assertEquals("value",result.get(3).getEqualToJson());
        Assert.assertNull(result.get(3).getContains());
        Assert.assertNull(result.get(3).getDoesNotMatch());
        Assert.assertNull(result.get(3).getEqualTo());
        Assert.assertNull(result.get(3).getEqualToXml());
        Assert.assertNull(result.get(3).getMatches());
        Assert.assertNull(result.get(3).getMatchesXPath());
        Assert.assertNull(result.get(3).isAbsent());
        Assert.assertEquals("value",result.get(4).getEqualToXml());
        Assert.assertNull(result.get(4).getContains());
        Assert.assertNull(result.get(4).getDoesNotMatch());
        Assert.assertNull(result.get(4).getEqualTo());
        Assert.assertNull(result.get(4).getEqualToJson());
        Assert.assertNull(result.get(4).getMatches());
        Assert.assertNull(result.get(4).getMatchesXPath());
        Assert.assertNull(result.get(4).isAbsent());
        Assert.assertEquals("value",result.get(5).getMatches());
        Assert.assertNull(result.get(5).getContains());
        Assert.assertNull(result.get(5).getDoesNotMatch());
        Assert.assertNull(result.get(5).getEqualTo());
        Assert.assertNull(result.get(5).getEqualToJson());
        Assert.assertNull(result.get(5).getEqualToXml());
        Assert.assertNull(result.get(5).getMatchesXPath());
        Assert.assertNull(result.get(5).isAbsent());
        Assert.assertEquals("value",result.get(6).getMatchesXPath());
        Assert.assertNull(result.get(6).getContains());
        Assert.assertNull(result.get(6).getDoesNotMatch());
        Assert.assertNull(result.get(6).getEqualTo());
        Assert.assertNull(result.get(6).getEqualToJson());
        Assert.assertNull(result.get(6).getEqualToXml());
        Assert.assertNull(result.get(6).getMatches());
        Assert.assertNull(result.get(6).isAbsent());
        Assert.assertTrue(result.get(7).isAbsent());
        Assert.assertNull(result.get(7).getContains());
        Assert.assertNull(result.get(7).getDoesNotMatch());
        Assert.assertNull(result.get(7).getEqualTo());
        Assert.assertNull(result.get(7).getEqualToJson());
        Assert.assertNull(result.get(7).getEqualToXml());
        Assert.assertNull(result.get(7).getMatches());
        Assert.assertNull(result.get(7).getMatchesXPath());
    }

    @Test
    public void testConvertToValPatternList() {
        List<ValuePattern> list = new LinkedList<>();
        ValuePattern val1 = new ValuePattern();
        ValuePattern val2 = new ValuePattern();
        ValuePattern val3 = new ValuePattern();
        ValuePattern val4 = new ValuePattern();
        ValuePattern val5 = new ValuePattern();
        ValuePattern val6 = new ValuePattern();
        ValuePattern val7 = new ValuePattern();
        ValuePattern val8 = new ValuePattern();
        val1.setDoesNotMatch("value");
        val2.setMatches("value");
        val3.setContains("value");
        val4.setEqualToXml("value");
        val5.setAbsent(true);
        val6.setEqualToJson("value");
        val7.setMatchesXPath("value");
        val8.setEqualTo("value");
        list.add(val1);
        list.add(val2);
        list.add(val3);
        list.add(val4);
        list.add(val5);
        list.add(val6);
        list.add(val7);
        list.add(val8);

        List<ValPattern> result = ValuePatternConverter.convertToValPatternList(list);
        Assert.assertEquals(MatchingType.DOES_NOT_MATCH,result.get(0).getMatchingType());
        Assert.assertEquals("value",result.get(0).getValue());

        Assert.assertEquals(MatchingType.MATCHES,result.get(1).getMatchingType());
        Assert.assertEquals("value",result.get(1).getValue());

        Assert.assertEquals(MatchingType.CONTAINS,result.get(2).getMatchingType());
        Assert.assertEquals("value",result.get(2).getValue());

        Assert.assertEquals(MatchingType.EQUAL_TO_XML,result.get(3).getMatchingType());
        Assert.assertEquals("value",result.get(3).getValue());

        Assert.assertEquals(MatchingType.ABSENT,result.get(4).getMatchingType());
        Assert.assertNull(result.get(4).getValue());

        Assert.assertEquals(MatchingType.EQUAL_TO_JSON,result.get(5).getMatchingType());
        Assert.assertEquals("value",result.get(5).getValue());

        Assert.assertEquals(MatchingType.MATCHES_XPATH,result.get(6).getMatchingType());
        Assert.assertEquals("value",result.get(6).getValue());

        Assert.assertEquals(MatchingType.EQUAL_TO,result.get(7).getMatchingType());
        Assert.assertEquals("value",result.get(7).getValue());
    }
}
