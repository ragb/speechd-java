/*
 * SSIPClient.java
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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Basic high level, java interface to SSIP servers like speech-dispatcher.
 *  This is the recomended way to communicate with SSIP servers in java, almost all SSIP commands are supported directly. A {@code SSIPClient} instance represents a connection to one SSIP server, it can be used for speaking, changing speech parameters, etc... without knowning about SSIP protocole details, altough familiarity with SSIP concepts is useful.
 *  When this class doesn't provide the needed functionality, clients can subclass it and/or use {@link SSIPConnection} objects to send and receive raw SSIP data.<br/>
 *  SSIP events are also supported, see the {@link SSIPEventHandler} interface, {@link SSIPClient#setNotification(boolean)}, {@link SSIPClient#setNotification(boolean, speechd.ssip.SSIPEvent.EventType)} and {@link SSIPClient#setEventHandler(SSIPEventHandler)} methods.
 *  A {@code SSIPClient} instance should be used just for one connection, when closed it must be discarded.
 *  We recomend closing connections after using them, it will free network resources earlier.
 *  This class is safe for multi threaded use.
 *  All activities are logged to the {@code speechd.ssip.SSIPClient} logger (java logging api).
 *    
 *    <h4>notes</h4>
 *  <p>This interface is based on the <a href="http://www.freebsoft.org/doc/speechd/speech-dispatcher_15.html#SEC60">python</a> and <a href="http://www.freebsoft.org/doc/speechd/speech-dispatcher_14.html#SEC44">c</a> speech-dispatcher bindings, methods and functionality are more java like though.</p>
 * 	<p>When connecting to the SSIP server the host and port parameters are not defined directly on object creation. Like in the C interface, the {@code SPEECHD_HOST} and {@code SPEECHD_PORT} environment variables are usedd to set this values.
 * In addiction, the {@code speechd.host} and {@code speechd.post} properties are checked before the environment variables. There are also default values for fallback when no properties or enviromment variables are set, see {@link SSIPClient#DEFAULT_HOST} and {@link SSIPClient#DEFAULT_PORT}.
 * The order (the same for host and port) from first choice to last is:
 * <ol>
 * <li>Java system properties</li>
 * <li>Environment variables</li>
 * <li>Default values</li>
 * </ol></p>
 * <p>the name, component and user values to the constructor have the same meaning as in speech-dispatcher documentation: see {@link SSIPClient#SSIPClient(String, String, String)} for explanation.</p>
 * <p>Message priorities are defined when calling speaking methods ({@link SSIPClient#say(SSIPPriority, String)}, {@link SSIPClient#sayChar(SSIPPriority, char)},...) and not directly, although SSIPClient subclasses are allowed to use the {@link SSIPClient#setPriority(SSIPPriority)} method.</p>
 * <p>Commands that need a target (like all parameter setting commands) need previous target definition, see @{link {@link SSIPClient#setTarget(Target)} and {@link SSIPClient.Target}. In most cases this feature should not be used, the SELF default is the only needed target most times.
 * 
 * <h4>Example:</h4>
 * <code><pre>
 * SSIPClient spd = new SSIPClient("myApplication", null, null);
 * spd.setVolume(100); //highest volume
 * spd.say(SSIPPriority.TEXT, "This is a message."); //speak a message
 * spd.close(); //close connection.
 * </pre></code>
 * 
 * @author ragb
 * 
 * @see SSIPPriority
 * @see SSIPConnection
 * @see SSIPEvent
 * @see SSIPEventHandler
 * @see SSIPException
 * @see <a href="http://www.freebsoft.org/doc/speechd">Speech-dispatcher documentation</a>
 * @see <a href="http://www.freebsoft.org/doc/speechd/ssip.html">SSIP documentation</a>
 */
public class SSIPClient {

	/**
	 * Enumeration that defines targets for parameter definition (Integer values are allowed for specific client ids).
	 * 
	 * @author ragb
	 * 
	 */
	public enum Target {
		SELF, ALL
	};

