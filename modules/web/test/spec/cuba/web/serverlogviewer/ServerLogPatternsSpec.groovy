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

import com.haulmont.cuba.web.app.ui.serverlogviewer.ServerLogWindow
import spock.lang.Shared
import spock.lang.Specification

import java.util.regex.Pattern

class ServerLogPatternsSpec extends Specification {

    protected ServerLogWindow serverLog

    @Shared
    protected String[] lines

    @Shared
    protected String[] patterns

    def setup() {
        serverLog = new ServerLogWindow()

        lines = [
                "at com.vaadin.server.ServerRpcManager.applyInvocation(ServerRpcManager.java:162)",
                "at com.vaadin.event.EventRouter.fireEvent(EventRouter.java:164)",
                "at sun.rmi.server.UnicastServerRef.dispatch(Unknown Source)",
                "at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)",
                "at org.gradle.internal.progress.DefaultBuildOperationExecutor.run(DefaultBuildOperationExecutor.java)",
                "at org.codehaus.groovy.tools.GroovyStarter.rootLoader(GroovyStarter.java:109)",
                "at java.util.stream.ReferencePipeline\$2\$1.accept(ReferencePipeline.java:174)",
                "at java.util.stream.ReduceOps\$ReduceOp.evaluateSequential(ReduceOps.java:708)",
                "at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:502)",
                "at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)",
                "[na:1.8.0_77] at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:481)",
                "at java.util.Spliterators\$",
                "at java.security.ProtectionDomain\$JavaSecurityAccessImpl.doIntersectionPrivilege(Unknown Source)",
                "at java.security.ProtectionDomain\$1.doIntersectionPrivilege(Unknown Source)",
                "at java.security.AccessController.doPrivileged",
                "at java.security.AccessControlContext\$1.doIntersectionPrivilege(AccessControlContext.java:87)",
                "at java.rmi.server.SkeletonNotFoundException",
                "at java.lang.reflect.Method.invoke(Unknown Source)",
                "at groovy.myPackage.error.",
                "at java.security.ProtectionDomain\$1.doIntersectionPrivilege(ProtectionDomain.java:75)",
                "at java.lang.reflect.Constructor.newInstance(Constructor.java:408)",
                "at com.sun.proxy.\$Proxy28.executeUpdate (Unknown Source)",
                "12:36:27.955 DEBUG [http-nio-8080-exec-7] com.haulmont.cuba.gui.theme.ThemeConstantsRepository",
                "myLine consists of some-symbols"
        ]

        patterns = [
                /at com[\.]vaadin[\.]server[\.]ServerRpcManager/,
                /at com[\.]vaadin[\.]event[\.]EventRouter[\.]fireEvent/,
                /at sun[\.]rmi[\.]/,
                /at sun[\.]reflect[\.]/,
                /at org[\.]gradle[\.]/,
                /at org[\.]codehaus[\.]groovy[\.]/,
                /at java[\.]util[\.]stream[\.]ReferencePipeline[\$]/,
                /at java[\.]util[\.]stream[\.]ReduceOps[\$]/,
                /at java[\.]util[\.]stream[\.]AbstractPipeline[\.]wrapAndCopyInto/,
                /at java[\.]util[\.]stream[\.]AbstractPipeline[\.]evaluate/,
                /at java[\.]util[\.]stream[\.]AbstractPipeline[\.]copyInto/,
                /at java[\.]util[\.]Spliterators[\$]/,
                /at java[\.]security[\.]ProtectionDomain[\$]JavaSecurityAccessImpl[\.]doIntersectionPrivilege/,
                /at java[\.]security[\.]ProtectionDomain[\$]1[\.]doIntersectionPrivilege/,
                /at java[\.]security[\.]AccessController[\.]doPrivileged/,
                /at java[\.]security[\.]AccessControlContext[\$]1[\.]doIntersectionPrivilege/,
                /at java[\.]rmi[\.]|/,
                /at java[\.]lang[\.]reflect[\.]Method[\.]invoke|/,
                /at groovy[\.]|/,
                /at java[\.]security[\.]ProtectionDomain[\$]1[\.]doIntersectionPrivilege|/,
                /at java[\.]lang[\.]reflect[\.]Constructor[\.]newInstance/,
                /at com[\.]sun[\.]proxy[\.][\$]Proxy/,
                /http-nio-8080-exec-\d*/,
                /.*some-symbol./
        ]
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

    def "check the line matches pattern" (String line, String pattern) {
        when:
        def transformedLine = serverLog.replaceSpaces(line)
        def transformedPattern = Pattern.compile(serverLog.replaceSpaces(pattern))
        def changedLine = serverLog.highlightLoweredAttention(transformedLine, transformedPattern)

        then:
        changedLine == serverLog.getLoweredAttentionLine(transformedLine)

        where:
        line << lines
        pattern << patterns
    }
}