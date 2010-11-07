/* 
 * Copyright (c) 2010 Levente Farkas <lfarkas@lfarkas.org>
 * 
 * This file is part of gstreamer-java.
 *
 * This code is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * version 3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gstreamer.elements.good;

import org.gstreamer.Bin;
import org.gstreamer.Caps;
import org.gstreamer.lowlevel.GstAPI.GstCallback;

import com.sun.jna.Pointer;

/**
 * Java representation of gstreamer gstrtpbin
 */
public class RTPBin extends Bin {
	public static final String GST_NAME = "gstrtpbin";
	
	public RTPBin(String name) {
		this(makeRawElement(GST_NAME, name));
	}

	public RTPBin(Initializer init) {
		super(init);
	}

	/**
     * Signal emitted when an SSRC that became inactive because of a BYE packet.
     * 
     * @see #connect(ON_BYE_SSRC)
     * @see #disconnect(ON_BYE_SSRC)
     */
    public static interface ON_BYE_SSRC {
        /**
         * @param rtpbin the object which received the signal
         * @param session the session
         * @param ssrc the SSRC
         * @param user_data user data set when the signal handler was connected.
         */
        public void onByeSsrc(RTPBin bin, int session, int ssrc, Pointer userData);
    }
    /**
     * Add a listener for the <code>on-bye-ssrc</code> signal on this RTPBin
     * 
     * @param listener The listener to be called.
     */
    public void connect(final ON_BYE_SSRC listener) {
        connect(ON_BYE_SSRC.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(RTPBin bin, int session, int ssrc, Pointer userData) {
                listener.onByeSsrc(bin, session, ssrc, userData);
            }
        });
    }    
    /**
     * Disconnect the listener for the <code>on-bye-ssrc</code> signal on this RTPBin
     * 
     * @param listener The listener that was registered to receive the signal.
     */
    public void disconnect(ON_BYE_SSRC listener) {
        disconnect(ON_BYE_SSRC.class, listener);
    }

	/**
     * Signal emitted when an SSRC that has timed out because of BYE.
     * 
     * @see #connect(ON_BYE_TIMEOUT)
     * @see #disconnect(ON_BYE_TIMEOUT)
     */
    public static interface ON_BYE_TIMEOUT {
        /**
         * @param rtpbin the object which received the signal
         * @param session the session
         * @param ssrc the SSRC
         * @param user_data user data set when the signal handler was connected.
         */
        public void onByeTimeout(RTPBin bin, int session, int ssrc, Pointer userData);
    }
    /**
     * Add a listener for the <code>on-bye-timeout</code> signal on this RTPBin
     * 
     * @param listener The listener to be called.
     */
    public void connect(final ON_BYE_TIMEOUT listener) {
        connect(ON_BYE_TIMEOUT.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(RTPBin bin, int session, int ssrc, Pointer userData) {
                listener.onByeTimeout(bin, session, ssrc, userData);
            }
        });
    }    
    /**
     * Disconnect the listener for the <code>on-bye-timeout</code> signal on this RTPBin
     * 
     * @param listener The listener that was registered to receive the signal.
     */
    public void disconnect(ON_BYE_TIMEOUT listener) {
        disconnect(ON_BYE_TIMEOUT.class, listener);
    }

	/**
     * Signal emitted when a new SSRC that entered session.
     * 
     * @see #connect(ON_NEW_SSRC)
     * @see #disconnect(ON_NEW_SSRC)
     */
    public static interface ON_NEW_SSRC {
        /**
         * @param rtpbin the object which received the signal
         * @param session the session
         * @param ssrc the SSRC
         * @param user_data user data set when the signal handler was connected.
         */
        public void onNewSsrc(RTPBin bin, int session, int ssrc, Pointer userData);
    }
    /**
     * Add a listener for the <code>on-new-ssrc</code> signal on this RTPBin
     * 
     * @param listener The listener to be called.
     */
    public void connect(final ON_NEW_SSRC listener) {
        connect(ON_NEW_SSRC.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(RTPBin bin, int session, int ssrc, Pointer userData) {
                listener.onNewSsrc(bin, session, ssrc, userData);
            }
        });
    }    
    /**
     * Disconnect the listener for the <code>on-new-ssrc</code> signal on this RTPBin
     * 
     * @param listener The listener that was registered to receive the signal.
     */
    public void disconnect(ON_NEW_SSRC listener) {
        disconnect(ON_NEW_SSRC.class, listener);
    }

	/**
     * Signal emitted when SSRC sender has sent data up to the configured NPT stop time.
     * 
     * @see #connect(ON_NPT_STOP)
     * @see #disconnect(ON_NPT_STOP)
     */
    public static interface ON_NPT_STOP {
        /**
         * @param rtpbin the object which received the signal
         * @param session the session
         * @param ssrc the SSRC
         * @param user_data user data set when the signal handler was connected.
         */
        public void onNptStop(RTPBin bin, int session, int ssrc, Pointer userData);
    }
    /**
     * Add a listener for the <code>on-npt-stop</code> signal on this RTPBin
     * 
     * @param listener The listener to be called.
     */
    public void connect(final ON_NPT_STOP listener) {
        connect(ON_NPT_STOP.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(RTPBin bin, int session, int ssrc, Pointer userData) {
                listener.onNptStop(bin, session, ssrc, userData);
            }
        });
    }    
    /**
     * Disconnect the listener for the <code>on-npt-stop</code> signal on this RTPBin
     * 
     * @param listener The listener that was registered to receive the signal.
     */
    public void disconnect(ON_NPT_STOP listener) {
        disconnect(ON_NPT_STOP.class, listener);
    }

	/**
     * Signal emitted when a sender SSRC that has timed out and became a receiver.
     * 
     * @see #connect(ON_SENDER_TIMEOUT)
     * @see #disconnect(ON_SENDER_TIMEOUT)
     */
    public static interface ON_SENDER_TIMEOUT {
        /**
         * @param rtpbin the object which received the signal
         * @param session the session
         * @param ssrc the SSRC
         * @param user_data user data set when the signal handler was connected.
         */
        public void onSenderTimeout(RTPBin bin, int session, int ssrc, Pointer userData);
    }
    /**
     * Add a listener for the <code>on-sender-timeout</code> signal on this RTPBin
     * 
     * @param listener The listener to be called.
     */
    public void connect(final ON_SENDER_TIMEOUT listener) {
        connect(ON_SENDER_TIMEOUT.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(RTPBin bin, int session, int ssrc, Pointer userData) {
                listener.onSenderTimeout(bin, session, ssrc, userData);
            }
        });
    }    
    /**
     * Disconnect the listener for the <code>on-sender-timeout</code> signal on this RTPBin
     * 
     * @param listener The listener that was registered to receive the signal.
     */
    public void disconnect(ON_SENDER_TIMEOUT listener) {
        disconnect(ON_SENDER_TIMEOUT.class, listener);
    }

	/**
     * Signal emitted when a SSRC that is active, i.e., sending RTCP.
     * 
     * @see #connect(ON_SSRC_ACTIVE)
     * @see #disconnect(ON_SSRC_ACTIVE)
     */
    public static interface ON_SSRC_ACTIVE {
        /**
         * @param rtpbin the object which received the signal
         * @param session the session
         * @param ssrc the SSRC
         * @param user_data user data set when the signal handler was connected.
         */
        public void onSsrcActive(RTPBin bin, int session, int ssrc, Pointer userData);
    }
    /**
     * Add a listener for the <code>on-ssrc-active</code> signal on this RTPBin
     * 
     * @param listener The listener to be called.
     */
    public void connect(final ON_SSRC_ACTIVE listener) {
        connect(ON_SSRC_ACTIVE.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(RTPBin bin, int session, int ssrc, Pointer userData) {
                listener.onSsrcActive(bin, session, ssrc, userData);
            }
        });
    }    
    /**
     * Disconnect the listener for the <code>on-ssrc-active</code> signal on this RTPBin
     * 
     * @param listener The listener that was registered to receive the signal.
     */
    public void disconnect(ON_SSRC_ACTIVE listener) {
        disconnect(ON_SSRC_ACTIVE.class, listener);
    }

	/**
     * Signal emitted when we have an SSRC collision.
     * 
     * @see #connect(ON_SSRC_COLLISION)
     * @see #disconnect(ON_SSRC_COLLISION)
     */
    public static interface ON_SSRC_COLLISION {
        /**
         * @param rtpbin the object which received the signal
         * @param session the session
         * @param ssrc the SSRC
         * @param user_data user data set when the signal handler was connected.
         */
        public void onSsrcCollision(RTPBin bin, int session, int ssrc, Pointer userData);
    }
    /**
     * Add a listener for the <code>on-ssrc-collision</code> signal on this RTPBin
     * 
     * @param listener The listener to be called.
     */
    public void connect(final ON_SSRC_COLLISION listener) {
        connect(ON_SSRC_COLLISION.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(RTPBin bin, int session, int ssrc, Pointer userData) {
                listener.onSsrcCollision(bin, session, ssrc, userData);
            }
        });
    }    
    /**
     * Disconnect the listener for the <code>on-ssrc-collision</code> signal on this RTPBin
     * 
     * @param listener The listener that was registered to receive the signal.
     */
    public void disconnect(ON_SSRC_COLLISION listener) {
        disconnect(ON_SSRC_COLLISION.class, listener);
    }
    
	/**
     * Signal emitted when a SSRC that is active, i.e., sending RTCP.
     * 
     * @see #connect(ON_SSRC_SDES)
     * @see #disconnect(ON_SSRC_SDES)
     */
    public static interface ON_SSRC_SDES {
        /**
         * @param rtpbin the object which received the signal
         * @param session the session
         * @param ssrc the SSRC
         * @param user_data user data set when the signal handler was connected.
         */
        public void onSsrcSdes(RTPBin bin, int session, int ssrc, Pointer userData);
    }
    /**
     * Add a listener for the <code>on-ssrc-sdes</code> signal on this RTPBin
     * 
     * @param listener The listener to be called.
     */
    public void connect(final ON_SSRC_SDES listener) {
        connect(ON_SSRC_SDES.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(RTPBin bin, int session, int ssrc, Pointer userData) {
                listener.onSsrcSdes(bin, session, ssrc, userData);
            }
        });
    }    
    /**
     * Disconnect the listener for the <code>on-ssrc-sdes</code> signal on this RTPBin
     * 
     * @param listener The listener that was registered to receive the signal.
     */
    public void disconnect(ON_SSRC_SDES listener) {
        disconnect(ON_SSRC_SDES.class, listener);
    }
    
	/**
     * Signal emitted when a new SSRC that became validated.
     * 
     * @see #connect(ON_SSRC_VALIDATED)
     * @see #disconnect(ON_SSRC_VALIDATED)
     */
    public static interface ON_SSRC_VALIDATED {
        /**
         * @param rtpbin the object which received the signal
         * @param session the session
         * @param ssrc the SSRC
         * @param user_data user data set when the signal handler was connected.
         */
        public void onSsrcValidated(RTPBin bin, int session, int ssrc, Pointer userData);
    }
    /**
     * Add a listener for the <code>on-ssrc-validated</code> signal on this RTPBin
     * 
     * @param listener The listener to be called.
     */
    public void connect(final ON_SSRC_VALIDATED listener) {
        connect(ON_SSRC_VALIDATED.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(RTPBin bin, int session, int ssrc, Pointer userData) {
                listener.onSsrcValidated(bin, session, ssrc, userData);
            }
        });
    }    
    /**
     * Disconnect the listener for the <code>on-ssrc-validated</code> signal on this RTPBin
     * 
     * @param listener The listener that was registered to receive the signal.
     */
    public void disconnect(ON_SSRC_VALIDATED listener) {
        disconnect(ON_SSRC_VALIDATED.class, listener);
    }
    
	/**
     * Signal emitted when an SSRC that has timed out.
     * 
     * @see #connect(ON_TIMEOUT)
     * @see #disconnect(ON_TIMEOUT)
     */
    public static interface ON_TIMEOUT {
        /**
         * @param rtpbin the object which received the signal
         * @param session the session
         * @param ssrc the SSRC
         * @param user_data user data set when the signal handler was connected.
         */
        public void onTimeout(RTPBin bin, int session, int ssrc, Pointer userData);
    }
    /**
     * Add a listener for the <code>on-timeout</code> signal on this RTPBin
     * 
     * @param listener The listener to be called.
     */
    public void connect(final ON_TIMEOUT listener) {
        connect(ON_TIMEOUT.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(RTPBin bin, int session, int ssrc, Pointer userData) {
                listener.onTimeout(bin, session, ssrc, userData);
            }
        });
    }    
    /**
     * Disconnect the listener for the <code>on-timeout</code> signal on this RTPBin
     * 
     * @param listener The listener that was registered to receive the signal.
     */
    public void disconnect(ON_TIMEOUT listener) {
        disconnect(ON_TIMEOUT.class, listener);
    }
    
	/**
     * Request the payload type as GstCaps for pt in session.
     * 
     * @see #connect(REQUEST_PT_MAP)
     * @see #disconnect(REQUEST_PT_MAP)
     */
    public static interface REQUEST_PT_MAP {
        /**
         * @param rtpbin the object which received the signal
         * @param session the session
         * @param pt the pt
         * @param user_data user data set when the signal handler was connected.
         */
        public Caps requestPtMap(RTPBin bin, int session, int pt, Pointer userData);
    }
    /**
     * Add a listener for the <code>request-pt-map</code> signal on this RTPBin
     * 
     * @param listener The listener to be called.
     */
    public void connect(final REQUEST_PT_MAP listener) {
        connect(REQUEST_PT_MAP.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public Caps callback(RTPBin bin, int session, int pt, Pointer userData) {
                return listener.requestPtMap(bin, session, pt, userData);
            }
        });
    }    
    /**
     * Disconnect the listener for the <code>request-pt-map</code> signal on this RTPBin
     * 
     * @param listener The listener that was registered to receive the signal.
     */
    public void disconnect(REQUEST_PT_MAP listener) {
        disconnect(REQUEST_PT_MAP.class, listener);
    }
    
	/**
     * Signal emitted when the current payload type changed to pt in session.
     * 
     * @see #connect(PAYLOAD_TYPE_CHANGE)
     * @see #disconnect(PAYLOAD_TYPE_CHANGE)
     */
    public static interface PAYLOAD_TYPE_CHANGE {
        /**
         * @param rtpbin the object which received the signal
         * @param session the session
         * @param pt the pt
         * @param user_data user data set when the signal handler was connected.
         */
        public void payloadTypeChange(RTPBin bin, int session, int pt, Pointer userData);
    }
    /**
     * Add a listener for the <code>payload-type-change</code> signal on this RTPBin
     * 
     * @param listener The listener to be called.
     */
    public void connect(final PAYLOAD_TYPE_CHANGE listener) {
        connect(PAYLOAD_TYPE_CHANGE.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(RTPBin bin, int session, int pt, Pointer userData) {
                listener.payloadTypeChange(bin, session, pt, userData);
            }
        });
    }    
    /**
     * Disconnect the listener for the <code>payload-type-change</code> signal on this RTPBin
     * 
     * @param listener The listener that was registered to receive the signal.
     */
    public void disconnect(PAYLOAD_TYPE_CHANGE listener) {
        disconnect(PAYLOAD_TYPE_CHANGE.class, listener);
    }
}