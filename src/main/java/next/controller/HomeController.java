package next.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import next.dao.QuestionDao;

@Controller
public class HomeController {
    private QuestionDao questionDao = QuestionDao.getInstance();

    @RequestMapping("/")
    public String execute(Model model) throws Exception {
    	model.addAttribute("questions", questionDao.findAll());
        return "index";
    }
}
