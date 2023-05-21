package hello.mybatis.config;

import hello.mybatis.repository.ItemRepository;
import hello.mybatis.repository.jdbctemplate.JdbcTemplateItemRepositoryV2;
import hello.mybatis.service.ItemService;
import hello.mybatis.service.ItemServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import javax.xml.crypto.Data;

@Configuration
@RequiredArgsConstructor
public class JdbcTemplateConfigV2 {

    private final DataSource dataSource;

    @Bean
    public ItemRepository itemRepository() {
        return new JdbcTemplateItemRepositoryV2(this.dataSource);
    }

    @Bean
    public ItemService ItemService() {
        return new ItemServiceV1(this.itemRepository());
    }


}
