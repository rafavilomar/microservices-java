# starting general services
docker-compose up --build -d

# starting Eureka Server
cd eureka-server
mvn clean install
docker-compose up --build -d

# starting API Gateway
cd ../api-gateway
mvn clean install
docker-compose up --build -d

# starting Customer
cd ../customer
mvn clean install
docker-compose up --build -d

# starting Email Notification
cd ../email-notification
mvn clean install
docker-compose up --build -d

# starting Loyalty
cd ../loyalty
mvn clean install
docker-compose up --build -d

# starting Product
cd ../product
mvn clean install
docker-compose up --build -d

# starting Security
cd ../security
mvn clean install
docker-compose up --build -d

# starting Shopping
cd ../shopping
mvn clean install
docker-compose up --build -d

echo "ALL MICROSERVICES STARTED :)"