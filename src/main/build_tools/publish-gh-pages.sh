#!/usr/bin/env bash
set -eu

rm -fr target/site-staging/.git
cd target/site-staging
git init
git remote add origin https://github.com/dakusui/pcond.git
git push origin :gh-pages || :
git checkout -b gh-pages
git add --all
git commit -m "Update Documentation"
git push origin gh-pages