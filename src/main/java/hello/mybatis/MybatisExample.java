package hello.mybatis;

import hello.mybatis.config.*;
import hello.mybatis.repository.ItemRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;


//@Import(MemoryConfig.class)
//@Import(JdbcTemplateConfigV1.class)
//@Import(JdbcTemplateConfigV2.class)
@Import(JdbcTemplateConfigV3.class)
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

}
