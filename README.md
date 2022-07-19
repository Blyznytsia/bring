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


## Contribution/Development

### Git process

The process of development and creation of the pull request consists of these steps:

- create a branch whose name consists of the id and name of the issue. Example: Issue name is "Implement new service#23",
  the branch will be named `feature/23-implement-new-service`
- fetch and checkout on this branch locally
- Implement/fix your feature, comment on your code
- Add javadoc for new class/method/field
- Follow the code style of the project, including indentation.
- Write and run tests.
- Before push changes build a project.
- Add or change the documentation as needed.
- Squash your commits into a single commit with git's interactive rebase. Create a new branch if necessary.
- Push your local changes to a remote Github repository
- Create a pull request into the **main** branch
- Add reviewers and ask them to review your code
- Implement reviewers' suggestions and pieces of advice. Push your changes again. If needed, add additional comments
  to describe more precisely your implementation.
- Once the pull request is approved and merged you can pull the changes from upstream to your local repo and delete your extra branch(es) remote and locally.

And last but not least: Always write your commit messages in the present tense. Your commit message should describe what the commit when applied, does to the code – not what you did to the code.

Useful links:
- https://www.atlassian.com/git/tutorials/using-branches
- https://www.atlassian.com/git/tutorials/comparing-workflows/feature-branch-workflow

### Git branch naming

The additional part about branch naming

We have different categories of issues therefore we have such prefixes accordingly: feature, bug, hotfix, release.

1. **Feature** is created for the implementation of the new functionality of the project.
2. **Bug** is created for fixing a bug.
3. **Hotfix** is created for fixing some hot, urgent bug in the production.
4. **Release** is created for the release branch.

Based on the purpose of the branch use this naming pattern `feature/id-name-of-issue` or `feature-id-name-of-issue`.
