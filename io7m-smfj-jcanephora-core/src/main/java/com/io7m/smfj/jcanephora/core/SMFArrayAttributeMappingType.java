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

import com.io7m.jcanephora.core.JCGLScalarType;
import com.io7m.smfj.core.SMFAttributeName;
import com.io7m.smfj.core.SMFImmutableStyleType;
import org.immutables.value.Value;

import java.util.Optional;

/**
 * A mapping from a named SMF attribute to a <tt>jcanephora</tt> array buffer
 * attribute.
 */

@SMFImmutableStyleType
@Value.Immutable
public interface SMFArrayAttributeMappingType
{
  /**
   * @return The name of the SMF attribute
   */

  @Value.Parameter
  SMFAttributeName name();

  /**
   * @return The integer index of the array attribute
   */

  @Value.Parameter
  int index();

  /**
   * A specification of the type of individual components in the array buffer
   * attribute. If no type is given, the type is inferred from the matching SMF
   * attribute.
   *
   * @return The type of attribute components
   */

  @Value.Parameter
  Optional<JCGLScalarType> componentType();

  /**
   * @return The number of components in each element
   */

  @Value.Parameter
  int componentCount();
}
