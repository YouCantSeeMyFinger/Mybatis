package hello.mybatis.config;

import hello.mybatis.repository.ItemRepository;
import hello.mybatis.repository.memory.MemoryItemRepository;
import hello.mybatis.service.ItemService;
import hello.mybatis.service.ItemServiceV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * MybatisExample에서 Import 설정해줘야한다.
 */
@Configuration
public class MemoryConfig {

    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }

    @Bean
    public ItemRepository itemRepository() {
        return new MemoryItemRepository();
    }

}
