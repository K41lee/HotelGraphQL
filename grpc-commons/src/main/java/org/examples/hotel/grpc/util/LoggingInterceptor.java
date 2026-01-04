package org.examples.hotel.grpc.util;

import io.grpc.*;

/**
 * Intercepteur pour logger les appels gRPC
 */
public class LoggingInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String methodName = call.getMethodDescriptor().getFullMethodName();
        long startTime = System.currentTimeMillis();

        System.out.println("[gRPC] --> " + methodName);

        ServerCall.Listener<ReqT> listener = next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void close(Status status, Metadata trailers) {
                long duration = System.currentTimeMillis() - startTime;
                System.out.println("[gRPC] <-- " + methodName + 
                                 " [" + status.getCode() + "] " + duration + "ms");
                super.close(status, trailers);
            }
        }, headers);

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(listener) {
            @Override
            public void onMessage(ReqT message) {
                System.out.println("[gRPC] Message received: " + message.getClass().getSimpleName());
                super.onMessage(message);
            }

            @Override
            public void onHalfClose() {
                super.onHalfClose();
            }

            @Override
            public void onCancel() {
                System.out.println("[gRPC] Call cancelled: " + methodName);
                super.onCancel();
            }
        };
    }
}

