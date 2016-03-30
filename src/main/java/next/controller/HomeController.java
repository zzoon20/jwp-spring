package next.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import next.dao.QuestionDao;

@Controller
public class HomeController {
    private QuestionDao questionDao = QuestionDao.getInstance();

    @RequestMapping("/")
    public ModelAndView execute() throws Exception {
    	ModelAndView mav = new ModelAndView("index");
    	mav.addObject("questions", questionDao.findAll());
        return mav;
    }
}