	/**
	 * Default host where SSIP server is running.
	 */
	public static final String DEFAULT_HOST = "localhost";

	/**
	 * Default port where SSIP server is running.
	 */
	public static final int DEFAULT_PORT = 6560;

	/**
	 * The {@link SSIPConnection} used to handle raw SSIP communication.
	 */
	private SSIPConnection _connection;

	/**
	 * Target to use when setting parameters, and some other stuff.
	 */
	private String _target;

	/**
	 * Client name
	 */
	private String _name;

	/**
	 * Client component
	 */
	private String _component;

	/**
	 * Connection's user name.
	 */
	private String _user;

	/**
	 * SSIP server host
	 */
	private String _host;

	/**
	 * Port where SSIP server is listening
	 */
	private int _port;

	/**
	 * Client id
	 */
	private int _clientId;

	/**
	 * LOGGER.
	 */
	private Logger _logger = Logger.getLogger("speechd.ssip.SSIPClient");

	/**
	 * Constructs a new {@code SSIPClient} and connects it to the SSIP server.
	 * For setting server host and port see explanation above.
	 * 
	 * @param name client name.
	 * @param component Component for this connection, if {@code null} the "main" default value will be used.
	 * @param user the client user name, if {@code null} the current user name will be used.
	 * @throws SSIPException when SSIP communication errors are enconterered when connecting to server.
	 */
	public SSIPClient (String name, String component, String user)
	throws SSIPException {
		if (name == null) {
			throw new NullPointerException("SSIP connection's name can't be null");
		} else {
			_name = name;
			_logger.fine(String.format("Defnining connection's name as %s", _name));
		}
		// when component is null assign "main" to it (like in c api):
		if (component == null) {
			_component = "main";
		} else {
			_component = component;
		}
		_logger.fine(String.format("Defining connection's component as %s", _component));

		// if user is null try to find the current user's name
		if (user == null) {
			_user = System.getProperty("user.name");
		} else {
			_user = user;
		}
		_logger.fine(String.format("Defining user as %s", _user));

		// Find host where spd is running:
		if ((_host = System.getProperty("speechd.host")) == null) {
			if ((_host = System.getenv("SPEECHD_HOST")) == null)
				_host = DEFAULT_HOST;
		}
		_logger.fine(String.format("Defining host as %s", _host));

		// Find default port:
		String portStr;
		if ((portStr = System.getProperty("speechd.port")) == null) {
			if ((portStr = System.getenv("SPEECHD_PORT")) != null)
				_port = Integer.parseInt(portStr);
			else
				_port = DEFAULT_PORT;
		}
		_logger.fine(String.format("Defining port as %d", _port));

		// create connection and connect it:
		_connection = new SSIPConnection(_host, _port);
		_connection.connect();
		_logger.info("connected to host");

		// set connection's name
		setTarget(Target.SELF);
		setParameter(_target, "client_name", makeFullName());
		_logger.fine("Client name set");

		// get client self id:
		SSIPResponse res = _connection.sendCommand(new SSIPCommand("HISTORY",
				"GET", "CLIENT_ID"));
		_clientId = getIntResponse(res);
		_logger.fine(String.format("Client id is %d", _clientId));
	}

	/**
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize () throws Throwable {
		super.finalize();
		close();
	}

	/**
	 * Closes smoothly  the connection freeing all resources.
	 * @throws SSIPException on SSIP Errors.
	 */
	public void close () throws SSIPException {
		if (_connection.isConnected()) {
			_connection.sendCommand(new SSIPCommand("quit"));
			_connection.disconnect();
			_logger.info("Disconnected from host");
		}
	}

