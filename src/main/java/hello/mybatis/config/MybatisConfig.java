package hello.mybatis.config;

import hello.mybatis.repository.ItemRepository;
import hello.mybatis.repository.mybatis.ItemMapper;
import hello.mybatis.repository.mybatis.MybatisItemRepository;
import hello.mybatis.service.ItemService;
import hello.mybatis.service.ItemServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * ItemMapper가 DataSource , transactionManager등을 알아서 연결해서 하기 때문에 myBatis를 사용 한다면 itemMapper만 주입 받으면 된다.
 *
 */
@Configuration
@RequiredArgsConstructor
public class MybatisConfig {


    private final ItemMapper itemMapper;

    @Bean
    ItemRepository itemRepository() {
        return new MybatisItemRepository(this.itemMapper);
    }

    @Bean
    ItemService service() {
        return new ItemServiceV1(this.itemRepository());
    }
}
