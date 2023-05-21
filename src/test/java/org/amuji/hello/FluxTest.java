package org.amuji.hello;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
class FluxTest {

    @Test
    void even_parallel_processing_the_subscriber_still_handles_by_sequence() {
        //GIVEN
        Flux<String> flux = Flux.just("Apple", "Orange", "Grape", "Banana", "Strawberry");

        //WHEN
        // 使用 publishOn 切换到多个线程。
        // 但是需要注意的是，即使是在多线程的环境下，Flux 依然保证了事件的处理顺序。
        // 也就是说，尽管事件可能在不同的线程中并行处理，但它们的完成顺序依然是按照原始 Flux 中的顺序。
        List<String> consumed = new LinkedList<>();
        flux.publishOn(Schedulers.parallel())
                .log()  // 添加日志
                .map(item -> {
                    // 模拟耗时操作
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        log.error("Error occurred while sleeping the thread", e);
                    }
                    return item.toUpperCase();
                })
                .subscribe(item -> {
                    consumed.add(item);
                    log.info("{} - {}", Thread.currentThread().getName(), item);
                });

        List<String> expected = new ArrayList<>(Arrays.asList("APPLE", "ORANGE", "GRAPE", "BANANA", "STRAWBERRY"));

        // THEN
        log.info("The main thread is waiting");
        await().atMost(6, TimeUnit.SECONDS).untilAsserted(
                () -> assertThat(consumed).containsExactlyElementsOf(expected));
        log.info("The main thread is resumed, and the test is going to finish.");
    }
}