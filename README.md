# Voucher handling microservice

## Introduction
This microservice provides 2 main functionality groups:
1. Voucher management (CRUD)
2. Voucher redemption

## Getting started
1. Install Java 17+
2. Start Spring application from your favorite IDE or run the following command in a console at the project root dir:
```shell
gradlew :bootRun
```
3. Open `http://localhost:8080/swagger-ui/index.html` in browser for API doc UI.

## Run tests

Use the following command to run tests:
```shell
gradlew :check
```

## API documentation
See section "Getting  started" for Swagger UI.

## Supported use cases
### Optional redemption limitation
#### Single use:
Create a new voucher with `redemptionCount: 1`.

#### X times use:
In case of a predefined limit create a new voucher with `redemptionCount: <expected limit>`.

#### Multiple (undefined / "infinite") use:
Create a new voucher with `redemptionCount: null`.

### Ability to redeem only before a certain point in time
Use optional property `expiry` during voucher creation to set any future date-time.