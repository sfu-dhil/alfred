#!/bin/sh

IDX=index
DIR=src/main/resources/stopwords

ls $DIR | grep -v $IDX > $DIR/$IDX
