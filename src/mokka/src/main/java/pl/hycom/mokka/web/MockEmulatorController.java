package pl.hycom.mokka.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Controller
@RequestMapping(headers = "x-requested-with!=XMLHttpRequest")
public class MockEmulatorController {

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return "redirect:/login?logout";
	}

	@RequestMapping(path = "/login")
	public String login(ModelMap model) {
		return handleResponse("login", model);
	}

	@RequestMapping(path = "/")
	public String index(ModelMap model) {
		return logs(model);
	}

	@RequestMapping(path = "/logs")
	public String logs(ModelMap model) {
		return handleResponse("log", model);
	}

	@RequestMapping(path = "/configurations")
	public String configurations(ModelMap model) {
		return handleResponse("configuration", model);
	}

	@RequestMapping(path = "/jmsconfigurations")
	public String jmsconfigurations(ModelMap model) {
		return handleResponse("jmsconfiguration", model);
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER_ADMIN')")
	@RequestMapping(path = "/users")
	public String users(ModelMap model) {
		return handleResponse("user", model);
	}

	@PreAuthorize("hasAnyRole('ROLE_USER')")
	@RequestMapping(path = "/change-password")
	public String changePassword(ModelMap model) {
		return handleResponse("changepassword", model);
	}

	private String handleResponse(String path, ModelMap model) {
		model.put("path", path);
		return "index";
	}
}
