/*
 * SSIPCommunicationException.java
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
 * Signals a communication error, for example, if a connection cannot be established or there was some problem 
 * transmitting a message.
 * 
 * @author ragb
 * 
 */
public class SSIPCommunicationException extends SSIPException {
  /**
   * 
   */
  private static final long serialVersionUID = 6408408425113231878L;

  /**
   * Constructs a SSIPCommunicationException. 
   */
  public SSIPCommunicationException () {
    // TODO Auto-generated constructor stub
  }

  /**
   * Constructs a SSIPCommunicationException, given an apropriate message.
   * @param message The message
   */
  public SSIPCommunicationException (String message) {
    super(message);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param t
   */
  public SSIPCommunicationException (Throwable t) {
    super(t);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param message
   * @param t
   */
  public SSIPCommunicationException (String message, Throwable t) {
    super(message, t);
    // TODO Auto-generated constructor stub
  }
}
