docker-compose -f api-gateway/docker-compose.yml down
docker-compose -f customer/docker-compose.yml down
docker-compose -f email-notification/docker-compose.yml down
docker-compose -f loyalty/docker-compose.yml down
docker-compose -f product/docker-compose.yml down
docker-compose -f security/docker-compose.yml down
docker-compose -f shopping/docker-compose.yml down
docker-compose -f eureka-server/docker-compose.yml down
docker-compose down

echo "BYE :("
