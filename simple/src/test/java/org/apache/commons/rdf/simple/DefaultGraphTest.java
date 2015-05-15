/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.rdf.simple;

import org.apache.commons.rdf.api.*;
import org.junit.Ignore;

/**
 * Ensure AbstractGraphTest does not crash if the RDFTermFactory throws
 * UnsupportedOperationException
 */

/*
public class DefaultGraphTest extends AbstractGraphTest {

    @Override
    public RDFContext createFactory() {
        // The most minimal RDFTermFactory that would still
        // make sense with a Graph
        return new RDFContext() {
            @Override
            public Graph createGraph() throws UnsupportedOperationException {
                return new GraphImpl(this);
            }

            @Override
            public IRI createIRI(String iri)
                    throws UnsupportedOperationException,
                    IllegalArgumentException {
                return new IRIImpl(this,iri);
            }
        };
    }
}
*/