	/**
	 *  Speaks a message with given priority.
	 * @param priority message priority
	 * @param text the message to speak
	 * @return imessage id of spoken message to be possibly used with callbacks.
	 * @throws SSIPException on SSIP errors.
	 * @see SSIPPriority
	 */
	public int say (SSIPPriority priority, String text) throws SSIPException {
		setPriority(priority);

		// send speak command:
		_logger.info(String.format("Saying message:\n%s", text));
		_connection.sendCommand(new SSIPCommand("speak"));
		// send data and extract msg id from the response:
		SSIPResponse response = _connection.sendData(text);
		int id =  getIntResponse(response);
		_logger.fine(String.format("Message id is %d", id));
		return id;
	}

	/**
	 * Speaks a formated message with given priority (utility method).
	 * @param priority message priority
	 * @param format format string like in {@link String#format(String, Object...)}
	 * @param args arguments to format
	 * @return the message id
	 * @throws SSIPException on SSIP errors.
	 * @see String#format(String, Object...)
	 * @see SSIPPriority
	 */
	public int sayFormated (SSIPPriority priority, String format, Object... args)
	throws SSIPException {
		return say(priority, String.format(format, args));
	}

	/**
	 * Speaks a character representation with given message.
	 * @param priority the priority
	 * @param c the character to speak
	 * @throws SSIPException on SSIP errors.
	 * @see SSIPPriority
	 */
	public void sayChar (SSIPPriority priority, char c) throws SSIPException {
		String s;
		setPriority(priority);
		if (c == ' ')
			s = "space";
		else
			s = Character.toString(c);

		_logger.info(String.format("Saying character %s", s));
		_connection.sendCommand(new SSIPCommand("CHAR", s));
	}

	/**
	 * Says given key-name representation. See <a href="http://www.freebsoft.org/doc/speechd/ssip_20.html#SEC36"<key names list</a> for possible key names.
	 * @param priority the priority
	 * @param key the key name
	 * @throws SSIPException on SSIP errors
	 * @see SSIPPriority
	 */
	public void sayKey (SSIPPriority priority, String key) throws SSIPException {
		setPriority(priority);
		_logger.info(String.format("Saying key %s", key));
		_connection.sendCommand(new SSIPCommand("KEY", key));
	}

	/**
	 * Plays a sound icon. Reffere to <a href="http://www.freebsoft.org/doc/speechd/ssip_21.html#SEC40">Standard sound icons list</a> for the available standard sound icons.
	 * @param priority the priority
	 * @param iconName the icon's name
	 * @throws SSIPException on SSIP errors
	 * @see SSIPPriority
	 */
	public void soundIcon (SSIPPriority priority, String iconName)
	throws SSIPException {
		setPriority(priority);
		_logger.info(String.format("Playing sound icon %s", iconName));
		_connection.sendCommand(new SSIPCommand("SOUND_ICON", iconName));
	}

	/**
	 * Stops speech for current target
	 * @throws SSIPException on SSIP errors.
	 */
	public void stop () throws SSIPException {
		_logger.info("stopping");
		_connection.sendCommand(new SSIPCommand("STOP", _target));
	}

	/**
	 * Cancels speech for current target
	 * @throws SSIPException on SSIp errors
	 */
	public void cancel () throws SSIPException {
		_logger.info("caceling");
		_connection.sendCommand(new SSIPCommand("CANCEL", _target));
	}

	/**
	 * Pauses speech for current target
	 * @throws SSIPException on SSIP errors
	 */
	public void pause () throws SSIPException {
		_logger.info("Pausing");
		_connection.sendCommand(new SSIPCommand("pause", _target));
	}

	/**
	 * Resumes speech for current target
	 * @throws SSIPException on SSIP errors
	 */
	public void resume () throws SSIPException {
		_logger.info("Pause");
		_connection.sendCommand(new SSIPCommand("resume", _target));
	}

	/**
	 * Instructs the server to begin a SSIP block. 
	 * @throws SSIPException on SSIP errors
	 * @see #endBlock()	
	 */
	public void beginBlock () throws SSIPException {
		_logger.info("Begin of block");
		_connection.sendCommand(new SSIPCommand("BLOCK", "BEGIN"));
	}

