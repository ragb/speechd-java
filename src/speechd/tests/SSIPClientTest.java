/*
 * SSIPClientTest.java
 *
 * Copyright (C) 2008, 2010 Rui Batista <rui.batista@ist.utl.pt>
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2.1, or (at your option)
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

import java.util.List;
import speechd.ssip.SSIPClient;
import speechd.ssip.SSIPCommandException;
import speechd.ssip.SSIPException;
import speechd.ssip.SSIPPriority;
import speechd.ssip.SSIPSynthesisVoice;
import junit.framework.TestCase;

/**
 * 
 * 
 * @author ragb
 * 
 */
public class SSIPClientTest extends TestCase {
  SSIPClient _client;

  /**
   * @param arg0
   */
  public SSIPClientTest (String arg0) {
    super(arg0);
  }

  /**
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp () throws Exception {
    super.setUp();
    _client = new SSIPClient("junit", null, null);
  }

  /**
   * @see junit.framework.TestCase#tearDown()
   */
  protected void tearDown () throws Exception {
    super.tearDown();
    _client.close();
  }

  /**
   * 
   */
  public void testSay () {
    try {
      _client.say(SSIPPriority.TEXT,
          "this is the client say method test. this one should not be spoken");
      _client.say(SSIPPriority.IMPORTANT, "this is important");
      _client.say(SSIPPriority.MESSAGE, "this is a message");
      _client.say(SSIPPriority.TEXT, "a litle text to say...");
    } catch (SSIPException e) {
      fail();
    }
  }

  public void testSayFormated () {
    try {
      _client.sayFormated(SSIPPriority.TEXT, "this thing is testing with %s",
          "junit");
    } catch (SSIPException e) {
      fail("say formated");
    }
  }

  public void testGetClientId () {
    int id = _client.getClientId();
    assertTrue(id > 0);
  }

  public void testStop () {
    try {
      _client.stop();
      _client.setTarget(SSIPClient.Target.ALL);
      _client.stop();
      _client.setTarget(_client.getClientId());
      _client.stop();
      _client.setTarget(SSIPClient.Target.SELF);
    } catch (SSIPException e) {
      fail("stop test.");
    }
  }

  public void testSetParameters () {
    try {
      _client.setVolume(0);
      _client.say(SSIPPriority.MESSAGE, "this is in a lower volume");
      _client.setVolume(100);
      _client.say(SSIPPriority.MESSAGE, "this is the highest");
      _client.setRate(100);
      _client.say(SSIPPriority.MESSAGE, "this is fast");
      _client.setRate(0);
      _client.setPitch(50);
      _client.say(SSIPPriority.MESSAGE, "this is like girls");
    } catch (SSIPCommandException e) {
      fail("" + e.getResponse().getCode());
    } catch (SSIPException e) {
      fail();
    }
  }

  public void testListings () {
    try {
      List<String> data;
      data = _client.getOutputModules();
      for (String s : data) {
    	  if(s == "dummy") continue; //Not for this one...:)
        _client.setOutputModule(s);
        _client.sayFormated(SSIPPriority.MESSAGE, "speaking from module %s", s);
      }
      data = _client.getVoices();
      for (String s : data) {
        _client.setVoice(s);
        _client.sayFormated(SSIPPriority.MESSAGE, "this voice %s", s);
      }
      for (SSIPSynthesisVoice v : _client.getSynthesisVoices()) {
        _client.sayFormated(SSIPPriority.MESSAGE, "Synth voice %s", v
            .toString());
      }
    } catch (SSIPException e) {
      fail();
    }
  }
}
