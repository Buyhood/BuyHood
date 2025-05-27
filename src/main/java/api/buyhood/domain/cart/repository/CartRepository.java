package api.buyhood.domain.cart.repository;

import api.buyhood.domain.cart.entity.Cart;
import api.buyhood.global.common.exception.InvalidRequestException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

import static api.buyhood.global.common.exception.enums.CommonErrorCode.REDIS_SERIALIZE_FAILED;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CartRepository {
    private final RedisTemplate<Object, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String CART_KEY_PREFIX = "cart:";
    private static final Duration CART_TTL = Duration.ofMinutes(60 * 24);

    //장바구니 담기
    public void add(Long userId, Cart cart){

        try {
            String value = objectMapper.writeValueAsString(cart);
            redisTemplate.opsForValue().set(CART_KEY_PREFIX + userId, value, CART_TTL);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize {}: {}", userId, cart, e);
            throw new InvalidRequestException(REDIS_SERIALIZE_FAILED);
        }
    }
}
