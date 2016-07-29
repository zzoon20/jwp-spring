package next.controller.user;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import next.controller.UserSessionUtils;
import next.dao.UserDao;
import next.model.User;

@Controller
public class UserController {
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	private UserDao userDao = UserDao.getInstance();

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public ModelAndView indexUser(HttpSession session) throws Exception {
		if (!UserSessionUtils.isLogined(session)) {
			return new ModelAndView("redirect:users/loginForm");
		}

		ModelAndView mav = new ModelAndView("user/list");
		mav.addObject("users", userDao.findAll());
		return mav;
	}

	@RequestMapping(value = "/users/loginForm", method = RequestMethod.GET)
	public ModelAndView formLogin() throws Exception {
		return new ModelAndView("user/login");
	}

	@RequestMapping(value = "/users/login", method = RequestMethod.POST)
	public ModelAndView login(HttpSession session, @RequestParam String userId, @RequestParam String password)
			throws Exception {
		User user = userDao.findByUserId(userId);

		if (user == null) {
			throw new NullPointerException("사용자를 찾을 수 없습니다.");
		}

		if (user.matchPassword(password)) {
			session.setAttribute("user", user);
			return new ModelAndView("redirect:/");
		} else {
			throw new IllegalStateException("비밀번호가 틀립니다.");
		}
	}

	@RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
	public ModelAndView showProfile(@PathVariable String userId) {
		ModelAndView mav = new ModelAndView("user/profile");
		mav.addObject("user", userDao.findByUserId(userId));
		return mav;
	}

}
