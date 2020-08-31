/*
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */

package io.crate.protocols;

import io.crate.exceptions.AmbiguousColumnException;
import io.crate.exceptions.SQLExceptions;
import io.crate.metadata.ColumnIdent;
import io.crate.protocols.postgres.PGError;
import io.crate.protocols.postgres.PGErrorStatus;
import io.crate.rest.action.HttpError;
import io.crate.types.DataTypes;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.hamcrest.Matcher;
import org.junit.Test;

import static io.crate.protocols.postgres.PGErrorStatus.AMBIGUOUS_COLUMN;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static io.crate.testing.TestingHelpers.createReference;

public class ErrorMappingTest {

    @Test
    public void test_ambiguous_column_exception_error_mapping() {
        isError(new AmbiguousColumnException(new ColumnIdent("x"), createReference("x", DataTypes.STRING)),
                is("Column \"x\" is ambiguous"),
                AMBIGUOUS_COLUMN,
                BAD_REQUEST,
                4006);
    }

    private void isError(Throwable t,
                         Matcher<String> msg,
                         PGErrorStatus pgErrorStatus,
                         HttpResponseStatus httpResponseStatus,
                         int errorCode) {
        var throwable = SQLExceptions.prepareForClientTransmission(t, null);

        var pgError = PGError.fromThrowable(throwable);
        assertThat(pgError.status(), is(pgErrorStatus));
        assertThat(pgError.message(), msg);

        var httpError = HttpError.fromThrowable(throwable);
        assertThat(httpError.errorCode(), is(errorCode));
        assertThat(httpError.httpResponseStatus(), is(httpResponseStatus));
        assertThat(httpError.message(), msg);
    }
}