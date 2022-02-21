// Copyright 2022 Abex. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.archivepatcher.shared;

import java.util.zip.CRC32;
import java.util.zip.Deflater;

// https://github.com/madler/zlib/issues/275
// https://bugs.openjdk.java.net/browse/JDK-8184306
public class ZLib275 {
  public static final boolean isBuggy;

  static {
    byte[] corpus = DefaultDeflateCompatibilityWindow.getCorpus();
    long baseline = compress(new Deflater(4), corpus);
    Deflater test = new Deflater(3);
    compress(test, corpus);
    test.setLevel(4);
    isBuggy = baseline != compress(test, corpus);
  }

  private static long compress(Deflater d, byte[] data) {
    byte[] out = new byte[1024];
    d.setInput(data);
    d.finish();
    CRC32 crc = new CRC32();
    while (!d.finished()) {
      int l = d.deflate(out);
      crc.update(out, 0, l);
    }
    return crc.getValue();
  }
}
