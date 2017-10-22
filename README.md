# CSCI 201 Final Project Server

## Introduction
This readme is not complete yet. It will be updated later.

The project cannot be executed yet because of bugs. I will provide update later. All classes in `databaseAccess` package are tested though.

## Class Structure

### Package `model`
This is the model package with three POJO classes.

### Package `service`
This is the first layer (service layer) of the server. `ApplicaitonConfig` class helps initialize all instances (services) at server startup. `PostUpdateService` is a WebSocket built to send post updates. All other classes are REST API's that will respond to HTTP requests. Read more about REST here [here](https://spring.io/understanding/REST).

### Package `databaseAccess`
The is the second layer of the server. There are 2 interfaces (read API and write API) that take care of communications with the database. The MongoDB read and write classes implement the 2 interfaces.

### Package `test`
This package includes test files for classes in databaseAccess package.
