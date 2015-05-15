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

import java.util.UUID;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDFContext;
import org.apache.commons.rdf.api.Triple;

/**
 * A simple implementation of RDFTermFactory.
 * <p>
 * The {@link RDFTerm} and {@link Graph} instances created by this factory are
 * simple in-memory Implementations that are not thread-safe or efficient, but
 * which may be useful for testing and prototyping purposes.
 */
public class SimpleRDFTermFactory implements RDFContext {

    /** Unique salt per instance, for {@link #createBlankNode(String)}
     */
    private final UUID SALT = UUID.randomUUID();

    @Override
    public BlankNode createBlankNode() {
        return new BlankNodeImpl(this);
    }

    @Override
    public BlankNode createBlankNode(String name) {
        return new BlankNodeImpl(this,SALT, name);
    }

    @Override
    public Graph createGraph() {
        // Creates a GraphImpl object using this object as the factory for
        // delegating all object creation to
        return new GraphImpl(this);
    }

    @Override
    public IRI createIRI(String iri) {
        IRI result = new IRIImpl(this,iri);
        // Reuse any IRI objects already created in Types
        return Types.get(result).orElse(result);
    }

    @Override
    public Literal createLiteral(String literal) {
        return new LiteralImpl(this,literal);
    }

    @Override
    public Literal createLiteral(String literal, IRI dataType) {
        return new LiteralImpl(this,literal, dataType);
    }

    @Override
    public Literal createLiteral(String literal, String language) {
        return new LiteralImpl(this,literal, language);
    }

    @Override
    public Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
                               RDFTerm object) {
        return new TripleImpl(this,subject, predicate, object);
    }
}
