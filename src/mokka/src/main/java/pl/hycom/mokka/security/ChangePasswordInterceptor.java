package pl.hycom.mokka.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import pl.hycom.mokka.security.model.CurrentUser;
import pl.hycom.mokka.security.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
public class ChangePasswordInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private UserManager userManager;

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object arg2) throws Exception {
		if ("XMLHttpRequest".equals(req.getHeader("X-Requested-With"))) {
			return true;
		}

		if (StringUtils.endsWith(req.getRequestURL(), "/change-password")) {
			return true;
		}

		return preHandleCurrentUser(res);
	}

	private boolean preHandleCurrentUser(HttpServletResponse res) throws IOException {
		if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof CurrentUser) {
			CurrentUser user = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (user != null) {
				User dbUser = userManager.getUser(user.getId());
				if (dbUser != null && dbUser.getResetPassword() == Boolean.TRUE) {
					res.sendRedirect("/change-password");
					return false;
				}
			}
		}

		return true;
	}

}
