/*
 * SSIPResponse.java
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

import java.util.Collections;
import java.util.List;

/**
 * Class to represent responses sent by SSIP servers on request to commands.
 * Responses have a return code and associated message, and have a possible list
 * of data strings returned by the server.
 * 
 * @author ragb
 * 
 * @see SSIPCommand
 * @see SSIPConnection#sendCommand(SSIPCommand)
 * @see SSIPConnection#sendData(String)
 */
public class SSIPResponse {
  /**
   * Return code
   */
  private int _code;
  /**
   * human readable message
   */
  private String _msg;
  /**
   * response data
   */
  private List<String> _data;

  /**
   * constructs a new SSIPResponse with code, message and data
   * 
   * @param code
   *          the return code
   * @param msg
   *          the human readable message
   * @param data
   *          the response data
   */
  SSIPResponse (int code, String msg, List<String> data) {
    this(code, msg);
    _data = data;
  }

  /**
   * Constructs a new SSIPResponse with code, message and no associated data. In
   * this case data is null
   * 
   * @param code
   *          the return code
   * @param msg
   *          the human readable message
   */
  public SSIPResponse (int code, String msg) {
    _code = code;
    _msg = msg;
  }

  /**
   * Gets the return code.
   * 
   * @return the code
   */
  public int getCode () {
    return _code;
  }

  /**
   * Gets the data associated with this SSIPResponse as an unmodifiable list.
   * When no data is associated {@code null} is returned
   * 
   * @return the data or {@code null} if there is no data.
   */
  public List<String> getData () {
    return (_data != null) ? Collections.unmodifiableList(_data) : null;
  }

  /**
   * Gets the human readable message for this SSIPResponse.
   * 
   * @return the message
   */
  public String getMsg () {
    return _msg;
  }
}
