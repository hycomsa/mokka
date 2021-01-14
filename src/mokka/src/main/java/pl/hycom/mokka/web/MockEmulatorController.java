package pl.hycom.mokka.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Controller
@RequestMapping(headers = "x-requested-with!=XMLHttpRequest")
public class MockEmulatorController {

	@GetMapping(value = "/logout")
	public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return "redirect:/login?logout";
	}

	@GetMapping(path = "/login")
	public String login(ModelMap model) {
		return handleResponse("login", model);
	}

	@GetMapping(path = "/")
	public String index(ModelMap model) {
		return logs(model);
	}

	@GetMapping(path = "/logs")
	public String logs(ModelMap model) {
		return handleResponse("log", model);
	}

	@GetMapping(path = "/configurations")
	public String configurations(ModelMap model) {
		return handleResponse("configuration", model);
	}

    @GetMapping(path = "/journal")
    public String journal(ModelMap model) {
        return handleResponse("journal", model);
    }

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER_ADMIN')")
	@GetMapping(path = "/users")
	public String users(ModelMap model) {
		return handleResponse("user", model);
	}

	@PreAuthorize("hasAnyRole('ROLE_USER')")
	@GetMapping(path = "/change-password")
	public String changePassword(ModelMap model) {
		return handleResponse("changepassword", model);
	}

	private String handleResponse(String path, ModelMap model) {
		model.put("path", path);
		return "index";
	}
}
