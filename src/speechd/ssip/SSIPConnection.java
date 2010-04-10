/*
 * SSIPConnection.java
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Instances of this class manage the lower details of SSIP protocol
 * communication, hidding almost all the socket communication and flat text
 * format of commands and responses.
 * 
 * <p>
 * With <code>SSIPConnection</code> clients can send command and receive
 * responses using plain java objects instead of plain SSIP commands and control
 * sequences. Sending data is also provided with all the needed escaping handled
 * transparentrly. Objects of this class <b>doesn't</b> send any SSIP commands
 * for themselves, even when connecting or disconnecting. Clients are
 * responssible for all needed initialization and finalization. This way SSIP
 * semantics are decoupled from SSIP comunication.<br/>
 * <code>SSIPConnection</code> instances parse and dispatch SSIP events when
 * active, see {@link SSIPConnection#setEventHandler} to define the
 * {@link SSIPEventHandler} and {@link SSIPEventParser} for more information.
 * Note that all exceptions not catched in the event callback will be discarded.
 * The event dispatching is processed in SSIPConnection's communications thread,
 * no complicated stuff are allowed nor sending of SSIP commands. Effects for
 * this practice are undefined. Clients are responsible for sending the specific
 * commands to activate and deactivate SSIP events.<br/> The
 * {@link SSIPConnection#sendCommand} and {@link SSIPConnection#sendData}
 * methods are synchronized on the connection to avoid concurrency problems,
 * when sending a command or data, no other thread can send something before the
 * first gets its response.<br/> All methods that do input/output can throw
 * subclasses of {@link SSIPException} on error. For example
 * {@link SSIPConnection#sendCommand} can throw a {@link SSIPCommandException}
 * when the response code is not on the 200-299 range, as defined in SSIP
 * documentation.
 * </p>
 * 
 * <p>
 * <code>SSIPConnection</code> instances should be used just to help
 * implementing higher level SSIP cleents, using it directly requires knoledge
 * of all SSIP details.
 * 
 * <h3>note</h3>
 * If in the future SSIP changes for another kind of communication (d-bus,
 * pypes, ...) we could extract this class's interface and create another
 * connection types, requiring no or few changes in clients.
 * 
 * @see SSIPCommand
 * @see SSIPResponse
 * @see SSIPEventHandler
 * @see SSIPEvent
 * @see SSIPClient
 * @see SSIPException
 * @see <a href="http://www.freebsoft.org/doc/speechd/ssip.html#Top">The SSIP *
 *      manual< /a>
 * 
 * @author ragb
 * 
 */
public class SSIPConnection {
	/**
	 * SSIP communications task. It reads events/responses from the outer
	 * <code<SSIPConnection</code>'s socket, parse them and synchronizes with
	 * the main thread.
	 * 
	 * @author ragb
	 * 
	 */
	private class InputThread implements Runnable {
		public void run() {
			while (SSIPConnection.this.isConnected()) {
				java.util.List<String> data = new LinkedList<String>();
				int code;
				// read lines
				try {
					while (_reader.ready()) {
						String line = readLine();
						assert (line.length() >= 4 && (line.charAt(3) == '-' || line
								.charAt(3) == ' '));
						char c = line.charAt(3);
						code = Integer.parseInt(line.substring(0, 3));
						if (c == ' ') {
							SSIPResponse res;
							String msg = line.substring(4);
							if (data.isEmpty())
								res = new SSIPResponse(code, msg);
							else
								res = new SSIPResponse(code, msg, data);
							dispatch(res);
						} else if (c == '-') {
							data.add(line.substring(4));
						}
					}
					Thread.yield();
				} catch (IOException e) {
					Thread.currentThread().interrupt();
					SSIPConnection.this.disconnect();
				} catch (InterruptedException e) {
					SSIPConnection.this.disconnect();
				}
			}
		}
	}

