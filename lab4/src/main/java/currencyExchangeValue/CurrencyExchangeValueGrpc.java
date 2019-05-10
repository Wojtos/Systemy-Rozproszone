package currencyExchangeValue;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.20.0)",
    comments = "Source: CurrencyExchangeValue.proto")
public final class CurrencyExchangeValueGrpc {

  private CurrencyExchangeValueGrpc() {}

  public static final String SERVICE_NAME = "currencyExchangeValue.CurrencyExchangeValue";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<currencyExchangeValue.CurrencyExchangeValueOuterClass.Application,
      currencyExchangeValue.CurrencyExchangeValueOuterClass.Response> getRegisterMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Register",
      requestType = currencyExchangeValue.CurrencyExchangeValueOuterClass.Application.class,
      responseType = currencyExchangeValue.CurrencyExchangeValueOuterClass.Response.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<currencyExchangeValue.CurrencyExchangeValueOuterClass.Application,
      currencyExchangeValue.CurrencyExchangeValueOuterClass.Response> getRegisterMethod() {
    io.grpc.MethodDescriptor<currencyExchangeValue.CurrencyExchangeValueOuterClass.Application, currencyExchangeValue.CurrencyExchangeValueOuterClass.Response> getRegisterMethod;
    if ((getRegisterMethod = CurrencyExchangeValueGrpc.getRegisterMethod) == null) {
      synchronized (CurrencyExchangeValueGrpc.class) {
        if ((getRegisterMethod = CurrencyExchangeValueGrpc.getRegisterMethod) == null) {
          CurrencyExchangeValueGrpc.getRegisterMethod = getRegisterMethod = 
              io.grpc.MethodDescriptor.<currencyExchangeValue.CurrencyExchangeValueOuterClass.Application, currencyExchangeValue.CurrencyExchangeValueOuterClass.Response>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "currencyExchangeValue.CurrencyExchangeValue", "Register"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  currencyExchangeValue.CurrencyExchangeValueOuterClass.Application.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  currencyExchangeValue.CurrencyExchangeValueOuterClass.Response.getDefaultInstance()))
                  .setSchemaDescriptor(new CurrencyExchangeValueMethodDescriptorSupplier("Register"))
                  .build();
          }
        }
     }
     return getRegisterMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CurrencyExchangeValueStub newStub(io.grpc.Channel channel) {
    return new CurrencyExchangeValueStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CurrencyExchangeValueBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new CurrencyExchangeValueBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CurrencyExchangeValueFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new CurrencyExchangeValueFutureStub(channel);
  }

  /**
   */
  public static abstract class CurrencyExchangeValueImplBase implements io.grpc.BindableService {

    /**
     */
    public void register(currencyExchangeValue.CurrencyExchangeValueOuterClass.Application request,
        io.grpc.stub.StreamObserver<currencyExchangeValue.CurrencyExchangeValueOuterClass.Response> responseObserver) {
      asyncUnimplementedUnaryCall(getRegisterMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRegisterMethod(),
            asyncServerStreamingCall(
              new MethodHandlers<
                currencyExchangeValue.CurrencyExchangeValueOuterClass.Application,
                currencyExchangeValue.CurrencyExchangeValueOuterClass.Response>(
                  this, METHODID_REGISTER)))
          .build();
    }
  }

  /**
   */
  public static final class CurrencyExchangeValueStub extends io.grpc.stub.AbstractStub<CurrencyExchangeValueStub> {
    private CurrencyExchangeValueStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CurrencyExchangeValueStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CurrencyExchangeValueStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CurrencyExchangeValueStub(channel, callOptions);
    }

    /**
     */
    public void register(currencyExchangeValue.CurrencyExchangeValueOuterClass.Application request,
        io.grpc.stub.StreamObserver<currencyExchangeValue.CurrencyExchangeValueOuterClass.Response> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getRegisterMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class CurrencyExchangeValueBlockingStub extends io.grpc.stub.AbstractStub<CurrencyExchangeValueBlockingStub> {
    private CurrencyExchangeValueBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CurrencyExchangeValueBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CurrencyExchangeValueBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CurrencyExchangeValueBlockingStub(channel, callOptions);
    }

    /**
     */
    public java.util.Iterator<currencyExchangeValue.CurrencyExchangeValueOuterClass.Response> register(
        currencyExchangeValue.CurrencyExchangeValueOuterClass.Application request) {
      return blockingServerStreamingCall(
          getChannel(), getRegisterMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class CurrencyExchangeValueFutureStub extends io.grpc.stub.AbstractStub<CurrencyExchangeValueFutureStub> {
    private CurrencyExchangeValueFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CurrencyExchangeValueFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CurrencyExchangeValueFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CurrencyExchangeValueFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_REGISTER = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final CurrencyExchangeValueImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(CurrencyExchangeValueImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REGISTER:
          serviceImpl.register((currencyExchangeValue.CurrencyExchangeValueOuterClass.Application) request,
              (io.grpc.stub.StreamObserver<currencyExchangeValue.CurrencyExchangeValueOuterClass.Response>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class CurrencyExchangeValueBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CurrencyExchangeValueBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return currencyExchangeValue.CurrencyExchangeValueOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("CurrencyExchangeValue");
    }
  }

  private static final class CurrencyExchangeValueFileDescriptorSupplier
      extends CurrencyExchangeValueBaseDescriptorSupplier {
    CurrencyExchangeValueFileDescriptorSupplier() {}
  }

  private static final class CurrencyExchangeValueMethodDescriptorSupplier
      extends CurrencyExchangeValueBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    CurrencyExchangeValueMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (CurrencyExchangeValueGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CurrencyExchangeValueFileDescriptorSupplier())
              .addMethod(getRegisterMethod())
              .build();
        }
      }
    }
    return result;
  }
}
