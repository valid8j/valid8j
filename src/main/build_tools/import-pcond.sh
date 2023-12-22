#!/usr/bin/env bash
set -E -o nounset -o errexit +o posix -o pipefail
shopt -s inherit_errexit

# shellcheck disable=SC1090
source "$(dirname "${0}")/lib/mvn-utils.rc"

function mangle_package() {
  local _basedir="${1}"
  mv "${_basedir}/com/github/dakusui/pcond" "${_basedir}/com/github/dakusui/$(project_name)_pcond"
  find "${_basedir}" -type f -name '*.java' \
    -exec sed -i 's/com\.github\.dakusui\.pcond/com.github.dakusui.'"$(project_name)"'_pcond/g' {} \;
}

function pcond_version() {
  echo "4.0.0-beta-11"
}

function main() {
  local _out=./target/generated-sources/local
  local _pcond_version
  _pcond_version="$(pcond_version)"
  if [[ ! -e "${_out}/.done" ]]; then
    mkdir -p "${_out}"
    mvn-unpack "com.github.dakusui:pcond:${_pcond_version}:jar:sources" "${_out}"
    mangle_package "${_out}"
    touch "${_out}/.done"
  fi
}

main
