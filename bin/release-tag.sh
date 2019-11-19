#!/bin/bash

TAG=v0.5.0

echo -e "Delete existing $TAG tag first."
git tag -d $TAG
git push origin :refs/tags/$TAG

echo -e "Create new version of $TAG tag."
git tag $TAG
git push origin $TAG
