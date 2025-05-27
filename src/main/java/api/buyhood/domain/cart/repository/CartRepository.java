package api.buyhood.domain.cart.repository;

import api.buyhood.domain.cart.entity.CartItem;
import api.buyhood.global.common.exception.enums.BadRequestException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

import static api.buyhood.global.common.exception.enums.CommonErrorCode.REDIS_SERIALIZE_FAILED;

@Repository
@RequiredArgsConstructor
public class CartRepository {
    private final RedisTemplate<Object, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String CART_KEY_PREFIX = "cart:";
    private static final Duration CART_TTL = Duration.ofMinutes(60 * 24);

    //장바구니 담기
    public void add(Long userId, List<CartItem> cartList){

        try {
            String cart = objectMapper.writeValueAsString(cartList);
            redisTemplate.opsForValue().set(CART_KEY_PREFIX + userId, cart, CART_TTL);

        } catch (JsonProcessingException e) {
            throw new BadRequestException(REDIS_SERIALIZE_FAILED);
        }
    }

}
