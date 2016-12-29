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

import com.io7m.jcanephora.core.JCGLArrayBufferType;
import com.io7m.jcanephora.core.JCGLArrayObjectBuilderType;
import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.JCGLBufferUpdateType;
import com.io7m.jcanephora.core.JCGLBufferUpdates;
import com.io7m.jcanephora.core.JCGLIndexBufferType;
import com.io7m.jcanephora.core.JCGLScalarIntegralType;
import com.io7m.jcanephora.core.JCGLScalarType;
import com.io7m.jcanephora.core.JCGLUnsignedType;
import com.io7m.jcanephora.core.api.JCGLArrayBuffersType;
import com.io7m.jcanephora.core.api.JCGLIndexBuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.smfj.core.SMFAttribute;
import com.io7m.smfj.core.SMFAttributeName;
import com.io7m.smfj.core.SMFAttributeNameType;
import com.io7m.smfj.core.SMFAttributeType;
import com.io7m.smfj.core.SMFComponentType;
import com.io7m.smfj.core.SMFFormatVersion;
import com.io7m.smfj.core.SMFHeader;
import com.io7m.smfj.parser.api.SMFParseError;
import com.io7m.smfj.parser.api.SMFParserEventsMetaType;
import javaslang.collection.List;
import javaslang.collection.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The default provider of {@link SMFArrayLoaderType} values.
 */

