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

import com.haulmont.cuba.gui.components.ValidationException;
import junit.framework.TestCase;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class EmailValidatorTest extends TestCase {
    protected ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private EmailValidator instance = new EmailValidator();

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    // Some examples are taken from Wikipedia https://en.wikipedia.org/wiki/Email_address

    public void testValidate() {
        String[] validEmails = {
                "\"very.(),:;<>[]\\\".VERY.\\\"very@\\\\ \\\"very\\\".unusual\"@strange.example.com",
                "\"()<>[]:,;@\\\"!#$%&'-/=?^_`{}| ~.a#\"@example.org",
                "\"(Test) \"@example.org",
                "\" \"@example.org",
                "selenium@google.com",
                "selenium#@google.com",
                "a@mail.ru",
                "Jhon.Doe+cuba.platform@gmail.com",
                "test@pegas.travel",
                "yuriy.pavlov@r7.com",
                "yuriy-p@mail.ru",
                "yuriy-@mail.ru",
                "test@int64.ru",
                "y_pavlov@mail.ru",
                "abc@hotmail.co.uk",
                "fully-qualified-domain@example.com",
                "zxc@safe-mail.net",
                "asd@o2.co.uk",
                "test@i.ua",
                "example-indeed@strange-example.com",
                "email@t-online.de",
                "abc.xyz@yahoo.com.br",
                "qwe-asd@oi.com.br",
                "my.ema-il@123qwe.co.uk",
                "i.van-petrov23@mail2cuba.com",
                "other.email-with-dash@example.com",
                "i.van-petrov23@mail-2-cuba.com",
                "admin@test.mg.gov.com",
                "admin@tes-t.mg.gov.com",
                "admin@tes-t.mg.g-o-v.com",
                "admin@te-st.m-g.gov.com",
                "test@pegas.travelersessolong",
                "simple@example.com",
                "very.common@example.com",
                "disposable.style.email.with+symbol@example.com",
                "_pavlov@mail.ru",
                "other.email-with-hyphen@example.com",
                "fully-qualified-domain@example.com",
                "user.name+tag+sorting@example.com",
                "x@example.com",
                "example-indeed@strange-example.com",
                "admin@mailserver1",
                "#!$%&'*+-/=?^_`{}|~@example.org",
                "example@s.example",
                "user@[2001:DB8::1]",
                "test@i--i.ru",
                "admin@t-est.m-g.go-v.com"
        };

        for (String validEmail : validEmails) {
            try {
                instance.validate(validEmail);
            } catch (ValidationException ve) {
                fail("email '" + validEmail + "' is incorrectly reported invalid");
            }
        }
    }

    public void testValidateFail() {
        String[] invalidEmails = {
                "\"(Test)test) \"@example.org",
                "\"Test) \"@example.org",
                "\"(Test \"@example.org",
                "\"(Test \"@example.org",
                "\"(etetst(Test) \"@example.org",
                "\"Invalid escape \\t\"@example.org",
                "@mail.ru",
                ".test@mail.ru",
                "test..test@mail.ru",
                "test.test.@mail.ru",
                "yuriy.@mail.ru",
                "Abc.example.com",
                "_pavlov@-mail.ru",
                "just\"not\"right@example.com",
                "pavlov@mail-.ru",
                "pavlov@-mail-.ru",
                "test@-i.com",
                "test@i-.ru",
                "test@-i-.com",
                ".email@test.com",
                "email.@test.com",
                ".email.@test.com",
                "admin@test-.mg.gov.com",
                "admin@tes-t.-mg.g--ov.com",
                "admin@te-st.m-g.-gov-.com",
                "Abc.example.com",
                "A @b@c@example.com",
                "a\"b(c)d,e:f;g<h>i[j\\k]l@example.com",
                "this is\"not\\allowed@example.com",
                "this\\ still\\\"not\\\\allowed@example.com",
                "1234567890123456789012345678901234567890123456789012345678901234+x@example.com",
                "john..doe@example.com",
                "john.doe@example..com",
                "admin@t-est.m-g-.go-v.com"
        };

        for (String invalidEmail : invalidEmails) {
            try {
                instance.validate(invalidEmail);
                fail("email '" + invalidEmail + "' is incorrectly reported valid");
            } catch (ValidationException ve) {
                // This is expected
            }
        }
    }
}