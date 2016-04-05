package next.controller.qna;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import next.CannotOperateException;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;

import core.jdbc.DataAccessException;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
	private Logger log = LoggerFactory.getLogger(ApiQuestionController.class);
	
	private QuestionDao questionDao = QuestionDao.getInstance();
	private AnswerDao answerDao = AnswerDao.getInstance();
	private QnaService qnaService = QnaService.getInstance();
	
	@RequestMapping(value="/{questionId}", method=RequestMethod.DELETE)
	public Result deleteQuestion(HttpSession session, @PathVariable Long questionId) throws Exception {
		if (!UserSessionUtils.isLogined(session)) {
			return Result.fail("Login is required");
		}
		
		try {
			qnaService.deleteQuestion(questionId, UserSessionUtils.getUserFromSession(session));
			return Result.ok();
		} catch (CannotOperateException e) {
			return Result.fail(e.getMessage());
		}
	}
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<Question> list() throws Exception {
		return questionDao.findAll();
	}
	
	@RequestMapping(value = "/{questionId}/answers", method = RequestMethod.POST)
	public Map<String, Object> addAnswer(HttpSession session, @PathVariable Long questionId, String contents) throws Exception {
		log.debug("questionId : {}, contents : {}", questionId, contents);
    	Map<String, Object> values = Maps.newHashMap();
		if (!UserSessionUtils.isLogined(session)) {
			values.put("result", Result.fail("Login is required"));
			return values;
		}
    	
    	User loginUser = UserSessionUtils.getUserFromSession(session);
    	Answer answer = new Answer(loginUser.getUserId(), contents, questionId);
    	Answer savedAnswer = answerDao.insert(answer);
		questionDao.updateCountOfAnswer(savedAnswer.getQuestionId());
		
		values.put("answer", savedAnswer);
		values.put("result", Result.ok());
		return values;
	}
	
	@RequestMapping(value = "/{questionId}/answers/{answerId}", method = RequestMethod.DELETE)
	public Result deleteAnswer(HttpSession session, @PathVariable Long answerId) throws Exception {
		if (!UserSessionUtils.isLogined(session)) {
			return Result.fail("Login is required");
		}
		
		Answer answer = answerDao.findById(answerId);
		if (!answer.isSameUser(UserSessionUtils.getUserFromSession(session))) {
			return Result.fail("다른 사용자가 쓴 글을 삭제할 수 없습니다.");
		}
		
		try {
			answerDao.delete(answerId);
			return Result.ok();
		} catch (DataAccessException e) {
			return Result.fail(e.getMessage());
		}
	}
}
