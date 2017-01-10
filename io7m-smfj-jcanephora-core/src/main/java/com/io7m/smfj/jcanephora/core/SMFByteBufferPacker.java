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

import com.io7m.ieee754b16.Binary16;
import com.io7m.jcanephora.core.JCGLScalarType;
import com.io7m.jintegers.Unsigned16;
import com.io7m.jintegers.Unsigned32;
import com.io7m.jintegers.Unsigned8;
import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.smfj.core.SMFAttribute;
import com.io7m.smfj.parser.api.SMFParseError;
import com.io7m.smfj.parser.api.SMFParserEventsDataType;

import java.nio.ByteBuffer;

/**
 * An event listener that packs data into a given {@link ByteBuffer}.
 */

public final class SMFByteBufferPacker implements SMFParserEventsDataType
{
  private final int stride;
  private final ByteBuffer buffer;
  private final JCGLScalarType type;
  private int index;

  /**
   * Construct a packer.
   *
   * @param in_buffer The byte buffer
   * @param in_type   The type of packed components
   * @param in_offset The initial offset
   * @param in_stride The stride of an entire vertex
   */

  public SMFByteBufferPacker(
    final ByteBuffer in_buffer,
    final JCGLScalarType in_type,
    final int in_offset,
    final int in_stride)
  {
    this.buffer = NullCheck.notNull(in_buffer, "Buffer");
    this.type = NullCheck.notNull(in_type, "Type");
    this.index = in_offset;
    this.stride = in_stride;
  }

  private void next()
  {
    this.index = Math.addExact(this.index, this.stride);
  }

  @Override
  public void onDataAttributeStart(
    final SMFAttribute attribute)
  {
    throw new UnreachableCodeException();
  }

  @Override
  public void onDataAttributeValueIntegerSigned1(
    final long x)
  {
    switch (this.type) {
      case TYPE_BYTE: {
        final byte cx = (byte) x;
        this.buffer.put(this.index, cx);
        break;
      }
      case TYPE_INT: {
        final int cx = (int) x;
        this.buffer.putInt(this.index, cx);
        break;
      }
      case TYPE_SHORT: {
        final short cx = (short) x;
        this.buffer.putShort(this.index, cx);
        break;
      }
      case TYPE_HALF_FLOAT:
      case TYPE_FLOAT:
      case TYPE_UNSIGNED_BYTE:
      case TYPE_UNSIGNED_INT:
      case TYPE_UNSIGNED_SHORT:
        throw new UnreachableCodeException();
    }

    this.next();
  }

  @Override
  public void onDataAttributeValueIntegerSigned2(
    final long x,
    final long y)
  {
    switch (this.type) {
      case TYPE_BYTE: {
        final byte cx = (byte) x;
        final byte cy = (byte) y;
        this.buffer.put(this.index, cx);
        this.buffer.put(this.index + 1, cy);
        break;
      }
      case TYPE_INT: {
        final int cx = (int) x;
        final int cy = (int) y;
        this.buffer.putInt(this.index, cx);
        this.buffer.putInt(this.index + 4, cy);
        break;
      }
      case TYPE_SHORT: {
        final short cx = (short) x;
        final short cy = (short) y;
        this.buffer.putShort(this.index, cx);
        this.buffer.putShort(this.index + 2, cy);
        break;
      }
      case TYPE_HALF_FLOAT:
      case TYPE_FLOAT:
      case TYPE_UNSIGNED_BYTE:
      case TYPE_UNSIGNED_INT:
      case TYPE_UNSIGNED_SHORT:
        throw new UnreachableCodeException();
    }

    this.next();
  }

  @Override
  public void onDataAttributeValueIntegerSigned3(
    final long x,
    final long y,
    final long z)
  {
    switch (this.type) {
      case TYPE_BYTE: {
        final byte cx = (byte) x;
        final byte cy = (byte) y;
        final byte cz = (byte) z;
        this.buffer.put(this.index, cx);
        this.buffer.put(this.index + 1, cy);
        this.buffer.put(this.index + 2, cz);
        break;
      }
      case TYPE_INT: {
        final int cx = (int) x;
        final int cy = (int) y;
        final int cz = (int) z;
        this.buffer.putInt(this.index, cx);
        this.buffer.putInt(this.index + 4, cy);
        this.buffer.putInt(this.index + 8, cz);
        break;
      }
      case TYPE_SHORT: {
        final short cx = (short) x;
        final short cy = (short) y;
        final short cz = (short) z;
        this.buffer.putShort(this.index, cx);
        this.buffer.putShort(this.index + 2, cy);
        this.buffer.putShort(this.index + 4, cz);
        break;
      }
      case TYPE_HALF_FLOAT:
      case TYPE_FLOAT:
      case TYPE_UNSIGNED_BYTE:
      case TYPE_UNSIGNED_INT:
      case TYPE_UNSIGNED_SHORT:
        throw new UnreachableCodeException();
    }

    this.next();
  }

