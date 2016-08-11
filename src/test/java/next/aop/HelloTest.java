package next.aop;

import java.lang.reflect.Proxy;

import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactoryBean;

public class HelloTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void proxyFactoryBean() {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());
		pfBean.addAdvice(new UppercaseAdvice());
		Hello proxiedHello = (Hello) pfBean.getObject();

		System.out.println(proxiedHello.sayHello("jun proxyfb"));
	}

	@Test
	public void hello() {
		Hello hello = new HelloTarget();
		HelloUppercase proxy = new HelloUppercase(hello);
		System.out.println(proxy.sayHello("jun"));
	}

	@Test
	public void simpleProxy() {
		Hello proxiedHello = (Hello) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { Hello.class },
				new UppercaseHandler(new HelloTarget()));
		System.out.println(proxiedHello.sayHi("jun"));
		System.out.println(proxiedHello.sayHello("jun"));
	}

}