	/**
	 * end of line sequence
	 */
	static private final String CRLF = "\r\n";
	/**
	 * SSIP end of data sequence
	 */
	static private final String END_OF_DATA = CRLF + "." + CRLF;
	/**
	 * socket used to comunicate with SSIP server
	 */
	private Socket _socket;
	/**
	 * reader to read characters from the socket instead of bytes
	 */
	private BufferedReader _reader;
	/**
	 * writer to write characters to the socket instead of plain bytes
	 */
	private BufferedWriter _writer;
	/**
	 * server host
	 */
	private String _host;
	/**
	 * SSIP server port
	 */
	private int _port;
	/**
	 * flag to see if this <code>SSIPConnection</code> is connected, it is used
	 * to stop comunications thread smoothly.
	 */
	private boolean _connected;
	/**
	 * variable to hold the Response wich the communications thread parsed and
	 * the main thread wants to get.
	 */
	private SSIPResponse _currentResponse;
	/**
	 * Thread to run the communications task.
	 */
	private Thread _thread;
	/**
	 * object wich handles events received from the server
	 */
	private SSIPEventHandler _eventHandler = null;

	/**
	 * logger object used by this connection to log activities.
	 */
	private Logger _logger = Logger.getLogger("speechd.ssip.SSIPConnection");

	/**
	 * Constructs a new <code>SSIPConnection</code> wich will connect to a host
	 * and port. ON creation the instance is disconnected.
	 * 
	 * @param host
	 *            the SSIP server host where to connect
	 * @param port
	 *            The SSIP server port
	 */
	public SSIPConnection(String host, int port) {
		_host = host;
		_port = port;
		_connected = false;
		_socket = null;
		_logger.log(Level.FINEST, "created connection");
	}

	/**
	 * Connects to the SSIP server, initializing socket and communications
	 * handling.
	 * 
	 * @throws SSIPException
	 *             when a network error is found initializing connections.
	 */
	public void connect() throws SSIPException {
		try {
			_socket = new Socket(_host, _port);
			_logger.log(Level.INFO, String.format("connected to %s port %d",
					_host, _port));
			_socket.setTcpNoDelay(true);
			_reader = new BufferedReader(new InputStreamReader(_socket
					.getInputStream()));
			_writer = new BufferedWriter(new java.io.OutputStreamWriter(_socket
					.getOutputStream()));

		} catch (IOException e) {
			_logger.log(Level.SEVERE, String.format(
					"I/O error connecting to %s port %d: %s", _host, _port, e
							.getMessage()));
			throw new SSIPCommunicationException("can't connect to host");
		}
		_connected = true;
		_thread = new Thread(new InputThread());
		_thread.setDaemon(true);
		_thread.start();
		_logger.log(Level.INFO, "started communications thread");
	}

	/**
	 * Disconnects from the SSIP server.
	 */
	public void disconnect() {
		if (!_connected)
			return;
		_connected = false;
		try {
			_logger.log(Level.FINE, "joining communications thread");
			_thread.join();
			_socket.close();
			_logger.info("disconnected from host");
		} catch (InterruptedException e) {
			// don't care....
			_logger.log(Level.WARNING,
					"interrupted exception when disconnecting", e);
		} catch (IOException e) {
			// we tryed but...
			_logger.log(Level.WARNING, "I/O exception when disconnecting", e);
		} finally {
			_thread = null;
			_socket = null;
		}
	}

	/**
	 * Sends a command to the SSIP server and returns server response when no
	 * errors are found.
	 * 
	 * @see SSIPCommand
	 * 
	 * @param command
	 *            the command to send
	 * @return the server response
	 * @throws SSIPCommandException
	 *             if server returns an error response
	 * @throws SSIPCommunicationException
	 *             when a communication error arrives when talking to server or
	 *             connection is not established.
	 */
	public synchronized SSIPResponse sendCommand(SSIPCommand command)
			throws SSIPCommandException, SSIPCommunicationException {
		if (!_connected)
			throw new SSIPCommunicationException("not connected to server");
		try {
			_logger.log(Level.FINE, "Sending command %s", command.toString());
			_writer.write(command.toString());
			_writer.write(CRLF);
			_writer.flush();
			_logger.log(Level.FINE, "command sent");
			SSIPResponse res = null;
			_logger.fine("receiving response");
			while (_currentResponse == null) {
				wait();
				res = _currentResponse;
				_logger.fine(String.format("Received response %s", res));
			}
			_currentResponse = null;
			if (res.getCode() / 100 != 2) {// code not in 200-299
				_logger.warning(String.format(
						"Error code %d returned from server", res.getCode()));
				throw new SSIPCommandException(command, res);
			}
			return res;
		} catch (IOException e) {
			_logger.log(Level.SEVERE, "I/O when sending command", e);
			disconnect();
			throw new SSIPCommunicationException("disconnected from server");
		} catch (InterruptedException e) {
			_logger.log(Level.SEVERE,
					"Communications thread interrupted when sending command");
			disconnect();
			throw new SSIPCommunicationException(e);
		}
	}

