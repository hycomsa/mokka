package pl.hycom.mokka.ui.wiremock;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import pl.hycom.api.model.Mapping;
import pl.hycom.api.service.WireMockManager;
import pl.hycom.mokka.util.validation.MappingValidator;

/**
 * @author Jakub Muras <jakub.muras@hycom.pl>
 */
@Controller
@RequestMapping(value = "/wiremock")
public class WiremockController {

    private final static String HOST = "localhost";
    private final static int PORT = 48080;

    @RequestMapping(method = RequestMethod.GET)
    public String get(Model model){
        model.addAttribute("mappings", new WireMockManager(HOST, PORT).getAllMappings());
        model.addAttribute("methods", pl.hycom.api.model.request.RequestMethod.values());
        model.addAttribute("predicates", pl.hycom.api.model.request.MatchingType.values());
        return "/wiremock/list";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(Model model){
        model.addAttribute("mapping", new Mapping());
        model.addAttribute("methods", pl.hycom.api.model.request.RequestMethod.values());
        model.addAttribute("predicates", pl.hycom.api.model.request.MatchingType.values());
        return "/wiremock/edit";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@ModelAttribute Mapping mapping, @RequestParam int urlType, Model model){
        if(2 == urlType){
            mapping.getRequestPattern().setUrlPattern(mapping.getRequestPattern().getUrl());
            mapping.getRequestPattern().setUrl(null);
        }else if(3 == urlType){
            mapping.getRequestPattern().setUrlPath(mapping.getRequestPattern().getUrl());
            mapping.getRequestPattern().setUrl(null);
        }else if(4 == urlType){
            mapping.getRequestPattern().setUrlPathPattern(mapping.getRequestPattern().getUrl());
            mapping.getRequestPattern().setUrl(null);
        }
        MappingValidator.validateNewMapping(mapping);
        new WireMockManager(HOST, PORT).create(mapping);
        return "redirect:/wiremock";
    }
}
