package api.buyhood.concurrency;

import api.buyhood.domain.cart.repository.CartRepository;
import api.buyhood.domain.order.service.OrderService;
import api.buyhood.domain.product.entity.Product;
import api.buyhood.domain.product.repository.ProductRepository;
import api.buyhood.domain.seller.repository.SellerRepository;
import api.buyhood.domain.store.repository.StoreRepository;
import api.buyhood.domain.user.repository.UserRepository;
import api.exception.NotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static api.buyhood.concurrency.OrderWithoutLockTest.*;
import static api.errorcode.ProductErrorCode.PRODUCT_NOT_FOUND;

@SpringBootTest
public class OrderWithOptimisticLockTest {
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


    @BeforeEach
    void setUp() {
        sellerRepository.save(TEST_SELLER);
        storeRepository.save(TEST_STORE);

        userRepository.save(TEST_USER);
        productRepository.save(TEST_PRODUCT);
    }

    @Test
    void 동시에_100개의_주문_요청_낙관락_적용() throws InterruptedException {
        //given
        int testCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(10); // 10개의 스레드
        CountDownLatch latch = new CountDownLatch(testCount); // 모든 스레드가 끝날 때까지 대기

        // 성공한 주문 수
        AtomicInteger successfulOrder = new AtomicInteger(0);

        // 낙관락 관련 실패 수
        AtomicInteger failedOrderOptimistic = new AtomicInteger(0);

        // 재고 감소, retry 등의 다른 예외로 인한 실패 수
        AtomicInteger failedOrder = new AtomicInteger(0);

        //when
        for (int i = 0; i < testCount; i++) {
            executorService.submit(() -> {
                try {
                    cartRepository.add(TEST_AUTH_USER_ID, TEST_CART);
                    orderService.applyOrder(TEST_APPLY_REQ, TEST_AUTH_USER);
                    successfulOrder.incrementAndGet(); // 성공한 예매 횟수 증가
                } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
                    failedOrderOptimistic.incrementAndGet();
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
        System.out.println("낙관락 실패 수: " + failedOrderOptimistic.get());
        System.out.println("실패한 주문 수: " + failedOrder.get());
        System.out.println("남은 재고 수: " + stock);
    }
}