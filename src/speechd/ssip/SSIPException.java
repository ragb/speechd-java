/*
 * SSIPException.java
 *
 * Copyright (C) 2008, 2010 Rui Batista <rui.batista@ist.utl.pt>
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this package; see the file COPYING.  If not, write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 */
package speechd.ssip;

/**
 * A generic exception that denotes some kind of network or communication error
 * 
 * @author ragb
 * 
 */
public class SSIPException extends Exception {
  /**
   * 
   */
  private static final long serialVersionUID = -7491952921596029302L;

  /**
   * Constructs a SSIPException  
   */
  protected SSIPException () {
  }

  /**
   * Constructs a SSIPException, given an apropriate message.
   * @param message The message
   */
  protected SSIPException (String message) {
    super(message);
  }

  /**
   * @param t
   */
  protected SSIPException (Throwable t) {
    super(t);
  }

  /**
   * @param message
   * @param t
   */
  protected SSIPException (String message, Throwable t) {
    super(message, t);
  }
}
