package api.buyhood.domain.cart.repository;

import api.buyhood.domain.cart.entity.Cart;
import api.buyhood.domain.cart.entity.CartItem;
import api.buyhood.domain.product.entity.Product;
import api.buyhood.domain.product.repository.ProductRepository;
import api.exception.InvalidRequestException;
import api.exception.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import static api.errorcode.CommonErrorCode.JSON_PARSING_FAILED;
import static api.errorcode.CommonErrorCode.REDIS_SERIALIZE_FAILED;
import static api.errorcode.StoreErrorCode.STORE_NOT_FOUND;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CartRepository {

	private final ProductRepository productRepository;
	private final RedisTemplate<Object, Object> redisTemplate;
	private final ObjectMapper objectMapper;
	private static final String CART_KEY_PREFIX = "cart:";
	private static final Duration CART_TTL = Duration.ofMinutes(60 * 24);//24시간

	//장바구니 담기
	public void add(Long userId, Cart cart) {

		try {
			String value = objectMapper.writeValueAsString(cart);
			redisTemplate.opsForValue().set(CART_KEY_PREFIX + userId, value, CART_TTL);

		} catch (JsonProcessingException e) {
			log.error("Failed to serialize {}: {}", userId, cart, e);
			throw new InvalidRequestException(REDIS_SERIALIZE_FAILED);
		}
	}

	//장바구니 조회
	public Cart findCart(Long userId) {
		String value = (String) redisTemplate.opsForValue().get(CART_KEY_PREFIX + userId);

		try {
			return objectMapper.readValue(value, Cart.class);
		} catch (JsonProcessingException e) {
			log.error("Failed to serialize {}: {}", userId, value, e);
			throw new InvalidRequestException(JSON_PARSING_FAILED);
		}

	}

	//장바구니에 담긴 가게 조회
	public Long findCartWithStore(Long userId) {
		String value = (String) redisTemplate.opsForValue().get(CART_KEY_PREFIX + userId);

		try {
			Cart cart = objectMapper.readValue(value, Cart.class);
			CartItem cartItem = cart.getCart().stream().iterator().next();

			Product product = productRepository.findById(cartItem.getProductId())
				.orElseThrow(() -> new NotFoundException(STORE_NOT_FOUND));

			return product.getStore().getId();

		} catch (JsonProcessingException e) {
			log.error("Failed to serialize {}: {}", userId, value, e);
			throw new InvalidRequestException(JSON_PARSING_FAILED);
		}

	}

	//장바구니 비우기
	public void clearCart(Long userId) {
		redisTemplate.delete(CART_KEY_PREFIX + userId);
	}

	//장바구니 존재 여부 확인
	public boolean existsCart(Long userId) {
		return redisTemplate.hasKey(CART_KEY_PREFIX + userId);
	}

}
