package next.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
public class PerformanceAspect {
	private static final Logger logger = LoggerFactory.getLogger(PerformanceAspect.class);

	@Pointcut("within(next.service..*) || within(next.dao..*)")
	public void performancePointcut() {
	}

	@Pointcut("within(next.dao..*)")
	public void paramPointcut() {
	}

	@Around("performancePointcut()")
	public Object doBasicProfilng(ProceedingJoinPoint pjp) throws Throwable {
		StopWatch watch = new StopWatch();
		watch.start();
		Object ret = pjp.proceed();
		watch.stop();

		logger.debug("{} result {} ms", pjp.toShortString(), watch.getTotalTimeMillis());
		return ret;
	}

	@Before("paramPointcut()")
	public void doAccess(JoinPoint jp) {
		Object[] params = jp.getArgs();
		for (Object object : params) {
			logger.debug("{} receive param {}", jp.toShortString(), object.toString());
		}
	}

}