	/**
	 * INstructs the server to end the current block
	 * @throws SSIPException on SSIP errors
	 * @see #beginBlock()
	 */
	public void endBlock () throws SSIPException {
		_logger.info("End of block");
		_connection.sendCommand(new SSIPCommand("BLOCK", "END"));
	}

	/**
	 * Sets the synthesizer volume.
	 * @param volume the volume between -100 and 100
	 * @throws SSIPException on SSIP error
	 * @throws IllegalArgumentException when value is not in the allowed range
	 */
	public void setVolume (int volume) throws SSIPException {
		verifySynthParameter(volume);
		setParameter(_target, "volume", volume);
	}

	/**
	 * Sets the speech rate
	 * @param rate the rate value between -100 and 100.
	 * @throws SSIPException on SSIP errors
	 * @throws IllegalArgumentException if {@code rate} value is out of range
	 */
	public void setRate (int rate) throws SSIPException {
		verifySynthParameter(rate);
		setParameter(_target, "RATE", rate);
	}

	/**
	 * Sets the synthesizer pitch
	 * @param pitch the pitch to set between -100 and 100.
	 * @throws SSIPException on SSIP error
	 * @throws IllegalArgumentException if {@code pitch} is out of range
	 */
	public void setPitch (int pitch) throws SSIPException {
		verifySynthParameter(pitch);
		setParameter(_target, "PITCH", pitch);
	}

	/**
	 * Sets the output module to use for synthesis. The available output modules depend of system configuration and SSIP Server in use.
	 * See {@link #getOutputModules()} to find what modules are available
	 * 
	 * @param module the module name
	 * @throws SSIPException  on error
	 */
	public void setOutputModule (String module) throws SSIPException {
		setParameter(_target, "OUTPUT_MODULE", module);
	}

	/**
	 * Sets the language for synthesis. Note that if the language is not available no error is thrown.
	 * @param language the two letter language code acording to RFC 1766
	 * @throws SSIPException on SSIP error
	 */
	public void setLanguage (String language) throws SSIPException {
		setParameter(_target, "LANGUAGE", language);
	}

	/**
	 * Sets the SSML mode to use
	 * @param mode {@code true} to activate SSML processing {@code false} to deactivate.
	 * @throws SSIPException on SSIP error.
	 */
	public void setSSMLMOde (boolean mode) throws SSIPException {
		setParameter(_target, "SSML_MODE", mode);
	}

	/**
	 * Defines what punctuation to report, supported values are defined in {@link SSIPPunctuation}.
	 * @param punctuation the punctuation mode.
	 * @throws SSIPException on SSIPError
	 */
	public void setPunctuation (SSIPPunctuation punctuation) throws SSIPException {
		setParameter(_target, "PUNCTUATION", punctuation.toString().toLowerCase());
	}

	/**
	 * Sets the Spelling mode for synthesis.
	 * @param spelling {@code true} for spelling, {@code false} for normal speaking.
	 * @throws SSIPException on SSIP error
	 */
	public void setSpelling (boolean spelling) throws SSIPException {
		setParameter(_target, "SPELLING", spelling);
	}

	/**
	 * Sets the voice to use. See <a href="http://www.freebsoft.org/doc/speechd/ssip_22.html#SEC41">Standard voice list</a> to get possible values.
	 * @param name the voice name
	 * @throws SSIPException on SSIP error
	 */
	public void setVoice (String name) throws SSIPException {
		setParameter(_target, "VOICE", name);
	}

	/**
	 * Sets specific synthesizer voice to use for synthesis. Possible values depend from synthesizer, use {@link #getSynthesisVoices()} to get possible values.
	 * @param name the voice name
	 * @throws SSIPException 
	 */
	public void setSynthesisVoice (String name) throws SSIPException {
		setParameter(_target, "SYNTHESIS_VOICE", name);
	}

