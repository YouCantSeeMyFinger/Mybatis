package hello.mybatis.domain;

import hello.mybatis.repository.ItemRepository;
import hello.mybatis.repository.ItemSearchCond;
import hello.mybatis.repository.ItemUpdateDto;
import hello.mybatis.repository.memory.MemoryItemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @SpringBootTest 는 상위에서 SpringBootApplication을 찾는다.<br><br>
 * 찾은 후에 해당 설정을 사용을한다.<br><br>
 * @Transactional의 경우 다음과 같이 작동한다고 공부했다.<br><br>
 * 1. RuntimeException 이 발동하는 경우 롤백한다.<br><br>
 * 2. RundtimeException을 제외한 CheckedException의 경우 커밋한다.<br><br>
 * 다만 Test에서는 트랙젝션을 자동으로 롤백시키는 기능이있다.<br><br>
 */


@Transactional
@SpringBootTest
class ItemRepositoryTest {

    /**
     * DataSource 와 TransactionManager는 스프링 부트가 자동으로 Bean을 주입해준다. <br><br>
     * 그렇다면 itemRepository는 왜 @Autowired가 가능한 것일까?<br><br>
     * 이유 : config에 보면 이미 @Bean을 붙여서 컨테이너가 관리하도록 만들었기 떄문이다.
     */

    @Autowired
    ItemRepository itemRepository;

//    @Autowired
//    PlatformTransactionManager transactionManager;

//    TransactionStatus status;

//    @BeforeEach
//    void beforeEach() {
//        // 트랜잭션 매니저에게서 connection을 받아온다.
//        // 트랜잭션을 할 시에 가장 중요한점은 동일한 connection을 가져온다는 것이다 .
//        // 기존 순수 jdbc만을 사용하여 connection을 가져올 때 각 메소드의 파라미터에 connection을 기입해줘야하지만 트랜잭션 매니저를 사용 함으로써
//        // 그 수고스러움이 덜어졌다.
//
//        // 트랜잭션 시작. (AutoWired = false , 참고로 사용하는 db마다 다르다.)
//        // 서비스의 모든 메소드는 트랜잭션 동기화매니저에서  connection을 가지고 오는 것이다.
//        // 사실 @Transactional을 사용하면 이러한 과정을 다 처리해준다.
//        status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
//    }


//    @AfterEach
//    void afterEach() {
//        //MemoryItemRepository 의 경우 제한적으로 사용
//        if (itemRepository instanceof MemoryItemRepository) {
//            ((MemoryItemRepository) itemRepository).clearStore();
//        }
//        this.transactionManager.rollback(status);
//    }

    @Test
    void save() {
        //given
        Item item = new Item("itemA", 10000, 10);

        //when
        Item savedItem = itemRepository.save(item);

        //then
        Item findItem = itemRepository.findById(item.getId()).get();
        assertThat(findItem).isEqualTo(savedItem);
    }

    @Test
    void updateItem() {
        //given
        Item item = new Item("item1", 10000, 10);
        Item savedItem = itemRepository.save(item);
        Long itemId = savedItem.getId();

        //when
        ItemUpdateDto updateParam = new ItemUpdateDto("item2", 20000, 30);
        itemRepository.update(itemId, updateParam);

        //then
        Item findItem = itemRepository.findById(itemId).get();
        assertThat(findItem.getItemName()).isEqualTo(updateParam.getItemName());
        assertThat(findItem.getPrice()).isEqualTo(updateParam.getPrice());
        assertThat(findItem.getQuantity()).isEqualTo(updateParam.getQuantity());
    }

    @Test
    void findItems() {
        //given
        Item item1 = new Item("itemA-1", 10000, 10);
        Item item2 = new Item("itemA-2", 20000, 20);
        Item item3 = new Item("itemB-1", 30000, 30);

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        //둘 다 없음 검증
        test(null, null, item1, item2, item3);
        test("", null, item1, item2, item3);

        //itemName 검증
        test("itemA", null, item1, item2);
        test("temA", null, item1, item2);
        test("itemB", null, item3);

        //maxPrice 검증
        test(null, 10000, item1);

        //둘 다 있음 검증
        test("itemA", 10000, item1);
    }

    void test(String itemName, Integer maxPrice, Item... items) {
        List<Item> result = itemRepository.findAll(new ItemSearchCond(itemName, maxPrice));
        assertThat(result).containsExactly(items);
    }
}
