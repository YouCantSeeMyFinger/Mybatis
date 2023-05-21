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
    }
}