public final class SMFArrayLoaders
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(SMFArrayLoaders.class);
  }

  private SMFArrayLoaders()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Construct a new array loader.
   *
   * @param g             A GL interface
   * @param in_meta       A metadata listener
   * @param configuration An array object configuration
   *
   * @return A new array loader
   */

  public static SMFArrayLoaderType newLoader(
    final JCGLInterfaceGL33Type g,
    final SMFParserEventsMetaType in_meta,
    final SMFArrayObjectConfiguration configuration)
  {
    return new Loader(g, in_meta, configuration);
  }

  private static final class Loader implements SMFArrayLoaderType
  {
    private final JCGLInterfaceGL33Type g;
    private final SMFArrayObjectConfiguration configuration;
    private final SortedMap<Integer, Attribute> attributes_by_index;
    private final SortedMap<String, Attribute> attributes_by_name;
    private final SMFParserEventsMetaType meta;
    private List<SMFParseError> errors;
    private int index_position;
    private long array_buffer_size;
    private int vertex_size;
    private JCGLArrayObjectBuilderType array_object_builder;
    private JCGLArrayObjectType array_object;
    private JCGLArrayBufferType array_buffer;
    private JCGLUnsignedType index_type;
    private JCGLIndexBufferType index_buffer;
    private JCGLBufferUpdateType<JCGLArrayBufferType> array_update;
    private SMFByteBufferPacker packer;
    private JCGLBufferUpdateType<JCGLIndexBufferType> index_update;
    private int index_stride;

    Loader(
      final JCGLInterfaceGL33Type in_g,
      final SMFParserEventsMetaType in_meta,
      final SMFArrayObjectConfiguration in_configuration)
    {
      this.g = NullCheck.notNull(in_g, "GL");
      this.configuration = NullCheck.notNull(in_configuration, "Configuration");
      this.meta = NullCheck.notNull(in_meta, "Meta");
      this.attributes_by_index = new TreeMap<>();
      this.attributes_by_name = new TreeMap<>();
      this.vertex_size = 0;
      this.array_buffer_size = 0L;
      this.index_position = 0;
      this.errors = List.empty();
    }

    private static JCGLUnsignedType determineIndexType(
      final long size)
    {
      if (size == 8L) {
        return JCGLUnsignedType.TYPE_UNSIGNED_BYTE;
      }
      if (size == 16L) {
        return JCGLUnsignedType.TYPE_UNSIGNED_SHORT;
      }
      return JCGLUnsignedType.TYPE_UNSIGNED_INT;
    }

    private static IllegalArgumentException incompatibleType(
      final SMFAttributeNameType name,
      final SMFArrayAttributeMappingType required_attribute,
      final SMFAttributeType received_attribute)
    {
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Incompatible types.");
      sb.append(System.lineSeparator());
      sb.append("  Attribute: ");
      sb.append(name.value());
      sb.append(System.lineSeparator());
      sb.append("  Required type: ");
      sb.append(required_attribute.componentCount());
      sb.append(" of ");
      sb.append(required_attribute.componentType().get());
      sb.append(System.lineSeparator());
      sb.append("  Received type: ");
      sb.append(received_attribute.componentCount());
      sb.append(" of ");
      sb.append(received_attribute.componentType());
      sb.append(System.lineSeparator());
      return new IllegalArgumentException(sb.toString());
    }

    private static
    @Nullable
    JCGLScalarType determineComponentType(
      final Optional<JCGLScalarType> required_opt,
      final SMFComponentType received)
    {
      if (required_opt.isPresent()) {
        final JCGLScalarType required = required_opt.get();
        switch (received) {
          case ELEMENT_TYPE_INTEGER_SIGNED: {
            switch (required) {
              case TYPE_BYTE:
              case TYPE_INT:
              case TYPE_SHORT:
                return required;
              case TYPE_HALF_FLOAT:
              case TYPE_FLOAT:
              case TYPE_UNSIGNED_BYTE:
              case TYPE_UNSIGNED_INT:
              case TYPE_UNSIGNED_SHORT:
                return null;
            }
            throw new UnreachableCodeException();
          }
          case ELEMENT_TYPE_INTEGER_UNSIGNED: {
            switch (required) {
              case TYPE_BYTE:
              case TYPE_INT:
              case TYPE_SHORT:
              case TYPE_HALF_FLOAT:
              case TYPE_FLOAT:
                return null;
              case TYPE_UNSIGNED_BYTE:
              case TYPE_UNSIGNED_INT:
              case TYPE_UNSIGNED_SHORT:
                return required;
            }
            throw new UnreachableCodeException();
          }
          case ELEMENT_TYPE_FLOATING: {
            switch (required) {
              case TYPE_BYTE:
              case TYPE_INT:
              case TYPE_SHORT:
              case TYPE_UNSIGNED_BYTE:
              case TYPE_UNSIGNED_INT:
              case TYPE_UNSIGNED_SHORT:
                return null;
              case TYPE_HALF_FLOAT:
              case TYPE_FLOAT:
                return required;
            }
            throw new UnreachableCodeException();
          }
        }

        throw new UnreachableCodeException();
      }

      switch (received) {
        case ELEMENT_TYPE_INTEGER_SIGNED:
          return JCGLScalarType.TYPE_INT;
        case ELEMENT_TYPE_INTEGER_UNSIGNED:
          return JCGLScalarType.TYPE_UNSIGNED_INT;
        case ELEMENT_TYPE_FLOATING:
          return JCGLScalarType.TYPE_FLOAT;
      }

      throw new UnreachableCodeException();
    }

    @Override
    public void onError(
      final SMFParseError e)
    {
      final LexicalPosition<Path> lex = e.lexical();
      LOG.error(
        "parse error: {}:{}:{}: {}",
        lex.file(),
        Integer.valueOf(lex.line()),
        Integer.valueOf(lex.column()),
        e.message());

      this.errors = this.errors.append(e);
    }

    @Override
    public void onStart()
    {
      this.array_object_builder =
        this.g.getArrayObjects().arrayObjectNewBuilder();
    }

    @Override
    public void onVersionReceived(
      final SMFFormatVersion version)
    {

    }

    @Override
    public void onFinish()
    {
      final JCGLArrayBuffersType g_ab = this.g.getArrayBuffers();
      final JCGLIndexBuffersType g_ib = this.g.getIndexBuffers();

      if (!this.errors.isEmpty()) {
        if (this.array_buffer != null) {
          g_ab.arrayBufferDelete(this.array_buffer);
          this.array_buffer = null;
        }
        if (this.index_buffer != null) {
          g_ib.indexBufferDelete(this.index_buffer);
          this.index_buffer = null;
        }
      }

      g_ab.arrayBufferUpdate(this.array_update);
      g_ib.indexBufferUpdate(this.index_update);

      this.array_object =
        this.g.getArrayObjects().arrayObjectAllocate(this.array_object_builder);
    }

    @Override
    public void onHeaderParsed(
      final SMFHeader in_header)
    {
      this.determineMappings(in_header);

      final JCGLArrayBuffersType g_ab = this.g.getArrayBuffers();
      this.array_buffer =
        g_ab.arrayBufferAllocate(
          this.array_buffer_size, this.configuration.arrayBufferUsage());
      this.array_update =
        JCGLBufferUpdates.newUpdateReplacingAll(this.array_buffer);

      final JCGLIndexBuffersType g_ib = this.g.getIndexBuffers();
      this.index_buffer =
        g_ib.indexBufferAllocate(
          Math.multiplyExact(in_header.triangleCount(), 3L),
          this.index_type,
          this.configuration.indexBufferUsage());
      this.index_stride =
        Math.multiplyExact(this.index_type.getSizeBytes(), 3);
      this.index_update =
        JCGLBufferUpdates.newUpdateReplacingAll(this.index_buffer);

      this.array_object_builder.setIndexBuffer(this.index_buffer);
      for (final Integer in_index : this.attributes_by_index.keySet()) {
        final Attribute attr = this.attributes_by_index.get(in_index);
        switch (attr.type) {
          case TYPE_UNSIGNED_INT:
          case TYPE_UNSIGNED_SHORT:
          case TYPE_UNSIGNED_BYTE:
          case TYPE_INT:
          case TYPE_SHORT:
          case TYPE_BYTE: {
            this.array_object_builder.setAttributeIntegral(
              in_index.intValue(),
              this.array_buffer,
              attr.count,
              JCGLScalarIntegralType.fromScalar(attr.type),
              this.vertex_size,
              attr.offset);
            break;
          }
          case TYPE_FLOAT:
          case TYPE_HALF_FLOAT: {
            this.array_object_builder.setAttributeFloatingPoint(
              in_index.intValue(),
              this.array_buffer,
              attr.count,
              attr.type,
              this.vertex_size,
              attr.offset,
              false);
            break;
          }
        }
      }
    }

    private void determineMappings(
      final SMFHeader in_header)
    {
      final Map<SMFAttributeName, SMFArrayAttributeMapping> required_attributes =
        this.configuration.mappings();
      final Map<SMFAttributeName, SMFAttribute> received_attributes =
        in_header.attributesByName();

      for (final SMFAttributeName name : required_attributes.keySet()) {
        final SMFArrayAttributeMapping required_attribute =
          required_attributes.get(name).get();

        if (received_attributes.containsKey(name)) {
          final SMFAttribute received_attribute =
            received_attributes.get(name).get();

          final JCGLScalarType decided =
            determineComponentType(
              required_attribute.componentType(),
              received_attribute.componentType());

          if (decided == null) {
            throw incompatibleType(
              name, required_attribute, received_attribute);
          }

          final Attribute attr =
            new Attribute(received_attribute.componentCount(), decided);
          final Integer b_index =
            Integer.valueOf(required_attribute.index());
          this.attributes_by_index.put(b_index, attr);
          this.attributes_by_name.put(name.value(), attr);
        }
      }

      for (final Integer a_index : this.attributes_by_index.keySet()) {
        final Attribute a = this.attributes_by_index.get(a_index);
        a.offset = this.vertex_size;
        final int size = Math.multiplyExact(a.count, a.type.getSizeBytes());
        this.vertex_size = Math.addExact(this.vertex_size, size);
      }

      this.array_buffer_size = Math.multiplyExact(
        this.vertex_size, in_header.vertexCount());
      this.index_type = determineIndexType(
        in_header.triangleIndexSizeBits());
    }

    @Override
    public boolean onMeta(
      final long vendor,
      final long schema,
      final long length)
    {
      return this.meta.onMeta(vendor, schema, length);
    }

    @Override
    public void onMetaData(
      final long vendor,
      final long schema,
      final byte[] data)
    {
      this.meta.onMetaData(vendor, schema, data);
    }

    @Override
    public void onDataAttributeStart(
      final SMFAttribute attribute)
    {
      final String name = attribute.name().value();
      if (this.attributes_by_name.containsKey(name)) {
        final Attribute attr = this.attributes_by_name.get(name);
        this.packer = new SMFByteBufferPacker(
          this.array_update.getData(),
          attr.type,
          (int) attr.offset,
          this.vertex_size);
      } else {
        this.packer = null;
      }
    }

    @Override
    public void onDataAttributeValueIntegerSigned1(
      final long x)
    {
      if (this.packer != null) {
        this.packer.onDataAttributeValueIntegerSigned1(x);
      }
    }

    @Override
    public void onDataAttributeValueIntegerSigned2(
      final long x,
      final long y)
    {
      if (this.packer != null) {
        this.packer.onDataAttributeValueIntegerSigned2(x, y);
      }
    }

    @Override
    public void onDataAttributeValueIntegerSigned3(
      final long x,
      final long y,
      final long z)
    {
      if (this.packer != null) {
        this.packer.onDataAttributeValueIntegerSigned3(x, y, z);
      }
    }

    @Override
    public void onDataAttributeValueIntegerSigned4(
      final long x,
      final long y,
      final long z,
      final long w)
    {
      if (this.packer != null) {
        this.packer.onDataAttributeValueIntegerSigned4(x, y, z, w);
      }
    }

    @Override
    public void onDataAttributeValueIntegerUnsigned1(
      final long x)
    {
      if (this.packer != null) {
        this.packer.onDataAttributeValueIntegerUnsigned1(x);
      }
    }

    @Override
    public void onDataAttributeValueIntegerUnsigned2(
      final long x,
      final long y)
    {
      if (this.packer != null) {
        this.packer.onDataAttributeValueIntegerUnsigned2(x, y);
      }
    }

    @Override
    public void onDataAttributeValueIntegerUnsigned3(
      final long x,
      final long y,
      final long z)
    {
      if (this.packer != null) {
        this.packer.onDataAttributeValueIntegerUnsigned3(x, y, z);
      }
    }

    @Override
    public void onDataAttributeValueIntegerUnsigned4(
      final long x,
      final long y,
      final long z,
      final long w)
    {
      if (this.packer != null) {
        this.packer.onDataAttributeValueIntegerUnsigned4(x, y, z, w);
      }
    }

    @Override
    public void onDataAttributeValueFloat1(
      final double x)
    {
      if (this.packer != null) {
        this.packer.onDataAttributeValueFloat1(x);
      }
    }

    @Override
    public void onDataAttributeValueFloat2(
      final double x,
      final double y)
    {
      if (this.packer != null) {
        this.packer.onDataAttributeValueFloat2(x, y);
      }
    }

    @Override
    public void onDataAttributeValueFloat3(
      final double x,
      final double y,
      final double z)
    {
      if (this.packer != null) {
        this.packer.onDataAttributeValueFloat3(x, y, z);
      }
    }

    @Override
    public void onDataAttributeValueFloat4(
      final double x,
      final double y,
      final double z,
      final double w)
    {
      if (this.packer != null) {
        this.packer.onDataAttributeValueFloat4(x, y, z, w);
      }
    }

    @Override
    public void onDataAttributeFinish(
      final SMFAttribute attribute)
    {
      this.packer = null;
    }

    @Override
    public void onDataTrianglesStart()
    {

    }

    @Override
    public void onDataTriangle(
      final long v0,
      final long v1,
      final long v2)
    {
      final ByteBuffer buffer = this.index_update.getData();

      switch (this.index_type) {
        case TYPE_UNSIGNED_BYTE: {
          buffer.put(this.index_position, (byte) (v0 & 0xffL));
          buffer.put(this.index_position + 1, (byte) (v1 & 0xffL));
          buffer.put(this.index_position + 2, (byte) (v2 & 0xffL));
          break;
        }
        case TYPE_UNSIGNED_INT: {
          buffer.putInt(this.index_position, (int) (v0 & 0xffffffffL));
          buffer.putInt(this.index_position + 4, (int) (v1 & 0xffffffffL));
          buffer.putInt(this.index_position + 8, (int) (v2 & 0xffffffffL));
          break;
        }
        case TYPE_UNSIGNED_SHORT: {
          buffer.putChar(this.index_position, (char) v0);
          buffer.putChar(this.index_position + 2, (char) v1);
          buffer.putChar(this.index_position + 4, (char) v2);
          break;
        }
      }

      this.index_position =
        Math.addExact(this.index_position, this.index_stride);
    }

    @Override
    public void onDataTrianglesFinish()
    {

    }

    @Override
    public List<SMFParseError> errors()
    {
      return this.errors;
    }

    @Override
    public JCGLArrayObjectType arrayObject()
      throws IllegalStateException
    {
      if (!this.errors.isEmpty()) {
        throw new IllegalStateException("Array loading has failed");
      }

      return this.array_object;
    }

    @Override
    public JCGLArrayBufferType arrayBuffer()
      throws IllegalStateException
    {
      if (!this.errors.isEmpty()) {
        throw new IllegalStateException("Array loading has failed");
      }

      return this.array_buffer;
    }

    @Override
    public JCGLIndexBufferType indexBuffer()
      throws IllegalStateException
    {
      if (!this.errors.isEmpty()) {
        throw new IllegalStateException("Array loading has failed");
      }

      return this.index_buffer;
    }

    private static final class Attribute
    {
      private final int count;
      private final JCGLScalarType type;
      private long offset;
      private boolean uploaded;

      Attribute(
        final int in_count,
        final JCGLScalarType in_type)
      {
        this.count = in_count;
        this.type = in_type;
      }
    }
  }
}
