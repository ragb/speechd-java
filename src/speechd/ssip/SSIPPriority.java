/*
 * SSIPPriority.java
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
 * This enum models messages' priority, which can take one of the following values:
 * <ul><li>IMPORTANT</li> <li>MESSAGE</li> <li>TEXT</li> <li>NOTIFICATION</li> <li>PROgRESS</li></ul>
 * <p>Clients use this enum's various values to specify what priority to use with speech commands.
 * See section 4.3 of SSIP specification to get details of SSIP priority model.
 * 
 * 
 * @author ragb
 * 
 * @see SSIPClient#say(SSIPPriority, String)
 * @see SSIPClient#sayChar(SSIPPriority, char)
 * @see SSIPClient#sayKey(SSIPPriority, String)
 * @see SSIPClient#setPriority(SSIPPriority)
 */
public enum SSIPPriority {
  IMPORTANT, MESSAGE, TEXT, NOTIFICATION, PROgRESS;
}
