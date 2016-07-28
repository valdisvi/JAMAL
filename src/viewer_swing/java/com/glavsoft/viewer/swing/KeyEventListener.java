// Copyright (C) 2010, 2011, 2012, 2013 GlavSoft LLC.
// All rights reserved.
//
// -------------------------------------------------------------------------
// This file is part of the TightVNC software. Please visit our Web site:
//
// http://www.tightvnc.com/
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, write to the Free Software Foundation, Inc.,
// 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
// -------------------------------------------------------------------------
//

package viewer_swing.java.com.glavsoft.viewer.swing;

import main.java.com.glavsoft.rfb.client.KeyEventMessage;
import main.java.com.glavsoft.rfb.protocol.ProtocolContext;
import viewer_swing.java.com.glavsoft.viewer.swing.SwingViewerWindow;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JToggleButton;

import static main.java.com.glavsoft.utils.Keymap.*;

public class KeyEventListener implements KeyListener {

    private final ProtocolContext context;
    private KeyboardConvertor convertor;

    private Map<Integer, Boolean> keysPresed = new HashMap<>();
    private boolean lastCtrl = false;
    private boolean lastxCtrl = false;
    private boolean lastyCtrl = false;

    public KeyEventListener(ProtocolContext context) {
        this.context = context;
    }

    public boolean ctrlChk(int keyChar) {
        if (keyChar == KeyEvent.VK_CONTROL) {
            if (lastCtrl) {
                if (lastxCtrl) {
                    if (lastyCtrl) {
                        for (int i = 0; i < SwingViewerWindow.getKbdButtons()
                                .size(); i++) {
                            if (SwingViewerWindow.getKbdButtons()
                                    .get(i) instanceof JToggleButton) {
                                JToggleButton btn = (JToggleButton) SwingViewerWindow
                                        .getKbdButtons().get(i);
                                if (btn.getName().equals("ctrlButton")
                                        || btn.getName().equals("altButton")) {
                                    btn.doClick();
                                }
                            }
                        }
                        lastCtrl = false;
                        lastxCtrl = false;
                        lastyCtrl = false;
                        return true;
                    }
                    lastyCtrl = true;
                }
                lastxCtrl = true;
            }
            lastCtrl = true;
        } else {
            lastCtrl = false;
            lastxCtrl = false;
            lastyCtrl = false;
        }
        return false;
    }

    private void processKeyEvent(KeyEvent e) {

        int keyChar = e.getKeyCode();

        if (ctrlChk(keyChar))
            return;
        if (processSpecialKeys(e))
            return;
        if (processActionKey(e))
            return;
        if (processModifierKeys(e))
            return;

        sendKeyEvent(keyChar, e);
    }

    /**
     * Process AltGraph, num pad keys...
     */
    private boolean processSpecialKeys(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (KeyEvent.VK_ALT_GRAPH == keyCode) {
            sendKeyEvent(K_CTRL_LEFT, e);
            sendKeyEvent(K_ALT_LEFT, e);
            return true;
        }
        switch (keyCode) {
        case KeyEvent.VK_NUMPAD0:
            keyCode = K_KP_0;
            break;
        case KeyEvent.VK_NUMPAD1:
            keyCode = K_KP_1;
            break;
        case KeyEvent.VK_NUMPAD2:
            keyCode = K_KP_2;
            break;
        case KeyEvent.VK_NUMPAD3:
            keyCode = K_KP_3;
            break;
        case KeyEvent.VK_NUMPAD4:
            keyCode = K_KP_4;
            break;
        case KeyEvent.VK_NUMPAD5:
            keyCode = K_KP_5;
            break;
        case KeyEvent.VK_NUMPAD6:
            keyCode = K_KP_6;
            break;
        case KeyEvent.VK_NUMPAD7:
            keyCode = K_KP_7;
            break;
        case KeyEvent.VK_NUMPAD8:
            keyCode = K_KP_8;
            break;
        case KeyEvent.VK_NUMPAD9:
            keyCode = K_KP_9;
            break;

        case KeyEvent.VK_MULTIPLY:
            keyCode = K_KP_MULTIPLY;
            break;
        case KeyEvent.VK_ADD:
            keyCode = K_KP_ADD;
            break;
        case KeyEvent.VK_SEPARATOR:
            keyCode = K_KP_SEPARATOR;
            break;
        case KeyEvent.VK_SUBTRACT:
            keyCode = K_KP_SUBTRACT;
            break;
        case KeyEvent.VK_DECIMAL:
            keyCode = K_KP_DECIMAL;
            break;
        case KeyEvent.VK_DIVIDE:
            keyCode = K_KP_DIVIDE;
            break;

        default:
            return false;
        }
        sendKeyEvent(e.getKeyCode(), e);
        return true;
    }