  @Override
  public void onDataAttributeValueIntegerSigned4(
    final long x,
    final long y,
    final long z,
    final long w)
  {
    switch (this.type) {
      case TYPE_BYTE: {
        final byte cx = (byte) x;
        final byte cy = (byte) y;
        final byte cz = (byte) z;
        final byte cw = (byte) w;
        this.buffer.put(this.index, cx);
        this.buffer.put(this.index + 1, cy);
        this.buffer.put(this.index + 2, cz);
        this.buffer.put(this.index + 3, cw);
        break;
      }
      case TYPE_INT: {
        final int cx = (int) x;
        final int cy = (int) y;
        final int cz = (int) z;
        final int cw = (int) w;
        this.buffer.putInt(this.index, cx);
        this.buffer.putInt(this.index + 4, cy);
        this.buffer.putInt(this.index + 8, cz);
        this.buffer.putInt(this.index + 12, cw);
        break;
      }
      case TYPE_SHORT: {
        final short cx = (short) x;
        final short cy = (short) y;
        final short cz = (short) z;
        final short cw = (short) w;
        this.buffer.putShort(this.index, cx);
        this.buffer.putShort(this.index + 2, cy);
        this.buffer.putShort(this.index + 4, cz);
        this.buffer.putShort(this.index + 6, cw);
        break;
      }
      case TYPE_HALF_FLOAT:
      case TYPE_FLOAT:
      case TYPE_UNSIGNED_BYTE:
      case TYPE_UNSIGNED_INT:
      case TYPE_UNSIGNED_SHORT:
        throw new UnreachableCodeException();
    }

    this.next();
  }

  @Override
  public void onDataAttributeValueIntegerUnsigned1(
    final long x)
  {
    switch (this.type) {
      case TYPE_UNSIGNED_BYTE: {
        Unsigned8.packToBuffer((int) x, this.buffer, this.index);
        break;
      }
      case TYPE_UNSIGNED_INT: {
        Unsigned32.packToBuffer(x, this.buffer, this.index);
        break;
      }
      case TYPE_UNSIGNED_SHORT: {
        Unsigned16.packToBuffer((int) x, this.buffer, this.index);
        break;
      }
      case TYPE_HALF_FLOAT:
      case TYPE_FLOAT:
      case TYPE_BYTE:
      case TYPE_INT:
      case TYPE_SHORT:
        throw new UnreachableCodeException();
    }

    this.next();
  }

  @Override
  public void onDataAttributeValueIntegerUnsigned2(
    final long x,
    final long y)
  {
    switch (this.type) {
      case TYPE_UNSIGNED_BYTE: {
        Unsigned8.packToBuffer((int) x, this.buffer, this.index);
        Unsigned8.packToBuffer((int) y, this.buffer, this.index + 1);
        break;
      }
      case TYPE_UNSIGNED_INT: {
        Unsigned32.packToBuffer(x, this.buffer, this.index);
        Unsigned32.packToBuffer(y, this.buffer, this.index + 4);
        break;
      }
      case TYPE_UNSIGNED_SHORT: {
        Unsigned16.packToBuffer((int) x, this.buffer, this.index);
        Unsigned16.packToBuffer((int) y, this.buffer, this.index + 2);
        break;
      }
      case TYPE_HALF_FLOAT:
      case TYPE_FLOAT:
      case TYPE_BYTE:
      case TYPE_INT:
      case TYPE_SHORT:
        throw new UnreachableCodeException();
    }

    this.next();
  }

  @Override
  public void onDataAttributeValueIntegerUnsigned3(
    final long x,
    final long y,
    final long z)
  {
    switch (this.type) {
      case TYPE_UNSIGNED_BYTE: {
        Unsigned8.packToBuffer((int) x, this.buffer, this.index);
        Unsigned8.packToBuffer((int) y, this.buffer, this.index + 1);
        Unsigned8.packToBuffer((int) z, this.buffer, this.index + 2);
        break;
      }
      case TYPE_UNSIGNED_INT: {
        Unsigned32.packToBuffer(x, this.buffer, this.index);
        Unsigned32.packToBuffer(y, this.buffer, this.index + 4);
        Unsigned32.packToBuffer(z, this.buffer, this.index + 8);
        break;
      }
      case TYPE_UNSIGNED_SHORT: {
        Unsigned16.packToBuffer((int) x, this.buffer, this.index);
        Unsigned16.packToBuffer((int) y, this.buffer, this.index + 2);
        Unsigned16.packToBuffer((int) z, this.buffer, this.index + 4);
        break;
      }
      case TYPE_HALF_FLOAT:
      case TYPE_FLOAT:
      case TYPE_BYTE:
      case TYPE_INT:
      case TYPE_SHORT:
        throw new UnreachableCodeException();
    }

    this.next();
  }

