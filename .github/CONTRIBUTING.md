How to Contribute
-----------------

Thank you very much for considering contributing to Quivr4s! It is people like you that make open-source projects
happen.

Before you begin however, please be sure to read
our [Contributor Code of Conduct](https://github.com/Topl/quivr4s/blob/main/.github/CODE_OF_CONDUCT.md). We want to
ensure a healthy and collaborative environment here and will be enforcing these rules.

**STOP RIGHT NOW AND READ THE CONTRIBUTOR LICENSE AGREEMENT AND CODE OF CONDUCT**

<a name="communication"></a>

## Communication channels

Before you get lost in the repository, here are a few starting points
for you to check out. You might find that others have had similar
questions or that your question rather belongs in one place than another.

* Chat: https://discord.gg/WVPGaG5Cqc
* Website: https://www.topl.co/
* Twitter: https://twitter.com/topl_protocol

Getting Started
---------------
If you have played around with the client and found some issues that crashes the client, be sure to submit
an [Issue](https://github.com/Topl/quivr4s/issues)

* Make sure you have a GitHub account
* Submit a ticket for your issue, assuming one does not already exist.
    * Clearly describe the issue including steps to reproduce when it is a bug.
    * Make sure you fill in the earliest version that you know has the issue.


Making Changes
--------------

1. Fork the repository to your Github account.

* Make any new branches from the `dev` branch, unless specifically targeting another feature branch or applying a hotfix
  to the `main` branch

2. Make commits
3. Make sure you have added the necessary tests for your changes.
4. Run all the tests to assure nothing else was accidentally broken.
5. Run `sbt clean preparePR` to check linting, formatting, and that all tests pass
6. Submit a [new pull request](https://github.com/Topl/quivr4s/pulls?q=is%3Apr+is%3Aopen+sort%3Aupdated-desc) in the
   proper format:

```
Pull Request

## Purpose
  State in plain words what the scope of the pull request and what problem it is trying to solve
## Approach
  Enumerate the steps you took to create the code/changes contained in this pull request
## Testing
  Specify all unit, integration, or end-to-end testing that is contained in this pull request
## Tickets
  List tickets if any that are connected to this pull request in the following format:
  * closes #1234
```

7. After submitting the pull request, make sure all checks pass and the branch is up to date with the target branch.