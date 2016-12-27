/*
 * Copyright © 2016 <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.smfj.jcanephora.core;

import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.smfj.core.SMFAttributeName;
import com.io7m.smfj.core.SMFImmutableStyleType;
import javaslang.collection.Map;
import org.immutables.value.Value;

import java.util.HashMap;
import java.util.Objects;

/**
 * A mapping from a set of SMF attributes to a set of <tt>jcanephora</tt>
 * array attributes.
 */

@SMFImmutableStyleType
@Value.Immutable
public interface SMFArrayObjectConfigurationType
{
  /**
   * @return The mappings
   */

  @Value.Parameter
  Map<SMFAttributeName, SMFArrayAttributeMapping> mappings();

  /**
   * @return The usage hint for the array buffer
   */

  @Value.Parameter
  @Value.Default
  default JCGLUsageHint arrayBufferUsage()
  {
    return JCGLUsageHint.USAGE_STATIC_DRAW;
  }

  /**
   * @return The usage hint for the index buffer
   */

  @Value.Parameter
  @Value.Default
  default JCGLUsageHint indexBufferUsage()
  {
    return JCGLUsageHint.USAGE_STATIC_DRAW;
  }

  /**
   * Check preconditions for the type.
   */

  @Value.Check
  default void checkPreconditions()
  {
    final Map<SMFAttributeName, SMFArrayAttributeMapping> m = this.mappings();
    final HashMap<Integer, SMFArrayAttributeMapping> by_index =
      new HashMap<>(m.size());

    for (final SMFAttributeName name : m.keySet()) {
      final SMFArrayAttributeMapping mapping = m.get(name).get();

      if (!Objects.equals(mapping.name(), name)) {
        throw new IllegalArgumentException(
          "Array attribute name " + name + " must match that of the map value");
      }

      final Integer b_index = Integer.valueOf(mapping.index());
      if (by_index.containsKey(b_index)) {
        final StringBuilder sb = new StringBuilder(128);
        sb.append("Duplicate attribute index.");
        sb.append(System.lineSeparator());
        sb.append("  Attribute [");
        sb.append(mapping.index());
        sb.append("]: ");
        sb.append(name);
        sb.append(System.lineSeparator());
        sb.append("  Attribute [");
        sb.append(mapping.index());
        sb.append("]: ");
        sb.append(by_index.get(b_index).name());
        sb.append(System.lineSeparator());
        throw new IllegalArgumentException(sb.toString());
      }

      by_index.put(b_index, mapping);
    }
  }
}
