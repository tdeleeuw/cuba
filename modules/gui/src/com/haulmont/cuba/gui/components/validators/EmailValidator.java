/*
 * Copyright (c) 2008-2016 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.haulmont.cuba.gui.components.validators;

import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.components.Field;
import com.haulmont.cuba.gui.components.Validatable;
import com.haulmont.cuba.gui.components.ValidationException;

import org.dom4j.Element;

import java.net.IDN;


/**
 * Field validator for an e-mail address.
 *
 * Implementation is heavily based on description of https://en.wikipedia.org/wiki/Email_address and relies on Guava for
 * the domain part.
 */
public class EmailValidator implements Field.Validator {
    protected String message;
    protected Messages messages;
    protected String messagesPack;

    /** Creates a new EmailValidator object. */
    public EmailValidator() {
        super();
    }

    /**
     * Creates a new EmailValidator object.
     *
     * @param  element       The element
     * @param  messagesPack  The message Pack name
     */
    public EmailValidator(Element element, String messagesPack) {
        message = element.attributeValue("message");
        this.messagesPack = messagesPack;
        this.messages = AppBeans.get(Messages.NAME);
    }

    /**
     * Field validator.<br>
     * Validators are invoked when {@link Validatable#validate()} is called. Editor screen calls {@code validate()} on commit.
     *
     * @param   value  The value to validate
     *
     * @throws  ValidationException  The validation failed
     */
    @Override public void validate(Object value) throws ValidationException {

        if (value == null) {
            return;
        }

        if (!isValidMail((CharSequence) value)) {
            String msg = null;
            if (message != null) {
                msg = messages.getTools().loadString(messagesPack, message);
            }
            if (msg == null) {
                msg = "Invalid value '%s'";
            }
            throw new ValidationException(String.format(msg,  value));
        }
    }

    /**
     * Check if the domain name is valid (can either be a domain name or an IPv4 or v6 address).
     *
     * @param   domainPart  domain name/IP to validated
     *
     * @return  true if it is valid, false otherwise.
     */
    private boolean isDomainNameValid(String domainPart) {

        if (domainPart.length() == 0) {
            return false;
        }

        if ('.' == domainPart.charAt(domainPart.length() - 1)) {
            return false;
        }

        if (domainPart.charAt(0) == '[') {

            if (domainPart.charAt(domainPart.length() - 1) != ']') {
                return false;
            }

            int skip = 1;

            if (domainPart.toLowerCase().startsWith("[ipv6:")) {
                skip = 6;
            }

            return InetAddresses.isInetAddress(domainPart.substring(skip, domainPart.length() - 2));
        }

        if (!InternetDomainName.isValid(domainPart)) {
            return false;
        }

        String asciiDomain;

        try {
            asciiDomain = IDN.toASCII(domainPart);
        } catch (IllegalArgumentException iae) {
            return false;
        }

        return asciiDomain.length() <= 255;
    }

    /**
     * Validates the local part.
     *
     * @param   localPart  The local part to validate
     *
     * @return  true if it is valid, false otherwise.
     */
    private boolean isLocalPartValid(String localPart) {

        if ((localPart.length() < 1) || (localPart.length() > 64)) {
            return false;
        }

        boolean isQuoted = '"' == localPart.charAt(0);

        if (isQuoted) {
            return isQuotedLocalPartValid(localPart);
        }

        return isUnquotedLocalPartValid(localPart);
    }