	/**
	 * Sends data over this <code>SSIPConnection</code> and returns the server
	 * response if no errors are found. Data will be escaped aconrding to SSIP
	 * escaping rules.
	 * g
	 * @param data
	 *            the data string to send
	 * @return the server response
	 * @throws SSIPDataException
	 *             if server returns an error
	 * @throws SSIPCommunicationException
	 *             if a communication error arises. or is not connected.
	 */
	public synchronized SSIPResponse sendData(String data)
			throws SSIPDataException, SSIPCommunicationException {
		if (!_connected)
			throw new SSIPCommunicationException("not connected to server");
		String dataEscaped = escapeData(data);
		try {
			_writer.write(dataEscaped);
			_writer.write(END_OF_DATA);
			_writer.flush();
			// get response:
			SSIPResponse res = null;
			while (_currentResponse == null) {
				wait();
				res = _currentResponse;
			}
			_currentResponse = null;
			if (res.getCode() / 100 != 2) // error:
				throw new SSIPDataException(data, res);
			return res;
		} catch (IOException e) {
			disconnect();
			throw new SSIPCommunicationException(e);
		} catch (InterruptedException e) {
			disconnect();
			throw new SSIPCommunicationException(e);
		}
	}

	/**
	 * escapes data for sending to SSIP server acording to SSIP rules
	 * 
	 * @param data
	 *            the data
	 * @return the escaped version of data
	 */
	private String escapeData(String data) {
		String escaped = data;
		if (escaped.startsWith("."))
			escaped = "." + escaped;
		escaped = escaped.replaceAll(CRLF + ".", CRLF + "..");
		return escaped;
	}

	/**
	 * Dispatches a response for event handling or for client direct processing
	 * acconrding to response code. this method is called be the communications
	 * thread, notifying the main one when event is meant to direct processing.
	 * 
	 * @param response
	 *            the response do dispatch
	 * @throws InterruptedException
	 *             if communications thread is interrupted
	 */
	private void dispatch(SSIPResponse response) throws InterruptedException {
		if (response.getCode() / 100 == 7) {// we've got an SSIP event
			if (_eventHandler != null) {
				try {
					_eventHandler.handleSSIPEvent(SSIPEventParser.getInstance()
							.parse(response));
				} catch (Exception e) {
					_logger.severe(String.format(
							"Exception in user callback: %s\n%s", e
									.getLocalizedMessage(), e.getStackTrace()));
				}
			}
		} else {
			// normal response
			synchronized (this) {
				_currentResponse = response;
				notify();
			}
		}
	}

	/**
	 * Reads a line from the socket according to SSIP end of line conventions.
	 * 
	 * @return the line read without the ending cr/lf pair.
	 * @throws IOException
	 *             if an io error ocurs.
	 */
	private String readLine() throws IOException {
		// thanks Sérgio Neves for this one
		StringBuilder sb = new StringBuilder();
		char c1 = (char) _reader.read(), c2 = (char) _reader.read();
		while (c1 != '\r' || c2 != '\n') {
			sb.append(c1);
			c1 = c2;
			c2 = (char) _reader.read();
		}
		_logger.finest(String.format("Read line %s", sb.toString()));
		return sb.toString();
	}

	/**
	 * Gets the connected state of this <code>SSIPConnection</code>
	 * 
	 * @return <code>true</code> if connected, <code>false</code> if
	 *         disconnected.
	 */
	public boolean isConnected() {
		return _connected;
	}

	/**
	 * Gets the <code>SSIPEventHnadler</code> wich are receiving events from
	 * this connection, if one exists.
	 * 
	 * @return the eventHandler if defined, <code>null</code> if not.
	 */
	public SSIPEventHandler getEventHandler() {
		return _eventHandler;
	}

	/**
	 * Sets the <code>SSIPEventHandler</code> to handle events comming from the
	 * server associated with this connection.
	 * 
	 * @param eventHandler
	 *            the eventHandler to set
	 */
	public void setEventHandler(SSIPEventHandler eventHandler) {
		_eventHandler = eventHandler;
	}
}
