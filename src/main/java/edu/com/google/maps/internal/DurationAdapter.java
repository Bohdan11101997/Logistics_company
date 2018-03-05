/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package edu.com.google.maps.internal;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import edu.com.google.maps.model.Distance;
import edu.com.google.maps.model.Duration;
import java.io.IOException;

/**
 * This class handles conversion from JSON to {@link Distance}.
 *
 * <p>Please see <a
 * href="https://google-gson.googlecode.com/svn/trunk/gson/docs/javadocs/com/google/gson/TypeAdapter.html">GSON
 * Type Adapter</a> for more detail.
 */
public class DurationAdapter extends TypeAdapter<Duration> {

  /**
   * Read a distance object from a Directions API result and convert it to a {@link Distance}.
   *
   * <p>We are expecting to receive something akin to the following:
   *
   * <pre>
   * {
   *   "value": 207,
   *   "text": "0.1 mi"
   * }
   * </pre>
   */
  @Override
  public Duration read(JsonReader reader) throws IOException {
    if (reader.peek() == JsonToken.NULL) {
      reader.nextNull();
      return null;
    }

    Duration duration = new Duration();

    reader.beginObject();
    while (reader.hasNext()) {
      String name = reader.nextName();
      if (name.equals("text")) {
        duration.humanReadable = reader.nextString();
      } else if (name.equals("value")) {
        duration.inSeconds = reader.nextLong();
      }
    }
    reader.endObject();

    return duration;
  }

  /** This method is not implemented. */
  @Override
  public void write(JsonWriter writer, Duration value) throws IOException {
    throw new UnsupportedOperationException("Unimplemented method");
  }
}