	/**
	 * Sets the pause context between sentences, higher the value longer the pause.
	 * @param n the pause context value between 0 and 100.
	 * @throws SSIPException on SSIP error
	 * @throws IllegalArgumentException if {@code n} is out of range.
	 */
	public void setPauseContext (int n) throws SSIPException {
		if (n < 0 || n < 100)
			throw new IllegalArgumentException(
					"pause context must be positive integer between 0 and 100");
		setParameter(_target, "PAUSE_CONTEXT", n);
	}

	/**
	 * Sets the capital letters recognition mode, possible values defined in {@link SSIPCapitalLetters}
	 * @param mode the mode.
	 * @throws SSIPException
	 */
	public void setCapitalLettersRecognitionMode(SSIPCapitalLetters mode) throws SSIPException {
		setParameter(_target, "CAP_LET_RECOGN", mode.toString().toLowerCase());
	}

	/**
	 * Gets the supported output modules. Returned values can be passed to {@link #setOutputModule(String)} to define what module to use.O
	 * @return the supported output modules as an unmodifiable list of strings.
	 * @throws SSIPException on SSIP error
	 */
	public List<String> getOutputModules () throws SSIPException {
		_logger.info("Listing output modules");
		SSIPResponse res = _connection.sendCommand(new SSIPCommand("LIST",
		"OUTPUT_MODULES"));
		List<String> ret = getListResponse(res);
		_logger.fine(String.format("Output modules are: %s", ret.toString()));
		return ret;
	}

	/**
	 * Gets the supported voice names. from the standard list ones
	 * @return the supported voice names as a unmodifiable list of strings.
	 * @throws SSIPException on SSIP error
	 */
	public List<String> getVoices () throws SSIPException {
		_logger.info("Getting voice names list");
		return getListResponse(_connection.sendCommand(new SSIPCommand("LIST",
		"VOICES")));
	}

	/**
	 * Gets information about all specific voices supported by the synthesizer in use.
	 * @return the supported voices information as an unmodifiable list of {@link SSIPSynthesisVoice} objects.
	 * @throws SSIPException on SSIP error.
	 * @see SSIPSynthesisVoice
	 */
	public List<SSIPSynthesisVoice> getSynthesisVoices () throws SSIPException {
		_logger.info("Getting synthesis voices");
		SSIPResponse res = _connection.sendCommand(new SSIPCommand("LIST",
		"SYNTHESIS_VOICES"));
		List<String> data = getListResponse(res);
		// get voice components and construct a list:
		List<SSIPSynthesisVoice> voices = new LinkedList<SSIPSynthesisVoice>();
		for (String s : data) {
			String[] tmp = s.split(" ");
			assert (tmp.length == 3);
			voices.add(new SSIPSynthesisVoice(tmp[0], tmp[1], tmp[2]));
		}
		return Collections.unmodifiableList(voices);
	}

	/**
	 * Gets the {@code SSIPConnection} this {@code SSIPClient} is using.
	 * @return the {@code SSIPConnection}
	 */
	public SSIPConnection getConnection() {
		return _connection;
	}
	
	/**
	 * Sets the current target for setting paramenters. allowed values are {@link Target#SELF} and {@link Target#ALL}, see {@link #setTarget(int)} to set targets for specific clients.
	 * @param target
	 */
	public void setTarget (Target target) {
		_target = target.toString();
	}

	/**
	 * Sets the target to a specific client by id. 
	 * @param id the client id
	 * @see #setTarget(Target)
	 */
	public void setTarget (int id) {
		verifyId(id);
		_target = Integer.toString(id);
	}

	/**
	 * Sets the {@code SSIPEventHandler} that will receive event notifications for this client.
	 * @param eventHandler the {@code SSIPEventHandler} to set, <@code null} for no handler.
	 */
	public void setEventHandler (SSIPEventHandler eventHandler) {
		_connection.setEventHandler(eventHandler);
	}

