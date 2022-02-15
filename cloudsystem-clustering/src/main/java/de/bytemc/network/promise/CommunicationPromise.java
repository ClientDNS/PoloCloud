package de.bytemc.network.promise;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class CommunicationPromise<T> extends DefaultPromise<T> implements ICommunicationPromise<T> {

    private final long timeout = 200;
    private final boolean enableTimeout = true;

    @Override
    protected EventExecutor executor() {
        return GlobalEventExecutor.INSTANCE;
    }

    @Override
    public ICommunicationPromise<T> setFailure(Throwable cause) {
        super.setFailure(cause);
        return this;
    }

    @Override
    public ICommunicationPromise<T> addFailureListener(Consumer<Throwable> listener) {
        addCompleteListener((ICommunicationPromiseListener<T>) future -> {
            if (future.cause() != null) listener.accept(future.cause());
        });
        return this;
    }

    @Override
    public ICommunicationPromise<T> addResultListener(Consumer<T> listener) {
        addCompleteListener((ICommunicationPromiseListener<T>) future -> {
            if (future.isSuccess()) {
                try {
                    listener.accept(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        return this;
    }

    public ICommunicationPromise<T> addCompleteListener(Consumer<ICommunicationPromise<T>> listener) {
        addCompleteListener((ICommunicationPromiseListener<T>) listener::accept);
        return this;
    }

    public ICommunicationPromise<T> addCompleteListener(final ICommunicationPromiseListener<T> listener) {
        this.addListener(listener);
        return this;
    }
}
