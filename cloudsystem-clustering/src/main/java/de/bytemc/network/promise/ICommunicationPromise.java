package de.bytemc.network.promise;

import com.google.common.collect.Lists;
import io.netty.util.concurrent.Promise;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public interface ICommunicationPromise<T> extends Promise<T> {

    ICommunicationPromise<T> addFailureListener(Consumer<Throwable> listener);

    ICommunicationPromise<T> addResultListener(Consumer<T> listener);

    ICommunicationPromise<T> addCompleteListener(Consumer<ICommunicationPromise<T>> listener);

    default T getBlocking() {
        return syncUninterruptibly().getNow();
    }

    static ICommunicationPromise<Void> combineAllToUnitPromise(Collection<ICommunicationPromise<?>> promises) {
        var unitPromise = new CommunicationPromise<Void>();
        if (promises.isEmpty()) {
            unitPromise.setSuccess(null);
            return unitPromise;
        }
        promises.forEach(promise -> promise.addCompleteListener(it -> {
            if (promises.stream().allMatch(Future::isDone) && !unitPromise.isDone()) {
                unitPromise.setSuccess(null);
            }
        }));
        return unitPromise;
    }

    static ICommunicationPromise<Void> combine(ICommunicationPromise<?> communicationPromise) {
        return combineAllToUnitPromise(Lists.newArrayList(communicationPromise));
    }

    static ICommunicationPromise<Void> combineAll(List<ICommunicationPromise<?>> promises) {
        return combineAllToUnitPromise(promises);
    }

}
