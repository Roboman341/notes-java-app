#!/usr/bin/env bash
set -eo pipefail

GIT_ROOT_PATH=$(git rev-parse --show-toplevel)
EXCLUDED_FOLDER_FROM_SEARCH="*/test/*"

find "$GIT_ROOT_PATH/" -name "*.java" ! -path "$EXCLUDED_FOLDER_FROM_SEARCH" -exec cat "{}" \; | \
# -vE inverts the match (removes lines matching the pattern).
  grep -vE '^import (org|java)\.' | \
  grep -vE '^\s*$' > java-code.txt

find "$GIT_ROOT_PATH/" -name "*.html" ! -path "$EXCLUDED_FOLDER_FROM_SEARCH" -exec cat "{}" \; >> java-code.txt