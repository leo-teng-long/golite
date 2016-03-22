#!/bin/bash

valid_prog_cnt=$(find valid/ -regex ".*/[^/]*.go" | wc -l | tr -d '[[:space:]]')
invalid_prog_cnt=$(find invalid/ -regex ".*/[^/]*.go" | wc -l | tr -d '[[:space:]]')
total_cnt=$((valid_prog_cnt + invalid_prog_cnt))

echo "Valid: $valid_prog_cnt"
echo "Invalid: $invalid_prog_cnt"
echo "Total: $total_cnt"

