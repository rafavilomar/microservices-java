# Register new customer
Author: rafavilomar  
Status: `In review` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-01-29

## Contents
- Links
- Objective
- Goals
- Overview
- Solution
  - Roles and permissions structure
  - Allow all requests for Spring Security
  - Save user
  - Call customer registration
  - Send email
- Considerations

## Links
- [Example implementation with gRPC and SpringBoot #1](https://medium.com/@ankithahjpgowda/grpc-implementation-in-springboot-and-microservices-366dc7a66c5a)
- [Example implementation with gRPC and SpringBoot #2](https://www.linkedin.com/pulse/building-microservices-spring-boot-andgrpc-jonathan-manera/)

## Objective
Lately, I've been using Eureka to handle microservices communication and centralizing security stuff in just one microservice.

The principal goal is to distribuite permissions validations across all microservices and use gRPC to handle a faster 
instant communication between microservices, and also use Kafka for asynchronously event driven communication with email services.

## Goals
- Allow request with Spring Security.
- Implement gRPC to communicate Security with Customer.
- Implement Kafka to communicate Security with Email Notification.

## Overview
![Customer register flow](..%2Fimages%2Fcustomer_register_flow.png)

## Solution

### Roles and permissions structure
Roles consist of a list of permissions that define user's access to the system configured in controllers. In this way 
roles are totally flexible to permissions changes.   
Each user can only have one role assigned.   
![Role Structure](..%2Fimages%2Frole_structure.png)

### Allow all requests for Spring Security
Spring Security block all access by default. To avoid that I need to use this configuration to permit request for all services.
This is a temporal solution, I'll implement filters and permissions validation soon.    

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {
                    CorsConfigurationSource source = request -> {
                        CorsConfiguration configuration = new CorsConfiguration();
                        configuration.setAllowedOrigins(List.of(CorsConfiguration.ALL));
                        configuration.setAllowedMethods(List.of(CorsConfiguration.ALL));
                        configuration.setAllowedHeaders(List.of(CorsConfiguration.ALL));
                        return configuration;
                    };
                    cors.configurationSource(source);
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }
}
```

### Save user
There is only one entry point to create a new customer validating duplicated email and customer role existence.    
I'm using `BCryptPasswordEncoder` to encode and secure password before save it.
```java
@Override
public void registerCustomer(RegisterCustomerRequest newUser) {
  log.info("Register a new customer {}", newUser.email());
  if (userRepository.existsByEmail(newUser.email()))
    throw new BadRequestException("Seems this email is already registered. Try login!");

  try {
    Role customerRole = roleService.findByName("Customer");
    User user = User.builder()
            .email(newUser.email())
            .password(passwordEncoder.encode(newUser.password()))
            .role(customerRole)
            .build();
    userRepository.save(user);
  } catch (Exception exception) {
    log.error("Error creating customer: {}", exception.getMessage());
    throw new InternalErrorException("Can't register in this moment. Please try later");
  }
}
```

### Call customer registration
I could use REST with Eureka and Feign Clients to handle microservices communications.
But I choose gRPC to have a higher performance, avoid controllers and endpoints for internal communication, 
and also handle permissions validation just for REST services.

To make this possible is necessary to create the gRPC server **(Customer)** and the client **(Security)**, just like this:   
![gRPC communication](..%2Fimages%2Fgrpc_communication.png)

#### Common module
gRPC uses proto files to define services, request and response structures. The server and clients must have access to those 
files, so it was necessary to create a new module to share common files like this:     
- **Common module**   
![Common module](..%2Fimages%2Fcommon_module.png)     
- **Proto file**
```protobuf
syntax = "proto3";

import "google/protobuf/empty.proto";

package common.grpc.common;

option java_multiple_files = true;

service CustomerService {

  rpc register(CustomerRegistrationRequest) returns (google.protobuf.Empty);

}

message CustomerRegistrationRequest {
  string firstName = 1;
  string lastName = 2;
  string email = 3;
  string country = 4;
  string address = 5;
  uint64 idUser = 6;
}
```  

Therefore, Customer and Security need to include common module in their dependencies.

#### Server configuration
Server side, with gRPC, only needs to define the service and port for gRPC. gRPC services can be defined like this:    
```java
@GrpcService
public class CustomerServiceGrpc extends common.grpc.common.CustomerServiceGrpc.CustomerServiceImplBase {

    private final ICustomerService customerService;

    @Override
    public void register(CustomerRegistrationRequest request, StreamObserver<Empty> responseObserver) {
        customerService.register(com.microservice_level_up.module.customer.dto.CustomerRegistrationRequest.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .address(request.getAddress())
                .country(request.getCountry())
                .idUser(request.getIdUser())
                .build());

        responseObserver.onNext(null);
        responseObserver.onCompleted();
    }
}
```

#### Client configuration
Also, in Security, it's necessary to add the client configuration. As a Bean, this client can be injected in any part of the system.
```java
@Configuration
public class GrpcClientConfiguration {

    @Bean
    CustomerServiceGrpc.CustomerServiceBlockingStub customerServiceBlockingStub() {
        Channel channel = ManagedChannelBuilder.forAddress("localhost", 8081)
                .usePlaintext()
                .build();

        return CustomerServiceGrpc.newBlockingStub(channel);
    }
}
```

### Send email
Kafka seems a perfect option to handle all email notifications with users. Email Notification module will be called just 
for specific scenarios and is not necessary to an instant execution, therefore all of this notifications can be sent 
synchronously.

![Kafka communication](..%2Fimages%2Fkafka_communication.png)

This feature use a topic called `customer_created` to handle their kafka events:

#### Security

- **Producer config**
```java
@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {
    @Value("${kafka.bootstrap-address}")
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<String, Event<?>> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Event<?>> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic topic() {
        return new NewTopic("customer_created", 1, (short) 1);
    }
}
```

- **Publish service**
```java
@Slf4j
@Service
public record UserService(
        // other injections...
        KafkaTemplate<String, Event<?>> producer) implements IUserService {
    
    // Other functions...
    
    private void publishCustomer(RegisterCustomerRequest newUser) {
        Event<CustomerCreatedNotification> event = new Event<>(
            UUID.randomUUID().toString(),
            LocalDateTime.now(),
            EventType.CREATED,
            new CustomerCreatedNotification(newUser.firstName(), newUser.lastName(), newUser.email())
        );
        String topicCustomer = "customer_created";
        producer.send(topicCustomer, event);
    }
}
```

#### Email Notification

- **Consumer config**
```java
@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConsumerConfig {
    @Value("${kafka.bootstrap-address}")
    private String bootstrapAddress;

    @Bean
    public ConsumerFactory<String, Event<?>> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);

        final JsonDeserializer<Event<?>> jsonDeserializer = new JsonDeserializer<>();
        jsonDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(jsonDeserializer));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Event<?>> kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, Event<?>> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
```

- **Service listening**
```java
@Slf4j
@Service
public record EmailNotificationService(JavaMailSender mailSender) {

    @KafkaListener(
            topics = "customer_created",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "grupo1"
    )
    public void sendEmail(Event<?> event) {
        ObjectMapper objectMapper = new ObjectMapper();
        CustomerCreatedNotification customer = objectMapper.convertValue(event.data(), CustomerCreatedNotification.class);
        log.info("Send email {}", customer);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(customer.email());
        message.setSubject("Registration");
        message.setText("Welcome " + customer.firstName() + " " + customer.lastName());

        mailSender.send(message);
    }
}
```

## Considerations
It was necessary to recreate Customer and Security modules because of compilation and integration errors. They weren't 
recreated at all essentially, but just inherent core configuration and dependencies from principal pom.xml file, and also 
change groupId and artifactId for module.

On the other hand, at this point Spring Security dependencies are added but not really implemented. Security is the only 
module with those dependencies, and is not really protected because there is no permission validations.