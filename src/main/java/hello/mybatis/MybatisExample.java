package hello.mybatis;

import hello.mybatis.config.*;
import hello.mybatis.repository.ItemRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;


//@Import(MemoryConfig.class)
//@Import(JdbcTemplateConfigV1.class)
//@Import(JdbcTemplateConfigV2.class)
@Log4j2
@Import(MybatisConfig.class)
@SpringBootApplication(scanBasePackages = "hello.mybatis.web")
public class MybatisExample {

    public static void main(String[] args) {
        SpringApplication.run(MybatisExample.class, args);
    }

    @Bean
    @Profile("local")
    public TestDataInit testDataInit(ItemRepository itemRepository) {
        return new TestDataInit(itemRepository);
    }


    /**
     * Profile이 test인 경우 => application.properties에 spring.profiles.active=test => 즉 테스트 코드를 실행하는 경우 아래와 같이 <br><br>
     * Datasource Bean을 직접 커스텀하여 컨테이너가 관리하도록 할 것이다.<br><br>
     * 커스텀이라고 말한 이유 : DataSource 와 TranscationManager는 스프링이 컨테이너가 스스로관리하여 주입이 필요한 경우 알아서 주입을해준다.
     *
     * @return dataSource
     */
//    @Bean
//    @Profile("test")
//    public DataSource dataSource() {
//        log.info("메모리 데이터 베이스 초기화");
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("org.h2.Driver");
//        dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
//        dataSource.setUsername("sa");
//        dataSource.setPassword("");
//        return dataSource;
//    }
}
