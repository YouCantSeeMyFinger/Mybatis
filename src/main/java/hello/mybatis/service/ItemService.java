package hello.mybatis.service;

import hello.mybatis.domain.Item;
import hello.mybatis.repository.ItemSearchCond;
import hello.mybatis.repository.ItemUpdateDto;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    Item save(Item item);

    void update(Long itemId, ItemUpdateDto updateParam);

    Optional<Item> findById(Long id);

    List<Item> findItems(ItemSearchCond itemSearch);
}
