/*
 * SSIPSynthesisVoice.java
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
 * This class represent a specific synthesizer foice information. It is used to get and set specific synth voices bepassing speech-dispatcher naming convenctions. See section 4.5 of ssip documentation for mor information.
 * 
 * @author ragb
 * 
 * @see SSIPClient#setSynthesisVoice(String)
 * @see SSIPClient#getSynthesisVoices()
 */
public class SSIPSynthesisVoice {
	
  /**
 * voice name acording to the specific synthesizer
 */
private String _name;

  /**
 * two letter language code  
 */
private String _language;
  
  /**
 * voice variant (only god knows what is this)
 */
private String _variant;

  /**
   * Constructs a new {@code SSIPSynthesisVoice} instance with voice name, language code and variant
   * 
   * @param name the voice's name
   * @param language the language code
   * @param variant the voice's variant
   */
 protected SSIPSynthesisVoice (String name, String language, String variant) {
    _name = name;
    _language = language;
    _variant = variant;
  }

  /**
   * gets the voice's language two letter code.
   * @return the language
   */
  public String getLanguage () {
    return _language;
  }

  /**
   * Gets the voice name
   * @return the name
   */
  public String getName () {
    return _name;
  }

  /**
   * Gets the voice variant
   * @return the variant
   */
  public String getVariant () {
    return _variant;
  }

  /**
   * Gets a String representation of this {@code SSIPSynthesisVoice} instance.
 * @see java.lang.Object#toString()
 */
public String toString () {
    return getName() + " " + getLanguage() + " " + getVariant();
  }
}