    /**
     * Check if the quoted local part is valid.
     *
     * @param   localPart  quoted local part to validate
     *
     * @return  true if it is valid, false otherwise.
     */
    private boolean isQuotedLocalPartValid(String localPart) {

        if (localPart.length() < 3) {
            return false;
        }

        if ((localPart.charAt(localPart.length() - 1) != '"') || (localPart.charAt(localPart.length() - 1) == '\\')) {
            return false;
        }

        char chars[] = localPart.toCharArray();
        int parenthesisCount = 0;

        for (int i = 1; i < (chars.length - 1); i++) {

            if ('\\' == chars[i]) {

                if ((++i == chars.length) || ((chars[i] != '\\') && (chars[i] != '"'))) {

                    // Escape should only be followed by '\' or '"'
                    return false;
                }
            } else {

                if (chars[i] == '(') {
                    parenthesisCount++;
                } else if (chars[i] == ')') {
                    parenthesisCount--;

                    if (parenthesisCount < 0) {
                        return false;
                    }
                } else if (!isValidQuotedChar(chars[i])) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Check if the local part (that is not quoted) is valid.
     *
     * @param   localPart  The unquoted local part
     *
     * @return  true if it is valid, false otherwise.
     */
    private boolean isUnquotedLocalPartValid(String localPart) {
        boolean isPreviousCharDot = true;
        char chars[] = localPart.toCharArray();

        for (char c : chars) {

            if (!isValidBasicChar(c)) {

                // Only dot has to be handled to detect double dots in unquoted local parts or at the beginning
                if ('.' == c) {

                    if (isPreviousCharDot) {
                        return false;
                    } else {
                        isPreviousCharDot = true;
                    }
                } else {
                    return false;
                }
            } else {
                isPreviousCharDot = false;
            }
        }

        // The last character cannot be a dot
        return chars[chars.length - 1] != '.';
    }

    /**
     * Checks the full e-mail address.
     *
     * @param   value  The input value
     *
     * @return  true if the email syntax is correct
     */
    private boolean isValidMail(CharSequence value) {
        String eMail = value.toString();
        int atPosition = eMail.lastIndexOf('@');

        if (atPosition < 0) {
            return false;
        }

        String localPart = eMail.substring(0, atPosition);
        String domainPart = eMail.substring(atPosition + 1);

        if (!isLocalPartValid(localPart)) {
            return false;
        }

        return isDomainNameValid(domainPart);
    }

    /**
     * Checks if the character is allowed in an unquoted local part (not taking into account dot as its validity depends
     * on the context).
     *
     * @param   c  character to validate
     *
     * @return  true if it is valid, false otherwise.
     */
    private boolean isValidBasicChar(char c) {

        if (((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'))) {
            return true;
        }

        if ((c >= '0') && (c <= '9')) {
            return true;
        }

        // #$%&'
        if ((c >= '#') && (c <= '\'')) {
            return true;
        }

        // *+-
        if ((c == '*') || (c == '+') || (c == '-')) {
            return true;
        }

        // ^_`
        if ((c >= '^') && (c <= '`')) {
            return true;
        }

        // {|}~
        if ((c >= '{') && (c <= '~')) {
            return true;
        }

        // !, /, =, ?
        if ((c == '!') || (c == '/') || (c == '=') || (c == '?')) {
            return true;
        }

        return c >= '\u0080';
    }

    /**
     * Checks if the character is allowed in an quoted local part (not taking into account escape character, nor parenthesis as its validity depends
     * on the context).
     *
     * @param   c  character to validate
     *
     * @return  true if it is valid, false otherwise.
     */
    private boolean isValidQuotedChar(char c) {

        if ((c >= 'a') && (c <= 'z')) {
            return true;
        }

        // 0-9:;<=>?@ A-Z [\]^_`
        if ((c >= '0') && (c <= '`')) {
            return true;
        }

        // "#$%&'
        if ((c >= '"') && (c <= '\'')) {
            return true;
        }

        // space or *+-,
        if ((c == '.') || (c == '*') || (c == '+') || (c == '-') || (c == ' ') || (c == ',')) {
            return true;
        }

        // {|}~
        if ((c >= '{') && (c <= '~')) {
            return true;
        }

        // !, /
        if ((c == '!') || (c == '/')) {
            return true;
        }

        return c >= '\u0080';
    }
}
