/*
 * SSIPConnectionTest.java
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
package speechd.tests;

import speechd.ssip.SSIPCommand;
import speechd.ssip.SSIPCommandException;
import speechd.ssip.SSIPConnection;
import speechd.ssip.SSIPException;
import speechd.ssip.SSIPResponse;
import junit.framework.TestCase;

/**
 * 
 * 
 * @author ragb
 * 
 */
public class SSIPConnectionTest extends TestCase {
  private SSIPConnection _connection;

  /**
   * Test method for {@link speechd.ssip.SSIPConnection#connect()}.
   */
  public void testConnect () {
    try {
      _connection = new SSIPConnection("localhost", 6560); // 6560 meaning
                                                            // default port.
      _connection.connect();
      assertTrue(_connection.isConnected());
    } catch (SSIPException e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for {@link speechd.ssip.SSIPConnection#disconnect()}.
   */
  public void testDisconnect () {
    try {
      _connection = new SSIPConnection("localhost", 6560); // 6560 meaning
                                                            // default port.
      _connection.connect();
      _connection.disconnect();
      assertFalse(_connection.isConnected());
    } catch (SSIPException e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link speechd.ssip.SSIPConnection#sendCommand(speechd.ssip.SSIPCommand)}.
   */
  public void testSendCommand () {
    try {
      _connection = new SSIPConnection("localhost", 6560); // 6560 meaning
                                                            // default port.
      _connection.connect();
      SSIPResponse res = _connection.sendCommand(new SSIPCommand("set", "self",
          "client_name", "test:test:test"));
      assertEquals(res.getCode() / 100, 2);
      // try an invalid command:
      try {
        _connection.sendCommand(new SSIPCommand("error", "stuff"));
        fail("it should have thrown an exception");
      } catch (SSIPCommandException e) {
        // fine here...
      }
      _connection.sendCommand(new SSIPCommand("quit"));
      _connection.disconnect();
    } catch (SSIPException e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link speechd.ssip.SSIPConnection#sendData(java.lang.String)}.
   */
  public void testSendData () {
    try {
      _connection = new SSIPConnection("localhost", 6560); // 6560 meaning
                                                            // default port.
      _connection.connect();
      _connection.sendCommand(new SSIPCommand("set", "self", "CLIENT_NAME",
          "test:test:test"));
      _connection.sendCommand(new SSIPCommand("speak"));
      _connection.sendData("this is a test");
      _connection.sendCommand(new SSIPCommand("quit"));
      _connection.disconnect();
    } catch (SSIPException e) {
      fail(e.getMessage());
    }
  }
}
