package finance.configs

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.h2.server.web.WebServlet
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment

@Configuration
@EnableTransactionManagement
@Profile("offline")
@PropertySource("classpath:database-h2.properties")
open class H2Config  @Autowired constructor(
            private var environment: Environment
    )  {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val url = "url"
    private val username = "dbuser"
    private val driver = "driver"
    private val password = "dbpassword"
    private val schema = "schema"
    private val data = "data"

    //schema=classpath:schema-h2.sql
    //data=classpath:data-h2.sql

    @Bean
    open fun dataSource(): DataSource {
        val dataSource = DriverManagerDataSource()
        val driverName = environment.getProperty(driver)
        if( driverName == null ) {
            logger.info("driverName is NULL.")
        } else {
            dataSource.setDriverClassName(driverName)
        }
        dataSource.url = environment.getProperty(url)
        dataSource.username = environment.getProperty(username)
        dataSource.password = environment.getProperty(password)

        logger.info("schema = ${environment.getProperty(schema)}")
        logger.info("data = ${environment.getProperty(data)}")

        return dataSource
    }

    @Bean
    open fun h2servletRegistration(): ServletRegistrationBean<*> {
        val registration = ServletRegistrationBean(WebServlet())
        registration.addUrlMappings("/h2-console/*")
        return registration
    }

    //@Bean
    //open fun jdbcTemplate(dataSource: DataSource): JdbcTemplate {
    //    return JdbcTemplate(dataSource)
    //}
}
