/*
 * SSIPEvent.java
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
 * Instances of this class represent SSIP events like defined on section 4.7 of SSIP specifications.
 * SSIP events have a type (see {@link SSIPEvent.EventType}), an associated message id (message to wich belongs the event), client id (id of the associated connection) and an optional index mark string, valid only for <code>INDEXMARK</code> event type.
 * SSIPEvents are usually constructed by an instance of <@link SSIPEventParser}.
 * {@link SSIPEventHandler} receive instances of SSIPEvent when notified of speech events.
 * 
 * @author ragb
 * 
 * @see SSIPEventHandler
 * @see SSIPEventParser
 */
public class SSIPEvent {
  public enum EventType {
    INDEX_MARK, BEGIN, END, CANCEL, PAUSE, RESUME
  }

  private EventType _type;
  private int _msgId;
  private int _clientId;
  private String _indexMark = null;

  /**
   * @param type
   * @param msgId
   * @param clientId
   */
  public SSIPEvent (EventType type, int msgId, int clientId) {
    _type = type;
    _msgId = msgId;
    _clientId = clientId;
  }

  /**
   * @param type
   * @param msgId
   * @param clientId
   * @param indexMark
   */	
  public SSIPEvent (EventType type, int msgId, int clientId, String indexMark) {
    this(type, msgId, clientId);
    _indexMark = indexMark;
  }

  /**
   * @return the clientId
   */
  public int getClientId () {
    return _clientId;
  }

  /**
   * @return the indexMark
   */
  public String getIndexMark () {
    return _indexMark;
  }

  /**
   * @return the msgId
   */
  public int getMsgId () {
    return _msgId;
  }

  /**
   * @return the type
   */
  public EventType getType () {
    return _type;
  }
}
