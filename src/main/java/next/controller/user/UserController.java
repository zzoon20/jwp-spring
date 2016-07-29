package next.controller.user;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import next.controller.UserSessionUtils;
import next.dao.UserDao;

@Controller
public class UserController {
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private UserDao userDao = UserDao.getInstance();
    
    @RequestMapping(value="/users", method=RequestMethod.GET)
    public ModelAndView listUser(HttpSession session) throws Exception {
    	if (!UserSessionUtils.isLogined(session)) {
			return new ModelAndView("redirect:/users/loginForm");
		}
    	
        ModelAndView mav = new ModelAndView("/user/list.jsp");
        mav.addObject("users", userDao.findAll());
        return mav;
    }
    
}
