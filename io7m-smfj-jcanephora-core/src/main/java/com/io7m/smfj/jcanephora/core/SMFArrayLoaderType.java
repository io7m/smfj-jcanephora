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
import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.JCGLIndexBufferType;
import com.io7m.smfj.parser.api.SMFParseError;
import com.io7m.smfj.parser.api.SMFParserEventsType;
import javaslang.collection.List;

/**
 * <p>The type of array loaders.</p>
 *
 * <p>An array loader has <i>failed</i> if {@link #errors()} is non-empty.
 * If an array loader has failed, attempting to access the loaded array buffer,
 * index buffer, or array object will raise an exception. If an array loader
 * has not failed, then the caller takes ownership of the loaded resources
 * and is responsible for deleting them when they are no longer being used.</p>
 */

public interface SMFArrayLoaderType extends SMFParserEventsType
{
  /**
   * @return The list of errors encountered during parsing, if any
   */

  List<SMFParseError> errors();

  /**
   * @return The loaded array object
   *
   * @throws IllegalStateException Iff {@link #errors()} is non-empty
   */

  JCGLArrayObjectType arrayObject()
    throws IllegalStateException;

  /**
   * @return The loaded array buffer
   *
   * @throws IllegalStateException Iff {@link #errors()} is non-empty
   */

  JCGLArrayBufferType arrayBuffer()
    throws IllegalStateException;

  /**
   * @return The loaded index buffer
   *
   * @throws IllegalStateException Iff {@link #errors()} is non-empty
   */

  JCGLIndexBufferType indexBuffer()
    throws IllegalStateException;
}