	/**
	 * Turns event notification on and off for all event types.
	 * @param value the value {@code true} to turn on, {@code false} to turn off.
	 * @throws SSIPException on SSIP error
	 */
	public void setNotification (boolean value) throws SSIPException {

		_connection.sendCommand(new SSIPCommand("SET", Target.SELF.toString(),
				"NOTIFICATION", "ALL", value ? "on" : "off"));
	}

	/**
	 * Sets event notifications on or off to a specific event type, see {@link SSIPEvent.EventType} for information about event types.
	 * @param value the value to set {@code true} to set on, {@code false} to turn off.
	 * @param type the type of the event.
	 * @throws SSIPException on SSIPError
	 */
	public void setNotification (boolean value, SSIPEvent.EventType type)
	throws SSIPException {
		_connection.sendCommand(new SSIPCommand("SET", Target.SELF.toString(),
				"NOTIFICATION", type.toString().toUpperCase(), value ? "on" : "off"));
	}

	/**
	 * Verifies if {@code id} is an valid id.
	 * @param id the id to verify
	 */
	protected void verifyId (int id) {
		if (id <= 0)
			throw new IllegalArgumentException("client ids must be postivie integers");
	}

	/**
	 * Verifies if a parameter is in the allowed range for synth parameters.
	 * @param param the parameter value
	 */
	protected void verifySynthParameter (int param) {
		if (param > 100 || param < -100)
			throw new IllegalArgumentException(
					"synth parameters must be between -100 and 100");
	}

	/**
	 * Constructs the client full name from its components (name, component and user).
	 * @return the full name ready to be sent to the set client_name SSIP command
	 */
	protected String makeFullName () {
		return _name + ":" + _component + ":" + _user;
	}

	/**
	 * Sets a parameter to a specific target with a string value
	 * @param target the target
	 * @param param the parameter name
	 * @param value the parameter value
	 * @throws SSIPException on SSIP error
	 */
	protected void setParameter (String target, String param, String value)
	throws SSIPException {
		_logger.info(String.format("Setting parameter %s to target %s with value %s", param, target, value));
		_connection.sendCommand(new SSIPCommand("set", target, param, value));
	}

	/**
	 * Sets a parameter to a specific target with an integer value
	 * @param target the target
	 * @param param the parameter name
	 * @param value the value
	 * @throws SSIPException on SSIP error
	 */
	protected void setParameter (String target, String param, int value)
	throws SSIPException {
		setParameter(target, param, Integer.toString(value));
	}

	/**
	 * Sets a parameter to a specific target with a boolean value
	 * @param target the target
	 * @param param the parameter to set
	 * @param value the value
	 * @throws SSIPException on SSIP error
	 */
	protected void setParameter (String target, String param, boolean value)
	throws SSIPException {
		setParameter(target, param, value ? "on" : "off");
	}

	/**
	 * Sets priority for the following messages
	 * @param priority the priority value
	 * @throws SSIPException on SSIP error
	 * @see SSIPPriority
	 */
	protected void setPriority (SSIPPriority priority) throws SSIPException {
		setParameter(Target.SELF.toString(), "priority", priority.toString());
	}

	/**
	 * Gets the integer value from a {@code SSIP} object.
	 * @param response the response
	 * @return the integer value contained in the response
	 */
	protected int getIntResponse (SSIPResponse response) {
		assert (response.getData() != null);
		return Integer.parseInt(response.getData().get(0));
	}

	/**
	 * Gets the list of data contained on a {@code SSIPResponse} object.
	 * @param res the response
	 * @return the data as a unmodifiable list of strings, an empty list if there is no data.
	 */
	protected List<String> getListResponse (SSIPResponse res) {
		List<String> data;
		if (res.getData() != null)
			data = Collections.unmodifiableList(res.getData());
		else
			data = Collections.emptyList();
		return data;
	}

	/**
	 * @return the clientId
	 */
	public int getClientId () {
		return _clientId;
	}
}
