#!/bin/sh

find ./target/i18n/ -name "*.properties" | xargs wc -l

