package hello.mybatis.repository.mybatis;

import hello.mybatis.domain.Item;
import hello.mybatis.repository.ItemSearchCond;
import hello.mybatis.repository.ItemUpdateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ItemMapper {

    void save(Item item);


    /**
     * 파라미터가 2개인 경우 아래와 같이 @Param을 사용한다.
     *
     * @param id
     * @param itemUpdateDto
     */
    void update(@Param("id") Long id, @Param("updateParam") ItemUpdateDto itemUpdateDto);

    Optional<Item> findById(Long id);

    List<Item> findAll(ItemSearchCond itemSearchCond);


}
