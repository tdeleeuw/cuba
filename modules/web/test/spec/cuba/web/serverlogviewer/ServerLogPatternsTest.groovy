/*
 * Copyright (c) 2008-2018 Haulmont.
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

package spec.cuba.web.serverlogviewer

import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.haulmont.cuba.core.global.Resources
import com.haulmont.cuba.core.sys.ResourcesImpl
import com.haulmont.cuba.web.app.ui.serverlogviewer.ServerLogWindow
import spock.lang.Shared
import spock.lang.Specification

import java.util.regex.Pattern

class ServerLogPatternsTest extends Specification {

    @Shared
    protected ServerLogWindow serverLog

    protected List<String> lines

    protected List<String> patterns

    def setupSpec() {
        serverLog = new ServerLogWindow()
    }

    def "check the line doesn't match pattern"() {
        when: "line from stack trace doesn't include pattern's value"
        def line = "2018-07-20 12:36:27.955 DEBUG [http-nio-8080-exec-77] " +
                "com.haulmont.cuba.gui.theme.ThemeConstantsRepository - Loading theme constants"
        def pattern = /http-nio-8080-exec-\D/
        def transformedLine = serverLog.replaceSpaces(line)
        def transformedPattern = Pattern.compile(serverLog.replaceSpaces(pattern))
        def changedLine = serverLog.highlightLoweredAttention(transformedLine, transformedPattern)

        then:
        changedLine != serverLog.getLoweredAttentionLine(transformedLine)
    }

    def "check the line matches pattern"(String line, String pattern) {
        when:
        def transformedLine = serverLog.replaceSpaces(line)
        def transformedPattern = Pattern.compile(serverLog.replaceSpaces(pattern))
        def changedLine = serverLog.highlightLoweredAttention(transformedLine, transformedPattern)

        then:
        changedLine == serverLog.getLoweredAttentionLine(transformedLine)

        where:
        line << loadLines()
        pattern << loadPatterns()
    }

    def loadLines() {
        if (lines == null) {
            loadData()
        }
        return lines
    }

    def loadPatterns() {
        if (patterns == null) {
            loadData()
        }
        return patterns
    }

    def loadData() {
        String path = "spec/cuba/web/serverlogviewer/testData"
        def loader = getClass().getClassLoader()
        Resources resources = new ResourcesImpl(loader, null)
        String json = resources.getResourceAsString(path)
        List<LinkedTreeMap> data = new Gson().fromJson(json, ArrayList.class) as List<LinkedTreeMap>

        lines = new ArrayList<>()
        patterns = new ArrayList<>()
        for (LinkedTreeMap object: data) {
            String line = object.get("line").toString()
            lines.add(line)
            String pattern = object.get("pattern").toString()
            patterns.add(pattern)
        }
    }
}