    private boolean processActionKey(KeyEvent e) {
        int keyCode = e.getKeyCode();
        final int location = e.getKeyLocation();
        if (e.isActionKey()) {
            switch (keyCode) {
            case KeyEvent.VK_HOME:
                keyCode = KeyEvent.KEY_LOCATION_NUMPAD == location ? K_KP_HOME
                        : K_HOME;
                break;
            case KeyEvent.VK_LEFT:
                keyCode = KeyEvent.KEY_LOCATION_NUMPAD == location ? K_KP_LEFT
                        : K_LEFT;
                break;
            case KeyEvent.VK_UP:
                keyCode = KeyEvent.KEY_LOCATION_NUMPAD == location ? K_KP_UP
                        : K_UP;
                break;
            case KeyEvent.VK_RIGHT:
                keyCode = KeyEvent.KEY_LOCATION_NUMPAD == location ? K_KP_RIGHT
                        : K_RIGHT;
                break;
            case KeyEvent.VK_DOWN:
                keyCode = KeyEvent.KEY_LOCATION_NUMPAD == location ? K_KP_DOWN
                        : K_DOWN;
                break;
            case KeyEvent.VK_PAGE_UP:
                keyCode = KeyEvent.KEY_LOCATION_NUMPAD == location
                        ? K_KP_PAGE_UP : K_PAGE_UP;
                break;
            case KeyEvent.VK_PAGE_DOWN:
                keyCode = KeyEvent.KEY_LOCATION_NUMPAD == location
                        ? K_KP_PAGE_DOWN : K_PAGE_DOWN;
                break;
            case KeyEvent.VK_END:
                keyCode = KeyEvent.KEY_LOCATION_NUMPAD == location ? K_KP_END
                        : K_END;
                break;
            case KeyEvent.VK_INSERT:
                keyCode = KeyEvent.KEY_LOCATION_NUMPAD == location ? K_KP_INSERT
                        : K_INSERT;
                break;
            case KeyEvent.VK_F1:
                keyCode = K_F1;
                break;
            case KeyEvent.VK_F2:
                keyCode = K_F2;
                break;
            case KeyEvent.VK_F3:
                keyCode = K_F3;
                break;
            case KeyEvent.VK_F4:
                keyCode = K_F4;
                break;
            case KeyEvent.VK_F5:
                keyCode = K_F5;
                break;
            case KeyEvent.VK_F6:
                keyCode = K_F6;
                break;
            case KeyEvent.VK_F7:
                keyCode = K_F7;
                break;
            case KeyEvent.VK_F8:
                keyCode = K_F8;
                break;
            case KeyEvent.VK_F9:
                keyCode = K_F9;
                break;
            case KeyEvent.VK_F10:
                keyCode = K_F10;
                break;
            case KeyEvent.VK_F11:
                keyCode = K_F11;
                break;
            case KeyEvent.VK_F12:
                keyCode = K_F12;
                break;

            case KeyEvent.VK_KP_LEFT:
                keyCode = K_KP_LEFT;
                break;
            case KeyEvent.VK_KP_UP:
                keyCode = K_KP_UP;
                break;
            case KeyEvent.VK_KP_RIGHT:
                keyCode = K_KP_RIGHT;
                break;
            case KeyEvent.VK_KP_DOWN:
                keyCode = K_KP_DOWN;
                break;

            default:
                return false; // ignore other 'action' keys
            }
            sendKeyEvent(e.getKeyCode(), e);
            return true;
        }
        return false;
    }

    private boolean processModifierKeys(KeyEvent e) {
        int keyCode = e.getKeyCode();

        switch (keyCode) {
        case KeyEvent.VK_CONTROL:
            break;
        case KeyEvent.VK_SHIFT:
            break;
        case KeyEvent.VK_ALT:
            break;
        case KeyEvent.VK_META:
            break;
        // follow two are 'action' keys in java terms but modifier keys actualy
        case KeyEvent.VK_WINDOWS:
            break;
        case KeyEvent.VK_CONTEXT_MENU:
            break;
        default:
            return false;
        }

        sendKeyEvent(e.getKeyCode(), e);

        return true;
    }

    private void sendKeyEvent(int keyChar, KeyEvent e) {
        context.sendMessage(new KeyEventMessage(keyChar,
                e.getID() == KeyEvent.KEY_PRESSED));
    }

    @Override
    public void keyTyped(KeyEvent e) {
        System.out.println(e.getKeyCode() + " typed");
        processModifierKeys(e);
        e.consume();
    }

    public void focusLost() {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        keysPresed.put(e.getKeyCode(), true);

        System.out.println(e.getKeyCode() + " presed");

        processKeyEvent(e);
        e.consume();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysPresed.remove(e.getKeyCode());
        System.out.println(e.getKeyCode() + " released");

        processKeyEvent(e);
        e.consume();

    }

    public void addModifierListener(
            ModifierButtonEventListener modifierButtonListener) {
    }

    public void setConvertToAscii(boolean convertToAscii) {
        if (convertToAscii && null == convertor) {
            convertor = new KeyboardConvertor();
        }
    }

}