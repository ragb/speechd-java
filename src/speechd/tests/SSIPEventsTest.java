/*
 * SSIPEventsTest.java
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

import java.util.concurrent.BlockingQueue;
import speechd.ssip.SSIPClient;
import speechd.ssip.SSIPEvent;
import speechd.ssip.SSIPEventHandler;
import speechd.ssip.SSIPException;
import speechd.ssip.SSIPPriority;
import junit.framework.TestCase;

/**
 * 
 * 
 * @author ragb
 * 
 */
public class SSIPEventsTest extends TestCase implements SSIPEventHandler {
  private SSIPClient _client;
  private BlockingQueue<SSIPEvent> _queue;

  /**
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp () throws Exception {
    super.setUp();
    _client = new SSIPClient("test", "test", "test");
    _client.setEventHandler(this);
    _queue = new java.util.concurrent.ArrayBlockingQueue<SSIPEvent>(1);
  }

  /**
   * @see junit.framework.TestCase#tearDown()
   */
  protected void tearDown () throws Exception {
    super.tearDown();
    _client.close();
  }

  public void testBegin () {
    try {
      _client.setNotification(true, SSIPEvent.EventType.BEGIN);
      _client.say(SSIPPriority.MESSAGE, "test begin");
      SSIPEvent e = _queue.take();
      assertEquals(e.getType(), SSIPEvent.EventType.BEGIN);
      _client.setNotification(false, SSIPEvent.EventType.BEGIN);
    } catch (SSIPException e) {
      fail();
    } catch (InterruptedException e) {
      fail();
    }
  }

  /**
   * @see speechd.ssip.SSIPEventHandler#handleSSIPEvent(speechd.ssip.SSIPEvent)
   */
  public void handleSSIPEvent (SSIPEvent event) {
    _queue.add(event);
  }
}
