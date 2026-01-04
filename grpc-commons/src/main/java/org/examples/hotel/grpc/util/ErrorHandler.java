package org.examples.hotel.grpc.util;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.examples.hotel.grpc.common.ServiceError;

/**
 * Utilitaire pour convertir les exceptions en erreurs gRPC
 */
public class ErrorHandler {

    /**
     * Convertit une exception en StatusRuntimeException gRPC
     */
    public static StatusRuntimeException toGrpcException(Exception e) {
        if (e instanceof StatusRuntimeException) {
            return (StatusRuntimeException) e;
        }

        if (e instanceof IllegalArgumentException) {
            return Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .withCause(e)
                    .asRuntimeException();
        }

        if (e.getMessage() != null && e.getMessage().contains("not found")) {
            return Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .withCause(e)
                    .asRuntimeException();
        }

        return Status.INTERNAL
                .withDescription(e.getMessage())
                .withCause(e)
                .asRuntimeException();
    }

    /**
     * Crée un ServiceError depuis une exception
     */
    public static ServiceError toServiceError(Exception e) {
        ServiceError.ErrorCode code;

        if (e instanceof IllegalArgumentException) {
            code = ServiceError.ErrorCode.INVALID_ARGUMENT;
        } else if (e.getMessage() != null && e.getMessage().contains("not found")) {
            code = ServiceError.ErrorCode.NOT_FOUND;
        } else {
            code = ServiceError.ErrorCode.INTERNAL;
        }

        return ServiceError.newBuilder()
                .setCode(code)
                .setMessage(e.getMessage() != null ? e.getMessage() : "Unknown error")
                .setDetails(e.getClass().getName())
                .build();
    }

    /**
     * Crée une exception depuis un ServiceError
     */
    public static StatusRuntimeException fromServiceError(ServiceError error) {
        Status status;

        switch (error.getCode()) {
            case NOT_FOUND:
                status = Status.NOT_FOUND;
                break;
            case INVALID_ARGUMENT:
                status = Status.INVALID_ARGUMENT;
                break;
            case ALREADY_EXISTS:
                status = Status.ALREADY_EXISTS;
                break;
            case PERMISSION_DENIED:
                status = Status.PERMISSION_DENIED;
                break;
            case UNAVAILABLE:
                status = Status.UNAVAILABLE;
                break;
            case DEADLINE_EXCEEDED:
                status = Status.DEADLINE_EXCEEDED;
                break;
            default:
                status = Status.INTERNAL;
        }

        return status
                .withDescription(error.getMessage())
                .asRuntimeException();
    }
}

