package next.controller.qna;

import java.util.List;

import javax.servlet.http.HttpSession;

import next.CannotDeleteException;
import next.controller.UserSessionUtils;
import next.dao.AnswerDao;
import next.dao.QuestionDao;
import next.model.Answer;
import next.model.Question;
import next.model.Result;
import next.model.User;
import next.service.QnaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import core.jdbc.DataAccessException;

@RestController
@RequestMapping("/api")
public class ApiQuestionController {
	private Logger log = LoggerFactory.getLogger(ApiQuestionController.class);
	
	private QuestionDao questionDao = QuestionDao.getInstance();
	private AnswerDao answerDao = AnswerDao.getInstance();
	private QnaService qnaService = QnaService.getInstance();
	
	@RequestMapping(value="/questions/{questionId}", method=RequestMethod.DELETE)
	public Result deleteQuestion(HttpSession session, @PathVariable Long questionId) throws Exception {
		if (!UserSessionUtils.isLogined(session)) {
			return Result.fail("Login is required");
		}
		
		try {
			qnaService.deleteQuestion(questionId, UserSessionUtils.getUserFromSession(session));
			return Result.ok();
		} catch (CannotDeleteException e) {
			return Result.fail(e.getMessage());
		}
	}
	
	@RequestMapping(value = "/questions", method = RequestMethod.GET)
	public List<Question> list() throws Exception {
		return questionDao.findAll();
	}
	
	@RequestMapping(value = "/questions/{questionId}/answers", method = RequestMethod.POST)
	public Model addAnswer(HttpSession session, @PathVariable Long questionId, String contents, Model model) throws Exception {
		log.debug("questionId : {}, contents : {}", questionId, contents);
    	if (!UserSessionUtils.isLogined(session)) {
    		model.addAttribute("result", Result.fail("Login is required"));
			return model;
		}
    	
    	User loginUser = UserSessionUtils.getUserFromSession(session);
    	Answer answer = new Answer(loginUser.getUserId(), contents, questionId);
    	Answer savedAnswer = answerDao.insert(answer);
		questionDao.updateCountOfAnswer(savedAnswer.getQuestionId());
		
		model.addAttribute("answer", savedAnswer);
		model.addAttribute("result", Result.ok());
		return model;
	}
	
	@RequestMapping(value = "/questions/{questionId}/answers/{answerId}", method = RequestMethod.DELETE)
	public Result deleteAnswer(HttpSession session, @PathVariable Long answerId) throws Exception {
		try {
			answerDao.delete(answerId);
			return Result.ok();
		} catch (DataAccessException e) {
			return Result.fail(e.getMessage());
		}
	}
}
