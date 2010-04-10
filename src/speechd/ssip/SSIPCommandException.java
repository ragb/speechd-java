/*
 * SSIPCommandException.java
 *
 * Copyright (C) 2008 Rui Batista <rui.batista@ist.utl.pt>
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this package; see the file COPYING.  If not, write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 */
package speechd.ssip;

/**
 * 
 * 
 * @author ragb
 * 
 */
/**
 * This exception is thrown when there was a response error from server corresponding 
 * to a SSIP command sent (the code of server's response was not in 200-299 range).
 * 
 * @author ragb
 * 
 */
public class SSIPCommandException extends SSIPException {
  /**
   * 
   */
  private static final long serialVersionUID = 3391141636275637224L;
  /**
   * The SSIPCommand.  
   */
  private SSIPCommand _command;
  /**
   * The response from server that corresponds to this SSIPCommand.
   */
  private SSIPResponse _response;

  /**
   * Constructs a SSIPCommandException, given the SSIPCommand sent and the response from server. 
   * @param command The SSIPCommand
   * @param response The response received.
   */
  public SSIPCommandException (SSIPCommand command, SSIPResponse response) {
    _command = command;
    _response = response;
  }

  /**
   * Gets the SSIPCommand associated with this SSIPCommandException.
   * @return the command
   */
  public SSIPCommand getCommand () {
    return _command;
  }

  /**
   * gets the response associated with this SSIPCommandException.
   * @return the response
   */
  public SSIPResponse getResponse () {
    return _response;
  }
}
