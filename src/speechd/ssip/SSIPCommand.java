/*
 * SSIPCommand.java
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
 * The SSIP command to be sent.
 * 
 * @author ragb
 * 
 */
public class SSIPCommand {
  /**
   * The command itself
   */
  private String _cmd;
  /**
   * The command arguments
   */
  private String[] _args;

  /**
   * Constructs a SSIPCommand, given the command and the various command arguments. 
   * @param command The command itself. 
   * @param args The command arguments  
   */
  public SSIPCommand (String command, String... args) {
    this(command);
    _args = args;
  }

  /**
   * Constructs a SSIPCommand without arguments.
   * @param command The command itself.
   */
  public SSIPCommand (String command) {
    _cmd = command;
  }

  /**
   * The command of this SSIPCommand.
   * @return The command itself.
   */
  public String getCommand () {
    return _cmd;
  }

  /**
   * The command arguments of this SSIPCommand.
   * @return The command arguments.
   */
  public String[] getArgs () {
    return _args;
  }

  /**
   * The representation of this SSIPCommand.
   * @return The representation of this SSIPCommand. 
   */
  public String toString () {
    StringBuilder sb = new StringBuilder();
    sb.append(getCommand());
    if (getArgs() != null) {
      for (int i = 0; i < getArgs().length; ++i) {
        sb.append(' ');
        sb.append(getArgs()[i]);
      }
    } else {
      sb.append(' ');
    }
    return sb.toString();
  }
}
