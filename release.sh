#!/bin/bash

export GPG_TTY=$(tty)
mvn release:prepare release:perform
