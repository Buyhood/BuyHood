package api.buyhood.concurrency;

import api.buyhood.domain.cart.entity.Cart;
import api.buyhood.domain.cart.entity.CartItem;
import api.buyhood.domain.cart.repository.CartRepository;
import api.buyhood.domain.order.dto.request.ApplyOrderReq;
import api.buyhood.domain.order.service.OrderService;
import api.buyhood.domain.product.entity.Product;
import api.buyhood.domain.product.repository.ProductRepository;
import api.buyhood.domain.seller.entity.Seller;
import api.buyhood.domain.seller.repository.SellerRepository;
import api.buyhood.domain.store.entity.Store;
import api.buyhood.domain.store.repository.StoreRepository;
import api.buyhood.domain.user.entity.User;
import api.buyhood.domain.user.repository.UserRepository;
import api.buyhood.enums.UserRole;
import api.buyhood.exception.NotFoundException;
import api.buyhood.security.AuthUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static api.buyhood.domain.order.enums.PaymentMethod.CARD;
import static api.buyhood.errorcode.ProductErrorCode.PRODUCT_NOT_FOUND;

@SpringBootTest
public class OrderWithoutLockTest {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    //user
    public static final String TEST_USER_EMAIL = "user@test.com";
    public static final User TEST_USER = new User("유저", TEST_USER_EMAIL, "12341234", "주소", "전화번호");

    //authUser
    public static final Long TEST_AUTH_USER_ID = TEST_USER.getId();
    public static final AuthUser TEST_AUTH_USER = new AuthUser(TEST_AUTH_USER_ID,TEST_USER_EMAIL, UserRole.USER);


    //seller
    public static final Seller TEST_SELLER = new Seller("seller", "seller@seller.com", "password", "사업자 번호", "전화번호");

    //store
    public static final Long TEST_STORE_ID = 1L;
    public static final Store TEST_STORE = new Store("가게", "주소", TEST_SELLER,true, "설명", LocalTime.parse("08:00:00"), LocalTime.parse("22:00:00"));

    //product
    public static final Long TEST_PRODUCT_ID = 1L;
    public static final Long TEST_STOCK = 100L;
    public static final Product TEST_PRODUCT = new Product("제품", 100L, "설명", TEST_STOCK, TEST_STORE);

    //cart
    public static final CartItem TEST_CART_ITEM = CartItem.builder()
            .productId(TEST_PRODUCT_ID)
            .quantity(1)
            .build();

    public static final Cart TEST_CART = Cart.builder()
            .cart(List.of(TEST_CART_ITEM))
            .build();

    //request
    public static final ApplyOrderReq TEST_APPLY_REQ = new ApplyOrderReq(CARD,TEST_STORE_ID, "테스트 주문 요청 메세지");

    @BeforeEach
    void setUp() {
        sellerRepository.save(TEST_SELLER);
        storeRepository.save(TEST_STORE);

        userRepository.save(TEST_USER);
        productRepository.save(TEST_PRODUCT);
    }

    /* 낙관락 적용 전 작성한 테스트 코드 */
    @Test
    void 동시에_100개의_주문_요청_동시성제어_X() throws InterruptedException {
        //given
        int testCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(10); // 10개의 스레드
        CountDownLatch latch = new CountDownLatch(testCount); // 모든 스레드가 끝날 때까지 대기

        // 성공한 주문 수
        AtomicInteger successfulOrder = new AtomicInteger(0);

        // 실패한 주문 수
        AtomicInteger failedOrder = new AtomicInteger(0);

        //when
        for (int i = 0; i < testCount; i++) {
            executorService.submit(() -> {
                try {
                    cartRepository.add(TEST_AUTH_USER_ID, TEST_CART);
                    orderService.applyOrder(TEST_APPLY_REQ, TEST_AUTH_USER);
                    successfulOrder.incrementAndGet(); // 성공한 예매 횟수 증가
                } catch (Exception e) {
                    failedOrder.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Product product = productRepository.findById(TEST_PRODUCT_ID)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));

        Long stock = product.getStock();

        System.out.println("성공한 주문 수: " + successfulOrder.get());
        System.out.println("남은 재고 수: " + stock);
        System.out.println("실패한 주문 수: " + failedOrder.get());
    }
}

