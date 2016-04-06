package next.support;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContextEvent;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

@Component
public class DBInitializer {
	private static final Logger log = LoggerFactory.getLogger(DBInitializer.class);
	
	@Autowired
	private DataSource dataSource;

	@PostConstruct
	public void contextInitialized(ServletContextEvent sce) {
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(new ClassPathResource("jwp.sql"));
		DatabasePopulatorUtils.execute(populator, dataSource);
		log.info("Initialized Database!");
	}
}
