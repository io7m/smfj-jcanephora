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

import com.io7m.ieee754b16.Binary16;
import com.io7m.jcanephora.core.JCGLArrayBufferType;
import com.io7m.jcanephora.core.JCGLArrayBufferUsableType;
import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.JCGLIndexBufferType;
import com.io7m.jcanephora.core.JCGLScalarType;
import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.jcanephora.core.api.JCGLArrayBuffersType;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLIndexBuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jintegers.Signed16;
import com.io7m.jintegers.Signed32;
import com.io7m.jintegers.Unsigned16;
import com.io7m.jintegers.Unsigned32;
import com.io7m.jintegers.Unsigned8;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.smfj.core.SMFAttributeName;
import com.io7m.smfj.format.text.SMFFormatText;
import com.io7m.smfj.jcanephora.core.SMFArrayAttributeMapping;
import com.io7m.smfj.jcanephora.core.SMFArrayLoaderType;
import com.io7m.smfj.jcanephora.core.SMFArrayLoaders;
import com.io7m.smfj.jcanephora.core.SMFArrayObjectConfiguration;
import com.io7m.smfj.parser.api.SMFParseError;
import com.io7m.smfj.parser.api.SMFParserEventsMetaType;
import com.io7m.smfj.parser.api.SMFParserEventsType;
import com.io7m.smfj.parser.api.SMFParserProviderType;
import com.io7m.smfj.parser.api.SMFParserSequentialType;
import javaslang.collection.HashMap;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class SMFArrayLoaderContract
{
  private static ByteBuffer copyHeap(final ByteBuffer original)
  {
    final ByteBuffer clone = ByteBuffer.allocate(original.capacity());
    clone.order(original.order());
    original.rewind();
    clone.put(original);
    original.rewind();
    clone.flip();
    return clone;
  }

  private static void checkType(
    final JCGLScalarType type,
    final int component_count,
    final JCGLArrayBufferUsableType array_buffer,
    final JCGLArrayBuffersType g_ab)
  {
    g_ab.arrayBufferBind(array_buffer);
    final ByteBuffer rb =
      copyHeap(g_ab.arrayBufferRead(
        array_buffer, x -> {
          final ByteBuffer b = ByteBuffer.allocateDirect((int) x);
          b.order(ByteOrder.nativeOrder());
          return b;
        }));

    switch (type) {
      case TYPE_BYTE: {
        checkTypeSigned8(component_count, rb);
        break;
      }

      case TYPE_HALF_FLOAT: {
        checkTypeFloat16(component_count, rb);
        break;
      }

      case TYPE_FLOAT: {
        checkTypeFloat32(component_count, rb);
        break;
      }

      case TYPE_INT: {
        checkTypeSigned32(component_count, rb);
        break;
      }

      case TYPE_SHORT: {
        checkTypeSigned16(component_count, rb);
        break;
      }

      case TYPE_UNSIGNED_BYTE: {
        checkTypeUnsigned8(component_count, rb);
        break;
      }

      case TYPE_UNSIGNED_INT: {
        checkTypeUnsigned32(component_count, rb);
        break;
      }

      case TYPE_UNSIGNED_SHORT: {
        checkTypeUnsigned16(component_count, rb);
        break;
      }
    }
  }

  private static void checkTypeUnsigned16(
    final int component_count,
    final ByteBuffer rb)
  {
    switch (component_count) {
      case 1: {
        {
          final long vx = (long) Unsigned16.unpackFromBuffer(rb, 0);
          final long vy = (long) Unsigned16.unpackFromBuffer(rb, 2);
          final long vz = (long) Unsigned16.unpackFromBuffer(rb, 4);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(32768L, vy);
          Assert.assertEquals(65535L, vz);
        }
        break;
      }
      case 2: {
        {
          final long vx = (long) Unsigned16.unpackFromBuffer(rb, 0);
          final long vy = (long) Unsigned16.unpackFromBuffer(rb, 2);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(65535L, vy);
        }

        {
          final long vx = (long) Unsigned16.unpackFromBuffer(rb, 4);
          final long vy = (long) Unsigned16.unpackFromBuffer(rb, 6);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(65535L, vy);
        }

        {
          final long vx = (long) Unsigned16.unpackFromBuffer(rb, 8);
          final long vy = (long) Unsigned16.unpackFromBuffer(rb, 10);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(65535L, vy);
        }
        break;
      }
      case 3: {
        {
          final long vx = (long) Unsigned16.unpackFromBuffer(rb, 0);
          final long vy = (long) Unsigned16.unpackFromBuffer(rb, 2);
          final long vz = (long) Unsigned16.unpackFromBuffer(rb, 4);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(32768L, vy);
          Assert.assertEquals(65535L, vz);
        }

        {
          final long vx = (long) Unsigned16.unpackFromBuffer(rb, 6);
          final long vy = (long) Unsigned16.unpackFromBuffer(rb, 8);
          final long vz = (long) Unsigned16.unpackFromBuffer(rb, 10);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(32768L, vy);
          Assert.assertEquals(65535L, vz);
        }

        {
          final long vx = (long) Unsigned16.unpackFromBuffer(rb, 12);
          final long vy = (long) Unsigned16.unpackFromBuffer(rb, 14);
          final long vz = (long) Unsigned16.unpackFromBuffer(rb, 16);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(32768L, vy);
          Assert.assertEquals(65535L, vz);
        }
        break;
      }
      case 4: {
        {
          final long vx = (long) Unsigned16.unpackFromBuffer(rb, 0);
          final long vy = (long) Unsigned16.unpackFromBuffer(rb, 2);
          final long vz = (long) Unsigned16.unpackFromBuffer(rb, 4);
          final long vw = (long) Unsigned16.unpackFromBuffer(rb, 6);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(32768L, vy);
          Assert.assertEquals(65535L, vz);
          Assert.assertEquals(1L, vw);
        }

        {
          final long vx = (long) Unsigned16.unpackFromBuffer(rb, 8);
          final long vy = (long) Unsigned16.unpackFromBuffer(rb, 10);
          final long vz = (long) Unsigned16.unpackFromBuffer(rb, 12);
          final long vw = (long) Unsigned16.unpackFromBuffer(rb, 14);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(32768L, vy);
          Assert.assertEquals(65535L, vz);
          Assert.assertEquals(1L, vw);
        }

        {
          final long vx = (long) Unsigned16.unpackFromBuffer(rb, 16);
          final long vy = (long) Unsigned16.unpackFromBuffer(rb, 18);
          final long vz = (long) Unsigned16.unpackFromBuffer(rb, 20);
          final long vw = (long) Unsigned16.unpackFromBuffer(rb, 22);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(32768L, vy);
          Assert.assertEquals(65535L, vz);
          Assert.assertEquals(1L, vw);
        }
        break;
      }
      default: {
        throw new UnreachableCodeException();
      }
    }
  }

  private static void checkTypeUnsigned32(
    final int component_count,
    final ByteBuffer rb)
  {
    switch (component_count) {
      case 1: {
        {
          final long vx = Unsigned32.unpackFromBuffer(rb, 0);
          final long vy = Unsigned32.unpackFromBuffer(rb, 4);
          final long vz = Unsigned32.unpackFromBuffer(rb, 8);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(2147483648L, vy);
          Assert.assertEquals(4294967295L, vz);
        }
        break;
      }
      case 2: {
        {
          final long vx = Unsigned32.unpackFromBuffer(rb, 0);
          final long vy = Unsigned32.unpackFromBuffer(rb, 4);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(4294967295L, vy);
        }

        {
          final long vx = Unsigned32.unpackFromBuffer(rb, 8);
          final long vy = Unsigned32.unpackFromBuffer(rb, 12);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(4294967295L, vy);
        }

        {
          final long vx = Unsigned32.unpackFromBuffer(rb, 16);
          final long vy = Unsigned32.unpackFromBuffer(rb, 20);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(4294967295L, vy);
        }
        break;
      }
      case 3: {
        {
          final long vx = Unsigned32.unpackFromBuffer(rb, 0);
          final long vy = Unsigned32.unpackFromBuffer(rb, 4);
          final long vz = Unsigned32.unpackFromBuffer(rb, 8);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(2147483648L, vy);
          Assert.assertEquals(4294967295L, vz);
        }

        {
          final long vx = Unsigned32.unpackFromBuffer(rb, 12);
          final long vy = Unsigned32.unpackFromBuffer(rb, 16);
          final long vz = Unsigned32.unpackFromBuffer(rb, 20);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(2147483648L, vy);
          Assert.assertEquals(4294967295L, vz);
        }

        {
          final long vx = Unsigned32.unpackFromBuffer(rb, 24);
          final long vy = Unsigned32.unpackFromBuffer(rb, 28);
          final long vz = Unsigned32.unpackFromBuffer(rb, 32);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(2147483648L, vy);
          Assert.assertEquals(4294967295L, vz);
        }
        break;
      }
      case 4: {
        {
          final long vx = Unsigned32.unpackFromBuffer(rb, 0);
          final long vy = Unsigned32.unpackFromBuffer(rb, 4);
          final long vz = Unsigned32.unpackFromBuffer(rb, 8);
          final long vw = Unsigned32.unpackFromBuffer(rb, 12);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(2147483648L, vy);
          Assert.assertEquals(4294967295L, vz);
          Assert.assertEquals(1L, vw);
        }

        {
          final long vx = Unsigned32.unpackFromBuffer(rb, 16);
          final long vy = Unsigned32.unpackFromBuffer(rb, 20);
          final long vz = Unsigned32.unpackFromBuffer(rb, 24);
          final long vw = Unsigned32.unpackFromBuffer(rb, 28);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(2147483648L, vy);
          Assert.assertEquals(4294967295L, vz);
          Assert.assertEquals(1L, vw);
        }

        {
          final long vx = Unsigned32.unpackFromBuffer(rb, 32);
          final long vy = Unsigned32.unpackFromBuffer(rb, 36);
          final long vz = Unsigned32.unpackFromBuffer(rb, 40);
          final long vw = Unsigned32.unpackFromBuffer(rb, 44);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(2147483648L, vy);
          Assert.assertEquals(4294967295L, vz);
          Assert.assertEquals(1L, vw);
        }
        break;
      }
      default: {
        throw new UnreachableCodeException();
      }
    }
  }

  private static void checkTypeUnsigned8(
    final int component_count,
    final ByteBuffer rb)
  {
    switch (component_count) {
      case 1: {
        {
          final long vx = (long) Unsigned8.unpackFromBuffer(rb, 0);
          final long vy = (long) Unsigned8.unpackFromBuffer(rb, 1);
          final long vz = (long) Unsigned8.unpackFromBuffer(rb, 2);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(128L, vy);
          Assert.assertEquals(255L, vz);
        }
        break;
      }
      case 2: {
        {
          final long vx = (long) Unsigned8.unpackFromBuffer(rb, 0);
          final long vy = (long) Unsigned8.unpackFromBuffer(rb, 1);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(255L, vy);
        }

        {
          final long vx = (long) Unsigned8.unpackFromBuffer(rb, 2);
          final long vy = (long) Unsigned8.unpackFromBuffer(rb, 3);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(255L, vy);
        }

        {
          final long vx = (long) Unsigned8.unpackFromBuffer(rb, 4);
          final long vy = (long) Unsigned8.unpackFromBuffer(rb, 5);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(255L, vy);
        }
        break;
      }
      case 3: {
        {
          final long vx = (long) Unsigned8.unpackFromBuffer(rb, 0);
          final long vy = (long) Unsigned8.unpackFromBuffer(rb, 1);
          final long vz = (long) Unsigned8.unpackFromBuffer(rb, 2);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(128L, vy);
          Assert.assertEquals(255L, vz);
        }

        {
          final long vx = (long) Unsigned8.unpackFromBuffer(rb, 3);
          final long vy = (long) Unsigned8.unpackFromBuffer(rb, 4);
          final long vz = (long) Unsigned8.unpackFromBuffer(rb, 5);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(128L, vy);
          Assert.assertEquals(255L, vz);
        }

        {
          final long vx = (long) Unsigned8.unpackFromBuffer(rb, 6);
          final long vy = (long) Unsigned8.unpackFromBuffer(rb, 7);
          final long vz = (long) Unsigned8.unpackFromBuffer(rb, 8);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(128L, vy);
          Assert.assertEquals(255L, vz);
        }
        break;
      }
      case 4: {
        {
          final long vx = (long) Unsigned8.unpackFromBuffer(rb, 0);
          final long vy = (long) Unsigned8.unpackFromBuffer(rb, 1);
          final long vz = (long) Unsigned8.unpackFromBuffer(rb, 2);
          final long vw = (long) Unsigned8.unpackFromBuffer(rb, 3);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(128L, vy);
          Assert.assertEquals(255L, vz);
          Assert.assertEquals(128L, vw);
        }

        {
          final long vx = (long) Unsigned8.unpackFromBuffer(rb, 4);
          final long vy = (long) Unsigned8.unpackFromBuffer(rb, 5);
          final long vz = (long) Unsigned8.unpackFromBuffer(rb, 6);
          final long vw = (long) Unsigned8.unpackFromBuffer(rb, 7);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(128L, vy);
          Assert.assertEquals(255L, vz);
          Assert.assertEquals(128L, vw);
        }

        {
          final long vx = (long) Unsigned8.unpackFromBuffer(rb, 8);
          final long vy = (long) Unsigned8.unpackFromBuffer(rb, 9);
          final long vz = (long) Unsigned8.unpackFromBuffer(rb, 10);
          final long vw = (long) Unsigned8.unpackFromBuffer(rb, 11);
          Assert.assertEquals(0L, vx);
          Assert.assertEquals(128L, vy);
          Assert.assertEquals(255L, vz);
          Assert.assertEquals(128L, vw);
        }
        break;
      }
      default: {
        throw new UnreachableCodeException();
      }
    }
  }

  private static void checkTypeSigned16(
    final int component_count,
    final ByteBuffer rb)
  {
    switch (component_count) {
      case 1: {
        {
          final long vx = (long) Signed16.unpackFromBuffer(rb, 0);
          final long vy = (long) Signed16.unpackFromBuffer(rb, 2);
          final long vz = (long) Signed16.unpackFromBuffer(rb, 4);
          Assert.assertEquals(-32767L, vx);
          Assert.assertEquals(0L, vy);
          Assert.assertEquals(32767L, vz);
        }
        break;
      }
      case 2: {
        {
          final long vx = (long) Signed16.unpackFromBuffer(rb, 0);
          final long vy = (long) Signed16.unpackFromBuffer(rb, 2);
          Assert.assertEquals(-32767L, vx);
          Assert.assertEquals(32767L, vy);
        }

        {
          final long vx = (long) Signed16.unpackFromBuffer(rb, 4);
          final long vy = (long) Signed16.unpackFromBuffer(rb, 6);
          Assert.assertEquals(-32767L, vx);
          Assert.assertEquals(32767L, vy);
        }

        {
          final long vx = (long) Signed16.unpackFromBuffer(rb, 8);
          final long vy = (long) Signed16.unpackFromBuffer(rb, 10);
          Assert.assertEquals(-32767L, vx);
          Assert.assertEquals(32767L, vy);
        }
        break;
      }
      case 3: {
        {
          final long vx = (long) Signed16.unpackFromBuffer(rb, 0);
          final long vy = (long) Signed16.unpackFromBuffer(rb, 2);
          final long vz = (long) Signed16.unpackFromBuffer(rb, 4);
          Assert.assertEquals(-32767L, vx);
          Assert.assertEquals(0L, vy);
          Assert.assertEquals(32767L, vz);
        }

        {
          final long vx = (long) Signed16.unpackFromBuffer(rb, 6);
          final long vy = (long) Signed16.unpackFromBuffer(rb, 8);
          final long vz = (long) Signed16.unpackFromBuffer(rb, 10);
          Assert.assertEquals(-32767L, vx);
          Assert.assertEquals(0L, vy);
          Assert.assertEquals(32767L, vz);
        }

        {
          final long vx = (long) Signed16.unpackFromBuffer(rb, 12);
          final long vy = (long) Signed16.unpackFromBuffer(rb, 14);
          final long vz = (long) Signed16.unpackFromBuffer(rb, 16);
          Assert.assertEquals(-32767L, vx);
          Assert.assertEquals(0L, vy);
          Assert.assertEquals(32767L, vz);
        }
        break;
      }
      case 4: {
        {
          final long vx = (long) Signed16.unpackFromBuffer(rb, 0);
          final long vy = (long) Signed16.unpackFromBuffer(rb, 2);
          final long vz = (long) Signed16.unpackFromBuffer(rb, 4);
          final long vw = (long) Signed16.unpackFromBuffer(rb, 6);
          Assert.assertEquals(-32767L, vx);
          Assert.assertEquals(0L, vy);
          Assert.assertEquals(32767L, vz);
          Assert.assertEquals(1L, vw);
        }

        {
          final long vx = (long) Signed16.unpackFromBuffer(rb, 8);
          final long vy = (long) Signed16.unpackFromBuffer(rb, 10);
          final long vz = (long) Signed16.unpackFromBuffer(rb, 12);
          final long vw = (long) Signed16.unpackFromBuffer(rb, 14);
          Assert.assertEquals(-32767L, vx);
          Assert.assertEquals(0L, vy);
          Assert.assertEquals(32767L, vz);
          Assert.assertEquals(1L, vw);
        }

        {
          final long vx = (long) Signed16.unpackFromBuffer(rb, 16);
          final long vy = (long) Signed16.unpackFromBuffer(rb, 18);
          final long vz = (long) Signed16.unpackFromBuffer(rb, 20);
          final long vw = (long) Signed16.unpackFromBuffer(rb, 22);
          Assert.assertEquals(-32767L, vx);
          Assert.assertEquals(0L, vy);
          Assert.assertEquals(32767L, vz);
          Assert.assertEquals(1L, vw);
        }
        break;
      }
      default: {
        throw new UnreachableCodeException();
      }
    }
  }

  private static void checkTypeSigned32(
    final int component_count,
    final ByteBuffer rb)
  {
    switch (component_count) {
      case 1: {
        {
          Assert.assertEquals(-2147483648L, (long) rb.getInt(0));
          Assert.assertEquals(0L, (long) rb.getInt(4));
          Assert.assertEquals(2147483647L, (long) rb.getInt(8));
        }
        break;
      }
      case 2: {
        {
          final long vx = (long) Signed32.unpackFromBuffer(rb, 0);
          final long vy = (long) Signed32.unpackFromBuffer(rb, 4);
          Assert.assertEquals(-2147483648L, vx);
          Assert.assertEquals(2147483647L, vy);
        }

        {
          final long vx = (long) Signed32.unpackFromBuffer(rb, 8);
          final long vy = (long) Signed32.unpackFromBuffer(rb, 12);
          Assert.assertEquals(-2147483648L, vx);
          Assert.assertEquals(2147483647L, vy);
        }

        {
          final long vx = (long) Signed32.unpackFromBuffer(rb, 16);
          final long vy = (long) Signed32.unpackFromBuffer(rb, 20);
          Assert.assertEquals(-2147483648L, vx);
          Assert.assertEquals(2147483647L, vy);
        }
        break;
      }
      case 3: {
        {
          final long vx = (long) Signed32.unpackFromBuffer(rb, 0);
          final long vy = (long) Signed32.unpackFromBuffer(rb, 4);
          final long vz = (long) Signed32.unpackFromBuffer(rb, 8);
          Assert.assertEquals(-2147483648L, vx);
          Assert.assertEquals(0L, vy);
          Assert.assertEquals(2147483647L, vz);
        }

        {
          final long vx = (long) Signed32.unpackFromBuffer(rb, 12);
          final long vy = (long) Signed32.unpackFromBuffer(rb, 16);
          final long vz = (long) Signed32.unpackFromBuffer(rb, 20);
          Assert.assertEquals(-2147483648L, vx);
          Assert.assertEquals(0L, vy);
          Assert.assertEquals(2147483647L, vz);
        }

        {
          final long vx = (long) Signed32.unpackFromBuffer(rb, 24);
          final long vy = (long) Signed32.unpackFromBuffer(rb, 28);
          final long vz = (long) Signed32.unpackFromBuffer(rb, 32);
          Assert.assertEquals(-2147483648L, vx);
          Assert.assertEquals(0L, vy);
          Assert.assertEquals(2147483647L, vz);
        }
        break;
      }
      case 4: {
        {
          final long vx = (long) Signed32.unpackFromBuffer(rb, 0);
          final long vy = (long) Signed32.unpackFromBuffer(rb, 4);
          final long vz = (long) Signed32.unpackFromBuffer(rb, 8);
          final long vw = (long) Signed32.unpackFromBuffer(rb, 12);
          Assert.assertEquals(-2147483648L, vx);
          Assert.assertEquals(0L, vy);
          Assert.assertEquals(2147483647L, vz);
          Assert.assertEquals(1L, vw);
        }

        {
          final long vx = (long) Signed32.unpackFromBuffer(rb, 16);
          final long vy = (long) Signed32.unpackFromBuffer(rb, 20);
          final long vz = (long) Signed32.unpackFromBuffer(rb, 24);
          final long vw = (long) Signed32.unpackFromBuffer(rb, 28);
          Assert.assertEquals(-2147483648L, vx);
          Assert.assertEquals(0L, vy);
          Assert.assertEquals(2147483647L, vz);
          Assert.assertEquals(1L, vw);
        }

        {
          final long vx = (long) Signed32.unpackFromBuffer(rb, 32);
          final long vy = (long) Signed32.unpackFromBuffer(rb, 36);
          final long vz = (long) Signed32.unpackFromBuffer(rb, 40);
          final long vw = (long) Signed32.unpackFromBuffer(rb, 44);
          Assert.assertEquals(-2147483648L, vx);
          Assert.assertEquals(0L, vy);
          Assert.assertEquals(2147483647L, vz);
          Assert.assertEquals(1L, vw);
        }
        break;
      }
      default: {
        throw new UnreachableCodeException();
      }
    }
  }

  private static void checkTypeFloat32(
    final int component_count,
    final ByteBuffer rb)
  {
    switch (component_count) {
      case 1: {
        Assert.assertEquals(-1000.0f, rb.getFloat(0), 0.0f);
        Assert.assertEquals(0.0f, rb.getFloat(4), 0.0f);
        Assert.assertEquals(1000.0f, rb.getFloat(8), 0.0f);
        break;
      }
      case 2: {
        {
          final double vx = (double) rb.getFloat(0);
          final double vy = (double) rb.getFloat(4);
          Assert.assertEquals(vx, -1000.0, 0.0);
          Assert.assertEquals(vy, 1000.0, 0.0);
        }

        {
          final double vx = (double) rb.getFloat(8);
          final double vy = (double) rb.getFloat(12);
          Assert.assertEquals(vx, -1000.0, 0.0);
          Assert.assertEquals(vy, 1000.0, 0.0);
        }

        {
          final double vx = (double) rb.getFloat(16);
          final double vy = (double) rb.getFloat(20);
          Assert.assertEquals(vx, -1000.0, 0.0);
          Assert.assertEquals(vy, 1000.0, 0.0);
        }
        break;
      }
      case 3: {
        {
          final double vx = (double) rb.getFloat(0);
          final double vy = (double) rb.getFloat(4);
          final double vz = (double) rb.getFloat(8);
          Assert.assertEquals(vx, -1000.0, 0.0);
          Assert.assertEquals(vy, 0.0, 0.0);
          Assert.assertEquals(vz, 1000.0, 0.0);
        }

        {
          final double vx = (double) rb.getFloat(12);
          final double vy = (double) rb.getFloat(16);
          final double vz = (double) rb.getFloat(20);
          Assert.assertEquals(vx, -1000.0, 0.0);
          Assert.assertEquals(vy, 0.0, 0.0);
          Assert.assertEquals(vz, 1000.0, 0.0);
        }

        {
          final double vx = (double) rb.getFloat(24);
          final double vy = (double) rb.getFloat(28);
          final double vz = (double) rb.getFloat(32);
          Assert.assertEquals(vx, -1000.0, 0.0);
          Assert.assertEquals(vy, 0.0, 0.0);
          Assert.assertEquals(vz, 1000.0, 0.0);
        }
        break;
      }
      case 4: {
        {
          final double vx = (double) rb.getFloat(0);
          final double vy = (double) rb.getFloat(4);
          final double vz = (double) rb.getFloat(8);
          final double vw = (double) rb.getFloat(12);
          Assert.assertEquals(vx, -1000.0, 0.0);
          Assert.assertEquals(vy, 0.0, 0.0);
          Assert.assertEquals(vz, 1.0, 0.0);
          Assert.assertEquals(vw, 1000.0, 0.0);
        }

        {
          final double vx = (double) rb.getFloat(16);
          final double vy = (double) rb.getFloat(20);
          final double vz = (double) rb.getFloat(24);
          final double vw = (double) rb.getFloat(28);
          Assert.assertEquals(vx, -1000.0, 0.0);
          Assert.assertEquals(vy, 0.0, 0.0);
          Assert.assertEquals(vz, 1.0, 0.0);
          Assert.assertEquals(vw, 1000.0, 0.0);
        }

        {
          final double vx = (double) rb.getFloat(32);
          final double vy = (double) rb.getFloat(36);
          final double vz = (double) rb.getFloat(40);
          final double vw = (double) rb.getFloat(44);
          Assert.assertEquals(vx, -1000.0, 0.0);
          Assert.assertEquals(vy, 0.0, 0.0);
          Assert.assertEquals(vz, 1.0, 0.0);
          Assert.assertEquals(vw, 1000.0, 0.0);
        }
        break;
      }
      default: {
        throw new UnreachableCodeException();
      }
    }
  }

  private static void checkTypeFloat16(
    final int component_count,
    final ByteBuffer rb)
  {
    switch (component_count) {
      case 1: {
        Assert.assertEquals(
          -1000.0, Binary16.unpackDouble(rb.getChar(0)), 0.0);
        Assert.assertEquals(
          0.0, Binary16.unpackDouble(rb.getChar(2)), 0.0);
        Assert.assertEquals(
          1000.0, Binary16.unpackDouble(rb.getChar(4)), 0.0);
        break;
      }
      case 2: {
        {
          final double vx = Binary16.unpackDouble(rb.getChar(0));
          final double vy = Binary16.unpackDouble(rb.getChar(2));
          Assert.assertEquals(vx, -1000.0, 0.0);
          Assert.assertEquals(vy, 1000.0, 0.0);
        }

        {
          final double vx = Binary16.unpackDouble(rb.getChar(4));
          final double vy = Binary16.unpackDouble(rb.getChar(6));
          Assert.assertEquals(vx, -1000.0, 0.0);
          Assert.assertEquals(vy, 1000.0, 0.0);
        }

        {
          final double vx = Binary16.unpackDouble(rb.getChar(8));
          final double vy = Binary16.unpackDouble(rb.getChar(10));
          Assert.assertEquals(vx, -1000.0, 0.0);
          Assert.assertEquals(vy, 1000.0, 0.0);
        }
        break;
      }
      case 3: {
        {
          final double vx = Binary16.unpackDouble(rb.getChar(0));
          final double vy = Binary16.unpackDouble(rb.getChar(2));
          final double vz = Binary16.unpackDouble(rb.getChar(4));
          Assert.assertEquals(vx, -1000.0, 0.0);
          Assert.assertEquals(vy, 0.0, 0.0);
          Assert.assertEquals(vz, 1000.0, 0.0);
        }

        {
          final double vx = Binary16.unpackDouble(rb.getChar(6));
          final double vy = Binary16.unpackDouble(rb.getChar(8));
          final double vz = Binary16.unpackDouble(rb.getChar(10));
          Assert.assertEquals(vx, -1000.0, 0.0);
          Assert.assertEquals(vy, 0.0, 0.0);
          Assert.assertEquals(vz, 1000.0, 0.0);
        }

        {
          final double vx = Binary16.unpackDouble(rb.getChar(12));
          final double vy = Binary16.unpackDouble(rb.getChar(14));
          final double vz = Binary16.unpackDouble(rb.getChar(16));
          Assert.assertEquals(vx, -1000.0, 0.0);
          Assert.assertEquals(vy, 0.0, 0.0);
          Assert.assertEquals(vz, 1000.0, 0.0);
        }
        break;
      }
      case 4: {
        {
          final double vx = Binary16.unpackDouble(rb.getChar(0));
          final double vy = Binary16.unpackDouble(rb.getChar(2));
          final double vz = Binary16.unpackDouble(rb.getChar(4));
          final double vw = Binary16.unpackDouble(rb.getChar(6));
          Assert.assertEquals(vx, -1000.0, 0.0);
          Assert.assertEquals(vy, 0.0, 0.0);
          Assert.assertEquals(vz, 1.0, 0.0);
          Assert.assertEquals(vw, 1000.0, 0.0);
        }

        {
          final double vx = Binary16.unpackDouble(rb.getChar(8));
          final double vy = Binary16.unpackDouble(rb.getChar(10));
          final double vz = Binary16.unpackDouble(rb.getChar(12));
          final double vw = Binary16.unpackDouble(rb.getChar(14));
          Assert.assertEquals(vx, -1000.0, 0.0);
          Assert.assertEquals(vy, 0.0, 0.0);
          Assert.assertEquals(vz, 1.0, 0.0);
          Assert.assertEquals(vw, 1000.0, 0.0);
        }

        {
          final double vx = Binary16.unpackDouble(rb.getChar(16));
          final double vy = Binary16.unpackDouble(rb.getChar(18));
          final double vz = Binary16.unpackDouble(rb.getChar(20));
          final double vw = Binary16.unpackDouble(rb.getChar(22));
          Assert.assertEquals(vx, -1000.0, 0.0);
          Assert.assertEquals(vy, 0.0, 0.0);
          Assert.assertEquals(vz, 1.0, 0.0);
          Assert.assertEquals(vw, 1000.0, 0.0);
        }
        break;
      }
      default: {
        throw new UnreachableCodeException();
      }
    }
  }

  private static void checkTypeSigned8(
    final int component_count,
    final ByteBuffer rb)
  {
    switch (component_count) {
      case 1: {
        Assert.assertEquals(-127L, (long) rb.get(0));
        Assert.assertEquals(0L, (long) rb.get(1));
        Assert.assertEquals(127L, (long) rb.get(2));
        break;
      }
      case 2: {
        Assert.assertEquals(-127L, (long) rb.get(0));
        Assert.assertEquals(0L, (long) rb.get(1));
        Assert.assertEquals(0L, (long) rb.get(2));
        Assert.assertEquals(0L, (long) rb.get(3));
        Assert.assertEquals(127L, (long) rb.get(4));
        Assert.assertEquals(0L, (long) rb.get(5));
        break;
      }
      case 3: {
        Assert.assertEquals(-127L, (long) rb.get(0));
        Assert.assertEquals(0L, (long) rb.get(1));
        Assert.assertEquals(127L, (long) rb.get(2));

        Assert.assertEquals(-127L, (long) rb.get(3));
        Assert.assertEquals(0L, (long) rb.get(4));
        Assert.assertEquals(127L, (long) rb.get(5));

        Assert.assertEquals(-127L, (long) rb.get(6));
        Assert.assertEquals(0L, (long) rb.get(7));
        Assert.assertEquals(127L, (long) rb.get(8));
        break;
      }
      case 4: {
        Assert.assertEquals(-127L, (long) rb.get(0));
        Assert.assertEquals(0L, (long) rb.get(1));
        Assert.assertEquals(127L, (long) rb.get(2));
        Assert.assertEquals(0L, (long) rb.get(3));

        Assert.assertEquals(-127L, (long) rb.get(4));
        Assert.assertEquals(0L, (long) rb.get(5));
        Assert.assertEquals(127L, (long) rb.get(6));
        Assert.assertEquals(0L, (long) rb.get(7));

        Assert.assertEquals(-127L, (long) rb.get(8));
        Assert.assertEquals(0L, (long) rb.get(9));
        Assert.assertEquals(127L, (long) rb.get(10));
        Assert.assertEquals(0L, (long) rb.get(11));
        break;
      }
      default: {
        throw new UnreachableCodeException();
      }
    }
  }

  private static SMFParserSequentialType createParser(
    final SMFParserEventsType loader,
    final String name)
    throws IOException
  {
    final String rpath = "/com/io7m/smfj/jcanephora/tests/" + name;
    try (final InputStream stream =
           SMFArrayLoaderContract.class.getResourceAsStream(rpath)) {
      final SMFParserProviderType fmt = new SMFFormatText();
      final Path path = Paths.get(rpath);
      final SMFParserSequentialType parser =
        fmt.parserCreateSequential(loader, path, stream);
      parser.parseHeader();
      parser.parseData();
      return parser;
    }
  }

  protected abstract JCGLContextType newContext(
    String name,
    int depth_bits,
    int stencil_bits);

  @Test
  public final void testLoadIntegerSigned8_1()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_BYTE;
    final int component_count = 1;
    final int vertex_count = 3;
    final String name = "integer8_1.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadIntegerSigned8_2()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_BYTE;
    final int component_count = 2;
    final int vertex_count = 3;
    final String name = "integer8_2.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadIntegerSigned8_3()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_BYTE;
    final int component_count = 3;
    final int vertex_count = 3;
    final String name = "integer8_3.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadIntegerSigned8_4()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_BYTE;
    final int component_count = 4;
    final int vertex_count = 3;
    final String name = "integer8_4.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadIntegerSigned16_1()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_SHORT;
    final int component_count = 1;
    final int vertex_count = 3;
    final String name = "integer16_1.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadIntegerSigned16_2()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_SHORT;
    final int component_count = 2;
    final int vertex_count = 3;
    final String name = "integer16_2.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadIntegerSigned16_3()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_SHORT;
    final int component_count = 3;
    final int vertex_count = 3;
    final String name = "integer16_3.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadIntegerSigned16_4()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_SHORT;
    final int component_count = 4;
    final int vertex_count = 3;
    final String name = "integer16_4.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadIntegerSigned32_1()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_INT;
    final int component_count = 1;
    final int vertex_count = 3;
    final String name = "integer32_1.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadIntegerSigned32_2()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_INT;
    final int component_count = 2;
    final int vertex_count = 3;
    final String name = "integer32_2.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadIntegerSigned32_3()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_INT;
    final int component_count = 3;
    final int vertex_count = 3;
    final String name = "integer32_3.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadIntegerSigned32_4()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_INT;
    final int component_count = 4;
    final int vertex_count = 3;
    final String name = "integer32_4.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadIntegerUnsigned8_1()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_UNSIGNED_BYTE;
    final int component_count = 1;
    final int vertex_count = 3;
    final String name = "unsigned8_1.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadIntegerUnsigned8_2()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_UNSIGNED_BYTE;
    final int component_count = 2;
    final int vertex_count = 3;
    final String name = "unsigned8_2.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadIntegerUnsigned8_3()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_UNSIGNED_BYTE;
    final int component_count = 3;
    final int vertex_count = 3;
    final String name = "unsigned8_3.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadIntegerUnsigned8_4()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_UNSIGNED_BYTE;
    final int component_count = 4;
    final int vertex_count = 3;
    final String name = "unsigned8_4.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadIntegerUnsigned16_1()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_UNSIGNED_SHORT;
    final int component_count = 1;
    final int vertex_count = 3;
    final String name = "unsigned16_1.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadIntegerUnsigned16_2()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_UNSIGNED_SHORT;
    final int component_count = 2;
    final int vertex_count = 3;
    final String name = "unsigned16_2.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadIntegerUnsigned16_3()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_UNSIGNED_SHORT;
    final int component_count = 3;
    final int vertex_count = 3;
    final String name = "unsigned16_3.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadIntegerUnsigned16_4()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_UNSIGNED_SHORT;
    final int component_count = 4;
    final int vertex_count = 3;
    final String name = "unsigned16_4.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadIntegerUnsigned32_1()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_UNSIGNED_INT;
    final int component_count = 1;
    final int vertex_count = 3;
    final String name = "unsigned32_1.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadIntegerUnsigned32_2()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_UNSIGNED_INT;
    final int component_count = 2;
    final int vertex_count = 3;
    final String name = "unsigned32_2.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadIntegerUnsigned32_3()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_UNSIGNED_INT;
    final int component_count = 3;
    final int vertex_count = 3;
    final String name = "unsigned32_3.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadIntegerUnsigned32_4()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_UNSIGNED_INT;
    final int component_count = 4;
    final int vertex_count = 3;
    final String name = "unsigned32_4.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadFloat32_1()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_FLOAT;
    final int component_count = 1;
    final int vertex_count = 3;
    final String name = "float32_1.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadFloat32_2()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_FLOAT;
    final int component_count = 2;
    final int vertex_count = 3;
    final String name = "float32_2.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadFloat32_3()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_FLOAT;
    final int component_count = 3;
    final int vertex_count = 3;
    final String name = "float32_3.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadFloat32_4()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_FLOAT;
    final int component_count = 4;
    final int vertex_count = 3;
    final String name = "float32_4.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadFloat16_1()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_HALF_FLOAT;
    final int component_count = 1;
    final int vertex_count = 3;
    final String name = "float16_1.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadFloat16_2()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_HALF_FLOAT;
    final int component_count = 2;
    final int vertex_count = 3;
    final String name = "float16_2.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadFloat16_3()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_HALF_FLOAT;
    final int component_count = 3;
    final int vertex_count = 3;
    final String name = "float16_3.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  @Test
  public final void testLoadFloat16_4()
    throws Exception
  {
    final JCGLContextType c = this.newContext("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final JCGLScalarType type = JCGLScalarType.TYPE_HALF_FLOAT;
    final int component_count = 4;
    final int vertex_count = 3;
    final String name = "float16_4.smft";

    this.check(g, type, component_count, vertex_count, name);
  }

  private void check(
    final JCGLInterfaceGL33Type g,
    final JCGLScalarType type,
    final int component_count,
    final int vertex_count,
    final String name)
    throws IOException
  {
    final SMFArrayAttributeMapping attr =
      SMFArrayAttributeMapping.builder()
        .setComponentCount(component_count)
        .setComponentType(type)
        .setIndex(0)
        .setName(SMFAttributeName.of("x"))
        .build();

    final SMFArrayObjectConfiguration config =
      SMFArrayObjectConfiguration.builder()
        .setArrayBufferUsage(JCGLUsageHint.USAGE_STATIC_DRAW)
        .setIndexBufferUsage(JCGLUsageHint.USAGE_STATIC_DRAW)
        .setMappings(HashMap.of(attr.name(), attr))
        .build();

    final SMFArrayLoaderType loader =
      SMFArrayLoaders.newLoader(g, new Meta(), config);

    try (final SMFParserSequentialType parser =
           createParser(loader, name)) {
      // Nothing
    }

    Assert.assertTrue(loader.errors().isEmpty());

    final JCGLArrayBufferType array_buffer = loader.arrayBuffer();
    final JCGLArrayObjectType array_object = loader.arrayObject();
    final JCGLIndexBufferType index_buffer = loader.indexBuffer();
    Assert.assertEquals(
      (long) (type.getSizeBytes() * component_count * vertex_count),
      array_buffer.getRange().getInterval());
    Assert.assertEquals(
      3L, index_buffer.getRange().getInterval());
    Assert.assertEquals(
      array_buffer, array_object.getAttributeAt(0).get().getArrayBuffer());

    final JCGLArrayBuffersType g_ab = g.getArrayBuffers();
    final JCGLIndexBuffersType g_ib = g.getIndexBuffers();
    final JCGLArrayObjectsType g_ao = g.getArrayObjects();

    checkType(type, component_count, array_buffer, g_ab);

    g_ab.arrayBufferDelete(array_buffer);
    g_ib.indexBufferDelete(index_buffer);
    g_ao.arrayObjectDelete(array_object);
  }

  private final class Meta implements SMFParserEventsMetaType
  {
    @Override
    public boolean onMeta(
      final long vendor,
      final long schema,
      final long length)
    {
      throw new UnreachableCodeException();
    }

    @Override
    public void onMetaData(
      final long vendor,
      final long schema,
      final byte[] data)
    {
      throw new UnreachableCodeException();
    }

    @Override
    public void onError(
      final SMFParseError e)
    {
      throw new UnreachableCodeException();
    }
  }
}
