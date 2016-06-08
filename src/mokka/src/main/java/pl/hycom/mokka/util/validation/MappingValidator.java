package pl.hycom.mokka.util.validation;

import org.thymeleaf.util.StringUtils;
import pl.hycom.api.model.Mapping;

/**
 * @author Jakub Muras <jakub.muras@hycom.pl>
 */
public class MappingValidator {
    public static void validateNewMapping(Mapping mapping){
        if (StringUtils.isEmptyOrWhitespace(mapping.getId())){
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (StringUtils.isEmptyOrWhitespace(mapping.getRequestPattern().getUrl()) &&
                StringUtils.isEmptyOrWhitespace(mapping.getRequestPattern().getUrlPath()) &&
                StringUtils.isEmptyOrWhitespace(mapping.getRequestPattern().getUrlPathPattern()) &&
                StringUtils.isEmptyOrWhitespace(mapping.getRequestPattern().getUrlPattern())){
            throw new IllegalArgumentException("Url, UrlPath, UrlPattern or UrlPathPattern has to being set");
        }
    }
}
