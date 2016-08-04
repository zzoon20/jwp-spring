package next.config;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import core.web.argumentresolver.LoginUserHandlerMethodArgumentResolver;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "next.controller", "next.service", "next.dao" })
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    private static final int CACHE_PERIOD = 31556926; // one year
    private static final String DB_DRIVER = "org.h2.Driver";
	private static final String DB_URL = "jdbc:h2:~/jwp-basic;AUTO_SERVER=TRUE";
	private static final String DB_USERNAME = "sa";
	private static final String DB_PW = "";
	@Autowired
	private DataSource dataSource;

	@Bean
	public DataSource dataSource() {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(DB_DRIVER);
		ds.setUrl(DB_URL);
		ds.setUsername(DB_USERNAME);
		ds.setPassword(DB_PW);
		return ds;
	}
	
    @Bean
    public JdbcTemplate jdbcTemplate(){
    	return new JdbcTemplate(dataSource);
    }
    
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setViewClass(JstlView.class);
        bean.setPrefix("/WEB-INF/jsp/");
        bean.setSuffix(".jsp");
        return bean;
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/resources/**")
                .addResourceLocations("/WEB-INF/static_resources/")
                .setCachePeriod(CACHE_PERIOD);
    }
    
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    	argumentResolvers.add(new LoginUserHandlerMethodArgumentResolver());
    }
    
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        // Serving static files using the Servlet container's default Servlet.
        configurer.enable();
    }    
}
