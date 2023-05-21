package hello.mybatis.repository.jdbctemplate;

import hello.mybatis.domain.Item;
import hello.mybatis.repository.ItemRepository;
import hello.mybatis.repository.ItemSearchCond;
import hello.mybatis.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * NamedParameterJdbcTemplate
 *
 * SqlParameterSource
 * - BeanPropertySqlParameterSource
 * - Map
 * - MapSqlParameterSource
 */
@Repository
@Slf4j
public class JdbcTemplateItemRepositoryV2 implements ItemRepository {

    // JdbcTemplate의 경우 DataSource가 필요하다.
    // 설정이 변경되었으므로 추후에 Config의 Bean도 변경해주어야 한다.
    // @import도 새로 생성한 config로 변경해줄 수 있도록하자.
    // private final JdbcTemplate template;

    // 변경내용 : v1에서는 JdbcTemplate => NameParameterJdbcTemplate
    // 이유 : template.update에서 바인딩변수는 순서에 맞게 바인딩 하지만 서비스를 하면서 수량에 가격이 , 가격에 수량이 저장되는
    //       버그의 소지가 있기 때문에 NamedParameterJdbcTemplate 채택
    private final NamedParameterJdbcTemplate template;

    public JdbcTemplateItemRepositoryV2(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }


    /**
     * SqlParameterSource source = new BeanPropertySqlParameterSource(item) <br><br>
     *
     * key = itemName <br><br>
     * value = [itemName의 실제 값]으로 해준다.
     * @param item
     * @return
     */

    @Override
    public Item save(Item item) {
        // id는 db가 직접 생성 때문에 id값을 개발자가 지정해주지 않아도 된다.
        String sql = "insert into item(item_name , price , quantity) values(:itemName,:price,:quantity)";

        // 변경사항 NamedParmeterJdbcTemplate 방법 1
        SqlParameterSource parameter = new BeanPropertySqlParameterSource(item);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        this.template.update(sql, parameter, keyHolder);
        long key = keyHolder.getKey().longValue();
        item.setId(key);
        return item;
    }

    /**
     * 아래의 경우 SqlParameterSource parameter = new BeanPropertySqlParameterSource()를 사용못한다. <br>
     * 이유 : BeanProperty 말 그대로 해당 객체의 필드의 값을 가져와서 사용하는 것이다. <br><br>
     * 하지만 아래의 메소드의 경우에는 id값도 설정해주어야한는대 ItemUpdateDTO의 BeanProperty를 확인하면 어디에도 Id Propertry가 존재하지 않는다.<br><br>
     * 때문에 Map.of 혹은 MapSqlParameterSource를 사용 할 수 밖에 없다.<br><br>
     *
     * @param itemId
     * @param updateParam
     */

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        String sql = "update item set item_name = :itemName , price =:price , quantity = :quantity where id = :id";

        // 변경사항 NamedParmeterJdbcTemplate 방법 2
        SqlParameterSource parameter = new MapSqlParameterSource()
                .addValue("itemName", updateParam.getItemName())
                .addValue("price", updateParam.getPrice())
                .addValue("quantity", updateParam.getQuantity())
                .addValue("id", itemId);

        this.template.update(sql, parameter);
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id , item_name , price , quantity from item where id = :id";

        try {
            // 변경사항 NamedParmeterJdbcTemplate 방법 3
            Map<String, Object> param = Map.of("id", id);

            // 자바 9부터 도입된 Map.of 비슷한 예제로 List.of로 리스트를 new 연산자를 사용하지 않고 만들수가 있다.
            // 하지만 주의할점이 2가지가 있다.
            // 첫번째 immutable 하다. 다시말해 put 혹은 remove와 같은 메소드로 객체의 데이터를 변경할 수 없게 된다는 것이다.
            // 두번 째 기존 new 연산자를 사용하여 JCF의 인스터를 생성할 때 생성자에 파라미터값을 넣어줄 수가 있는대 이때 선언하지 않는 경우 기본 크기가 10으로 지정된다.
            // 그런데 Map.of , List.of등과 같이 JCF를 초기화하는 작업을 하면 기본크기가 10으로 고정이된다.

            // Map의 경우 Map.entry를 사용한다면 size가 10이상으로 Map을 초기화 할 수가 있다.

            Item item = this.template.queryForObject(sql, param, this.itemRowMapper());
            return Optional.of(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String sql = "select id, item_name , price , quantity from item";
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        SqlParameterSource param = new BeanPropertySqlParameterSource(cond);

        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where";
        }
        boolean andFlag = false;
        if (StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%',:itemName,'%')";
            andFlag = true;
        }
        if (maxPrice != null) {
            if (andFlag) {
                sql += " and";
            }
            sql += " price <= :maxPrice";
        }
        log.info("sql={}", sql);
        return this.template.query(sql, param, itemRowMapper());
    }

    /**
     * 기존에는 rs를 이용 <br>
     * 하지만 BeanPropertyRowMapper.newInstance([변환할 객체])를 사용하면 변환 객체의 필드에 맞게 매핑해 주고 객체를 반환해준다.
     * @return item
     */

    private RowMapper<Item> itemRowMapper() {
        return BeanPropertyRowMapper.newInstance(Item.class);
        // 정리하자면 BeanPropertyRowMapper는 ResultSet의 결과를 받아서 자바 빈 규약에 맞추어 데이터를 변환한다.
    }
}
