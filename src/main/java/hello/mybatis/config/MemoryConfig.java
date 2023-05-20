package hello.mybatis.config;

import hello.mybatis.repository.ItemRepository;
import hello.mybatis.repository.memory.MemoryItemRepository;
import hello.mybatis.service.ItemService;
import hello.mybatis.service.ItemServiceV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
