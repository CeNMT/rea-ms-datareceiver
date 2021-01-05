/*
 * Copyright 2019 Google LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package main

import (
	"bytes"
	"io/ioutil"
	"log"
	"os"
	"regexp"
	"testing"

	"github.com/google/go-cmp/cmp"
)

func TestApplyConfigs(t *testing.T) {
	cmdFilePath := "cmd/apply/testdata/commands.txt"
	excludeNonCommandLinesRe := regexp.MustCompile("(?m)^(.{0,2}|([^D]|D[^r]|Dr[^y]).*)\n")
	replaceTmpDirNameRe := regexp.MustCompile("(?m)/tmp/.*$")

	var b bytes.Buffer
	log.SetOutput(&b)
	log.SetFlags(0) // Remove timestamps.

	*configPath = "samples/full/team1/config.yaml"
	projects = arrayFlags{"example-forseti"}
	*dryRun = true
	*importExisting = true
	*terraformApplyFlags = "-auto-approve"

	dir, err := ioutil.TempDir("", "")
	if err != nil {
		t.Fatalf("ioutil.TempDir = %v", err)
	}
	defer os.RemoveAll(dir)
	*terraformConfigsDir = dir

	if err := applyConfigs(); err != nil {
		t.Fatalf("applyConfigs = %v", err)
	}

	got := b.Bytes()
	// Remove lines that are not started with "Dry".
	got = excludeNonCommandLinesRe.ReplaceAll(got, []byte{})
	// Remove machine dependent and non-deterministic info.
	got = replaceTmpDirNameRe.ReplaceAll(got, []byte("/tmp/xxxxxxxxx"))
	// Remove "Dry run call: ".
	got = bytes.ReplaceAll(got, []byte("Dry run call: "), []byte{})

	want, err := ioutil.ReadFile(cmdFilePath)
	if err != nil {
		t.Fatalf("ioutil.ReadAll = %v", err)
	}

	if diff := cmp.Diff(string(got), string(want)); diff != "" {
		t.Fatalf("logged commands differ (-got +want):\n%v\nIf you are sure the command changes are desired, copy/paste the following content (without indent) to %q:\n%s", diff, cmdFilePath, string(got))
	}
}
