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
 */

package com.haulmont.cuba.core.global;

import org.hibernate.validator.cfg.ConstraintMapping;

import javax.annotation.Nullable;
import javax.validation.Validator;
import java.io.Serializable;
import java.util.Locale;

/**
 * Infrastructure interface to work with bean validation.
 */
public interface BeanValidation {
    String NAME = "cuba_BeanValidation";

    Validator getValidator();

    Validator getValidator(ConstraintMapping constraintMapping);

    Validator getValidator(@Nullable ConstraintMapping constraintMapping, ValidationOptions opts);

    class ValidationOptions implements Serializable {
        protected Boolean failFast;
        protected Locale locale;

        public Boolean getFailFast() {
            return failFast;
        }

        public void setFailFast(Boolean failFast) {
            this.failFast = failFast;
        }

        public Locale getLocale() {
            return locale;
        }

        public void setLocale(Locale locale) {
            this.locale = locale;
        }
    }
}