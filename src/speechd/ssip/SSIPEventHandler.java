/*
 * SSIPEventHandler.java
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
 *   IMplementors of this interface can receive SSIP event notifications from a SSIP server, due to various speech events.
 *   <p>SSIPEventHandlers must be registed with a {@link SSIPClient} or <@link SSIPConnection} to receive events, and the event notification must be turned on.
 * <p>Clients of this API could implement this interface to suit their needs but must take in acount some issues with multi threading and integration:
 * <ul>
 * <li>Due to the way communications are handled, event notifications are processed on the communications thread. It implies that event handling must be done quickly and no further SSIP communication are allowed.
 * <li>We could have provided a more complex event handling mechanism, but it would possibly prevent good implementations in some contexts. This way clients are responsible for integrating SSIP event handling with there loops (swing, swt) or any other possible use.
 *</ul>
 * 
 * 
 * @author ragb
 * 
 * @see SSIPClient#setEventHandler(SSIPEventHandler)	
 * @see SSIPConnection#setEventHandler(SSIPEventHandler)
 * @see SSIPEvent
 */
public interface SSIPEventHandler {
  /**
   * Handles an event.
   * @param event The event to be handled.
   */
  void handleSSIPEvent (SSIPEvent event);
}
