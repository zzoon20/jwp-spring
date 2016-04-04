package next.controller.qna;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import next.CannotOperateException;
import next.controller.UserSessionUtils;
import next.dao.QuestionDao;
import next.model.Question;
import next.service.QnaService;

@Controller
@RequestMapping("/questions")
public class QuestionController {
	private QuestionDao questionDao = QuestionDao.getInstance();
	private QnaService qnaService = QnaService.getInstance();

	@RequestMapping(value = "/{questionId}", method = RequestMethod.GET)
	public String show(@PathVariable Long questionId, Model model) throws Exception {
		Question question = qnaService.findById(questionId);
		model.addAttribute("question", question);
		return "/qna/show";
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String createForm(HttpSession session) throws Exception {
		if (!UserSessionUtils.isLogined(session)) {
			return "redirect:/users/loginForm";
		}
		return "/qna/form";
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public String create(HttpSession session, Question question) throws Exception {
		if (!UserSessionUtils.isLogined(session)) {
			return "redirect:/users/loginForm";
		}
		
		questionDao.insert(question.newQuestion(UserSessionUtils.getUserFromSession(session)));
		return "redirect:/";
	}

	@RequestMapping(value = "/{questionId}/edit", method = RequestMethod.GET)
	public String editForm(HttpSession session, @PathVariable Long questionId, Model model) throws Exception {
		if (!UserSessionUtils.isLogined(session)) {
			return "redirect:/users/loginForm";
		}
		
		Question question = qnaService.findById(questionId);
		if (!question.isSameUser(UserSessionUtils.getUserFromSession(session))) {
			throw new IllegalStateException("다른 사용자가 쓴 글을 수정할 수 없습니다.");
		}
		model.addAttribute("question", question);
		return "/qna/update.jsp";
	}

	@RequestMapping(value = "/{questionId}", method = RequestMethod.PUT)
	public String edit(HttpSession session, @PathVariable Long questionId, Question editQuestion) throws Exception {
		if (!UserSessionUtils.isLogined(session)) {
			return "redirect:/users/loginForm";
		}
		
		qnaService.updateQuestion(editQuestion, UserSessionUtils.getUserFromSession(session));
		return "redirect:/";
	}

	@RequestMapping(value = "/{questionId}", method = RequestMethod.DELETE)
	public String delete(HttpSession session, @PathVariable Long questionId, Model model) throws Exception {
		if (!UserSessionUtils.isLogined(session)) {
			return "redirect:/users/loginForm";
		}
		
		try {
			qnaService.deleteQuestion(questionId, UserSessionUtils.getUserFromSession(session));
			return "redirect:/";
		} catch (CannotOperateException e) {
			model.addAttribute("question", qnaService.findById(questionId));
			model.addAttribute("errorMessage", e.getMessage());
			return "show.jsp";
		}
	}
}
