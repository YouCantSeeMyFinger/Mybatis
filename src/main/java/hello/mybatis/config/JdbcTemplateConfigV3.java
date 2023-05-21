package hello.mybatis.config;


import hello.mybatis.repository.ItemRepository;
import hello.mybatis.repository.jdbctemplate.JdbcTemplateItemRepositoryV3;
import hello.mybatis.service.ItemService;
import hello.mybatis.service.ItemServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class JdbcTemplateConfigV3 {

    private final DataSource dataSource;

    @Bean
    public ItemRepository itemRepository() {
        return new JdbcTemplateItemRepositoryV3(this.dataSource);
    }

    @Bean
    public ItemService service() {
        return new ItemServiceV1(this.itemRepository());
    }

}