  @Override
  public void onDataAttributeValueIntegerUnsigned4(
    final long x,
    final long y,
    final long z,
    final long w)
  {
    switch (this.type) {
      case TYPE_UNSIGNED_BYTE: {
        Unsigned8.packToBuffer((int) x, this.buffer, this.index);
        Unsigned8.packToBuffer((int) y, this.buffer, this.index + 1);
        Unsigned8.packToBuffer((int) z, this.buffer, this.index + 2);
        Unsigned8.packToBuffer((int) w, this.buffer, this.index + 3);
        break;
      }
      case TYPE_UNSIGNED_INT: {
        Unsigned32.packToBuffer(x, this.buffer, this.index);
        Unsigned32.packToBuffer(y, this.buffer, this.index + 4);
        Unsigned32.packToBuffer(z, this.buffer, this.index + 8);
        Unsigned32.packToBuffer(w, this.buffer, this.index + 12);
        break;
      }
      case TYPE_UNSIGNED_SHORT: {
        Unsigned16.packToBuffer((int) x, this.buffer, this.index);
        Unsigned16.packToBuffer((int) y, this.buffer, this.index + 2);
        Unsigned16.packToBuffer((int) z, this.buffer, this.index + 4);
        Unsigned16.packToBuffer((int) w, this.buffer, this.index + 6);
        break;
      }
      case TYPE_HALF_FLOAT:
      case TYPE_FLOAT:
      case TYPE_BYTE:
      case TYPE_INT:
      case TYPE_SHORT:
        throw new UnreachableCodeException();
    }

    this.next();
  }

  @Override
  public void onDataAttributeValueFloat1(
    final double x)
  {
    switch (this.type) {
      case TYPE_FLOAT: {
        this.buffer.putFloat(this.index, (float) x);
        break;
      }
      case TYPE_HALF_FLOAT: {
        this.buffer.putChar(this.index, Binary16.packDouble(x));
        break;
      }
      case TYPE_UNSIGNED_BYTE:
      case TYPE_UNSIGNED_INT:
      case TYPE_UNSIGNED_SHORT:
      case TYPE_BYTE:
      case TYPE_INT:
      case TYPE_SHORT:
        throw new UnreachableCodeException();
    }

    this.next();
  }

  @Override
  public void onDataAttributeValueFloat2(
    final double x,
    final double y)
  {
    switch (this.type) {
      case TYPE_FLOAT: {
        this.buffer.putFloat(this.index, (float) x);
        this.buffer.putFloat(this.index + 4, (float) y);
        break;
      }
      case TYPE_HALF_FLOAT: {
        this.buffer.putChar(this.index, Binary16.packDouble(x));
        this.buffer.putChar(this.index + 2, Binary16.packDouble(y));
        break;
      }
      case TYPE_UNSIGNED_BYTE:
      case TYPE_UNSIGNED_INT:
      case TYPE_UNSIGNED_SHORT:
      case TYPE_BYTE:
      case TYPE_INT:
      case TYPE_SHORT:
        throw new UnreachableCodeException();
    }

    this.next();
  }

  @Override
  public void onDataAttributeValueFloat3(
    final double x,
    final double y,
    final double z)
  {
    switch (this.type) {
      case TYPE_FLOAT: {
        this.buffer.putFloat(this.index, (float) x);
        this.buffer.putFloat(this.index + 4, (float) y);
        this.buffer.putFloat(this.index + 8, (float) z);
        break;
      }
      case TYPE_HALF_FLOAT: {
        this.buffer.putChar(this.index, Binary16.packDouble(x));
        this.buffer.putChar(this.index + 2, Binary16.packDouble(y));
        this.buffer.putChar(this.index + 4, Binary16.packDouble(z));
        break;
      }
      case TYPE_UNSIGNED_BYTE:
      case TYPE_UNSIGNED_INT:
      case TYPE_UNSIGNED_SHORT:
      case TYPE_BYTE:
      case TYPE_INT:
      case TYPE_SHORT:
        throw new UnreachableCodeException();
    }

    this.next();
  }

  @Override
  public void onDataAttributeValueFloat4(
    final double x,
    final double y,
    final double z,
    final double w)
  {
    switch (this.type) {
      case TYPE_FLOAT: {
        this.buffer.putFloat(this.index, (float) x);
        this.buffer.putFloat(this.index + 4, (float) y);
        this.buffer.putFloat(this.index + 8, (float) z);
        this.buffer.putFloat(this.index + 12, (float) w);
        break;
      }
      case TYPE_HALF_FLOAT: {
        this.buffer.putChar(this.index, Binary16.packDouble(x));
        this.buffer.putChar(this.index + 2, Binary16.packDouble(y));
        this.buffer.putChar(this.index + 4, Binary16.packDouble(z));
        this.buffer.putChar(this.index + 6, Binary16.packDouble(w));
        break;
      }
      case TYPE_UNSIGNED_BYTE:
      case TYPE_UNSIGNED_INT:
      case TYPE_UNSIGNED_SHORT:
      case TYPE_BYTE:
      case TYPE_INT:
      case TYPE_SHORT:
        throw new UnreachableCodeException();
    }

    this.next();
  }

  @Override
  public void onDataAttributeFinish(
    final SMFAttribute attribute)
  {
    throw new UnreachableCodeException();
  }

  @Override
  public void onDataTrianglesStart()
  {
    throw new UnreachableCodeException();
  }

  @Override
  public void onDataTriangle(
    final long v0,
    final long v1,
    final long v2)
  {
    throw new UnreachableCodeException();
  }

  @Override
  public void onDataTrianglesFinish()
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
