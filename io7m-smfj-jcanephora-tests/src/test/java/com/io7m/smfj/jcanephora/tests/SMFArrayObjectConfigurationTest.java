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

package com.io7m.smfj.jcanephora.tests;

import com.io7m.jcanephora.core.JCGLScalarType;
import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.smfj.core.SMFAttributeName;
import com.io7m.smfj.jcanephora.core.SMFArrayAttributeMapping;
import com.io7m.smfj.jcanephora.core.SMFArrayObjectConfiguration;
import javaslang.Tuple;
import javaslang.collection.HashMap;
import javaslang.collection.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Optional;

public final class SMFArrayObjectConfigurationTest
{
  @Rule public ExpectedException expected = ExpectedException.none();

  @Test
  public void testMisfiled()
  {
    final SMFArrayAttributeMapping m0 = SMFArrayAttributeMapping.of(
      SMFAttributeName.of("a"), 0, Optional.of(JCGLScalarType.TYPE_FLOAT), 4);

    this.expected.expect(IllegalArgumentException.class);
    SMFArrayObjectConfiguration.builder()
      .setMappings(HashMap.of(SMFAttributeName.of("b"), m0))
      .build();
  }

  @Test
  public void testDuplicate()
  {
    final SMFArrayAttributeMapping m0 = SMFArrayAttributeMapping.of(
      SMFAttributeName.of("a"), 0, Optional.of(JCGLScalarType.TYPE_FLOAT), 4);
    final SMFArrayAttributeMapping m1 = SMFArrayAttributeMapping.of(
      SMFAttributeName.of("b"), 0, Optional.of(JCGLScalarType.TYPE_FLOAT), 4);

    this.expected.expect(IllegalArgumentException.class);
    SMFArrayObjectConfiguration.builder()
      .setMappings(HashMap.ofEntries(List.of(
        Tuple.of(SMFAttributeName.of("a"), m0),
        Tuple.of(SMFAttributeName.of("b"), m1))))
      .build();
  }

  @Test
  public void testMappings()
  {
    final SMFArrayAttributeMapping m0 = SMFArrayAttributeMapping.of(
      SMFAttributeName.of("a"), 0, Optional.of(JCGLScalarType.TYPE_FLOAT), 4);
    final SMFArrayAttributeMapping m1 = SMFArrayAttributeMapping.of(
      SMFAttributeName.of("b"), 1, Optional.of(JCGLScalarType.TYPE_FLOAT), 4);

    final SMFArrayObjectConfiguration m =
      SMFArrayObjectConfiguration.builder()
        .setMappings(HashMap.ofEntries(List.of(
          Tuple.of(SMFAttributeName.of("a"), m0),
          Tuple.of(SMFAttributeName.of("b"), m1))))
        .setIndexBufferUsage(JCGLUsageHint.USAGE_DYNAMIC_COPY)
        .setArrayBufferUsage(JCGLUsageHint.USAGE_STREAM_READ)
        .build();

    Assert.assertEquals(JCGLUsageHint.USAGE_DYNAMIC_COPY, m.indexBufferUsage());
    Assert.assertEquals(JCGLUsageHint.USAGE_STREAM_READ, m.arrayBufferUsage());
    Assert.assertEquals(m0, m.mappings().get(SMFAttributeName.of("a")).get());
    Assert.assertEquals(m1, m.mappings().get(SMFAttributeName.of("b")).get());
    Assert.assertEquals(2L, (long) m.mappings().size());
  }
}
