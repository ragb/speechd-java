/*
 * SSIPEventParser.java
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

import java.util.Map;

/**
 * This utility class is a kind of factory for events, it creates {@link SSIPEvent} instances from {@link SSIPResponse}. This is a singletone, see {@link SSIPEventParser#getInstance()} to get the single instance.
 * 
 * @author ragb
 * 
 * @see SSIPEvent
 */
public final class SSIPEventParser {
  
	/**
	 * The single instance
	 */
	private static SSIPEventParser _instance = null;
	
  /**
 * Map with event codes to event types:
 */
private static Map<Integer, SSIPEvent.EventType> _eventCodes = new java.util.HashMap<Integer, SSIPEvent.EventType>();
  // map event codes to event types:
  static {
    _eventCodes.put(700, SSIPEvent.EventType.INDEX_MARK);
    _eventCodes.put(701, SSIPEvent.EventType.BEGIN);
    _eventCodes.put(702, SSIPEvent.EventType.END);
    _eventCodes.put(703, SSIPEvent.EventType.CANCEL);
    _eventCodes.put(704, SSIPEvent.EventType.PAUSE);
    _eventCodes.put(705, SSIPEvent.EventType.RESUME);
  }

  /**
 * constructs a new SSIPEventParser
 */
private SSIPEventParser () {
  }

  /**
   * gets the single instance of SSIPEventParser
 * @return the single instance
 */
static SSIPEventParser getInstance () {
    if (_instance == null)
      _instance = new SSIPEventParser();
    return _instance;
  }

  /**
   * Parses a {@code SSIPEvent} from a <@code SSIPResponse}
 * @param response the response
 * @return the <@code SSIPEvent} parsed from the {@code response}
 */
SSIPEvent parse (SSIPResponse response) {
    assert (response.getData().size() == 2 || response.getData().size() == 3);
    SSIPEvent.EventType type = _eventCodes.get(response.getCode());
    assert (type != null);
    int msgId = Integer.parseInt(response.getData().get(0));
    int clientId = Integer.parseInt(response.getData().get(1));
    // when we have an index mark event we must construct it with one more
    // parameter
    if (type == SSIPEvent.EventType.INDEX_MARK)
      return new SSIPEvent(type, msgId, clientId, response.getData().get(2));
    // no index mark:
    return new SSIPEvent(type, msgId, clientId);
  }
}
