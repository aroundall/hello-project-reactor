package org.amuji.hello;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class ReactorDemo {
    public static void main(String[] args) throws InterruptedException {
        // 创建一个 Flux
        Flux<String> flux = Flux.just("Apple", "Orange", "Grape", "Banana", "Strawberry");

        // 使用 publishOn 切换到多个线程。
        // 但是需要注意的是，即使是在多线程的环境下，Flux 依然保证了事件的处理顺序。
        // 也就是说，尽管事件可能在不同的线程中并行处理，但它们的完成顺序依然是按照原始 Flux 中的顺序。
        flux.publishOn(Schedulers.parallel())
                .log()  // 添加日志
                .map(item -> {
                    // 模拟耗时操作
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        log.error("Error occurred while sleeping the thread", e);
                    }
                    return item.toUpperCase();
                })
                .subscribe(item -> log.info("{} - {}", Thread.currentThread().getName(), item));

        log.info("The main thread is going to sleep");
        Thread.sleep(6000);
        log.info("The main thread woke up, and the app is going to finish.");
    }
}
