package de.polocloud.network.promise;

import io.netty.util.concurrent.GenericFutureListener;

public interface ICommunicationPromiseListener<T> extends GenericFutureListener<ICommunicationPromise<T>> {

    @Override
    void operationComplete(ICommunicationPromise<T> future);

}
