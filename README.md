[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Blyznytsia_bring&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Blyznytsia_bring)

## Table of Contents

- [Description](#description)
    - [Features](#features)
    - [Configuration](#configuration)
    - [Context](#context)

## Description

### Features

**Bring Framework** is an inversion of control and dependency injection framework. It allows you to declare what objects
you need, and then it processes configuration, creates required objects, sets dependencies and brings objects that are
ready to use.

### Configuration

A configuration provides the instructions for the container. It tells which objects should be created, and what values
should be injected into the fields. So Bring Framework should provide some tools for the configuration. (It could be XML
files, JSON files, property text files, Java, annotations, etc.)

### Context

When everything is configured and created, it should be stored somewhere. In other words, all the objects that were
created by the container should be stored in some context (register of objects). 