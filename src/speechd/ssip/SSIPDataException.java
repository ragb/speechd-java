/*
 * SSIPDataException.java
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
 * Thrown if server responds with some kind of error when client sends data (a String to be spoken).
 * 
 * @author ragb
 * 
 */
public class SSIPDataException extends SSIPException {
  /**
   * 
   */
  private static final long serialVersionUID = 4516728009407211407L;
  /**
   * The data which is meant to be sent.
   */
  private String _data;
  /**
   * The response from server.
   */
  private SSIPResponse _response;

  /**
   * Constructs a SSIPDataException, given the data which is meant to be sent and the response from server.  
   * @param data The data
   * @param response The response
   */
  public SSIPDataException (String data, SSIPResponse response) {
    _data = data;
    _response = response;
  }

  /**
   * Gets the data.
   * @return the data
   */
  public String getData () {
    return _data;
  }

  /**
   * Gets the response from server.
   * @return the response
   */
  public SSIPResponse getResponse () {
    return _response;
  }
}
