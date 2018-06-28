package com.fingerchat.api.client;

import com.fingerchat.api.Logger;
import com.fingerchat.api.ack.AckCallback;
import com.fingerchat.api.ack.AckContext;
import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.api.util.thread.ExecutorManager;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by LY309313 on 2017/9/23.
 */

public final class AckRequestMgr {


    private static AckRequestMgr I;

    private final Logger logger = ClientConfig.I.getLogger();

    private final Map<Integer, RequestTask> queue = new ConcurrentHashMap<>();
    private final ScheduledExecutorService timer = ExecutorManager.INSTANCE.getTimerThread();
    private final Callable<Boolean> NONE = new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
            return Boolean.FALSE;
        }
    };
    private Connection connection;


    public static AckRequestMgr I() {
        if (I == null) {
            synchronized (AckRequestMgr.class) {
                if (I == null) {
                    I = new AckRequestMgr();
                }
            }
        }
        return I;
    }

    private AckRequestMgr() {
    }

    public Future<Boolean> add(int messageSeq, AckContext context) {

        if (context.callback == null) return null;
        return addTask(new RequestTask(messageSeq, context));
    }

    public RequestTask getAndRemove(int messageSeq) {
        return queue.remove(messageSeq);
    }


    public void clear() {
        for (RequestTask task : queue.values()) {
            try {
                task.future.cancel(true);
            } catch (Exception e) {
            }
        }
    }

    private RequestTask addTask(RequestTask task) {
        queue.put(task.messageSeq, task);
        task.future = timer.schedule(task, task.timeout, TimeUnit.MILLISECONDS);
        return task;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public final class RequestTask extends FutureTask<Boolean> implements Runnable {
        private final int timeout;
        private final long sendTime;
        private final int messageSeq;
        private AckCallback callback;
        private Packet request;
        private Future<?> future;
        private int retryCount;

        private RequestTask(AckCallback callback, int timeout, int messageSeq, Packet request, int retryCount) {
            super(NONE);
            this.callback = callback;
            this.timeout = timeout;
            this.sendTime = System.currentTimeMillis();
            this.messageSeq = messageSeq;
            this.request = request;
            this.retryCount = retryCount;
        }

        private RequestTask(int messageSeq, AckContext context) {
            this(context.callback, context.timeout, messageSeq, context.request, context.retryCount);
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void run() {
            queue.remove(messageSeq);
            timeout();
        }

        public void timeout() {
            call(null);
        }

        public void success(Packet packet) {
            call(packet);
        }

        private void call(Packet response) {
            if (this.future.cancel(true)) {
                boolean success = response != null;
                this.set(success);
                if (callback != null) {
                    if (success) {
                        logger.d("receive one ack response, messageSeq=%s, costTime=%d, request=%s, response=%s"
                                , messageSeq, (System.currentTimeMillis() - sendTime), request, response
                        );
                        callback.onSuccess(response);
                    } else if (request != null && retryCount > 0) {
                        logger.w("one ack request timeout, retry=%d, messageSeq=%s, costTime=%d, request=%s"
                                , retryCount, messageSeq, (System.currentTimeMillis() - sendTime), request
                        );
                        addTask(copy(retryCount - 1));
                        connection.send(request);
                    } else {
                        logger.w("one ack request timeout, messageSeq=%s, costTime=%d, request=%s"
                                , messageSeq, (System.currentTimeMillis() - sendTime), request
                        );
                        callback.onTimeout(request);
                    }
                }
                callback = null;
                request = null;
            }
        }

        private RequestTask copy(int retryCount) {
            return new RequestTask(callback, timeout, messageSeq, request, retryCount);
        }
    }

}
