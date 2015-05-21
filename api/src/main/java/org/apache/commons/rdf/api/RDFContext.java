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
package org.apache.commons.rdf.api;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Locale;

/**
 * Factory for creating RDFTerm and Graph instances.
 *
 * It is not specified how an implementation should provide a RDFTermFactory.
 *
 * If an implementation does not support a particular method (e.g. it requires
 * additional parameters or can't create graphs), then it MAY throw
 * UnsupportedOperationException, as provided by the default implementations
 * here.
 *
 * If a factory method does not allow or support a provided parameter, e.g.
 * because an IRI is considered invalid, then it SHOULD throw
 * IllegalArgumentException.
 *
 * @see RDFTerm
 * @see Graph
 */
public interface RDFContext {

    /**
     * Create a new blank node.
     * <p>
     * The returned blank node MUST NOT be equal to any existing
     * {@link BlankNode} instances according to {@link BlankNode#equals(Object)}.
     *
     * @return A new, unique {@link BlankNode}
     * @throws UnsupportedOperationException
     *             If the operation is not supported.
     */
    default BlankNode createBlankNode() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "createBlankNode() not supported");
    }

    /**
     * Create a blank node based on the given name.
     * <p>
     * All {@link BlankNode}s created with the given <code>name</code>
     * <em>on a particular instance</em> of <code>RDFTermFactory</code> MUST be
     * equivalent according to {@link BlankNode#equals(Object)},
     * <p>
     * The returned BlankNode MUST NOT be equal to <code>BlankNode</code>
     * instances returned for any other <code>name</code> or those returned from
     * {@link #createBlankNode()}.
     * <p>
     * The returned BlankNode SHOULD NOT be equivalent to any BlankNodes created
     * on a <em>different</em> <code>RDFTermFactory</code> instance, e.g.
     * different instances of <code>RDFTermFactory</code> should produce
     * different blank nodes for the same <code>name</code> unless they
     * purposely are intending to create equivalent {@link BlankNode}
     * instances (e.g. a reinstated {@link Serializable} factory).
     *
     * @param name
     *            A non-empty, non-null, String that is unique to this blank
     *            node in the context of this {@link RDFContext}.
     * @return A BlankNode for the given name
     * @throws UnsupportedOperationException
     *             If the operation is not supported.
     */
    default BlankNode createBlankNode(String name)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "createBlankNode(String) not supported");
    }

    /**
     * Create a new graph.
     *
     * It is undefined if the graph will be persisted by any underlying storage
     * mechanism.
     *
     * {@link BlankNode} objects added to the {@link Graph} returned from this
     * method SHOULD be mapped using the {@link #createBlankNode(String)} of
     * this factory, called using the {@link BlankNode#uniqueReference()} as
     * the parameter, before they are inserted into the Graph.
     *
     * @return A new Graph
     * @throws UnsupportedOperationException If the operation is not supported.
     */
    default Graph createGraph() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("createGraph() not supported");
    }

    /**
     * Create an IRI from a (possibly escaped) String.
     *
     * The provided iri string MUST be valid according to the <a
     * href="http://www.w3.org/TR/rdf11-concepts/#dfn-iri">W3C RDF-1.1 IRI</a>
     * definition.
     *
     * @param iri Internationalized Resource Identifier
     * @return A new IRI
     * @throws IllegalArgumentException      If the provided string is not acceptable, e.g. does not
     *                                       conform to the RFC3987 syntax.
     * @throws UnsupportedOperationException If the operation is not supported.
     */
    default IRI createIRI(String iri) throws IllegalArgumentException,
            UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "createIRI(String) not supported");
    }

    /**
     * Create a simple literal.
     *
     * The provided lexical form should not be escaped in any sense, e.g. should
     * not include "quotes" unless those are part of the literal value.
     *
     * The returned Literal MUST have a {@link Literal#getLexicalForm()} that is
     * equal to the provided lexical form, MUST NOT have a
     * {@link Literal#getLanguageTag()} present, and SHOULD return a
     * {@link Literal#getDatatype()} that is equal to the IRI
     * <code>http://www.w3.org/2001/XMLSchema#string</code>.
     *
     * @param lexicalForm The literal value in plain text
     * @return The created Literal
     * @throws IllegalArgumentException      If the provided lexicalForm is not acceptable, e.g. because
     *                                       it is too large for an underlying storage.
     * @throws UnsupportedOperationException If the operation is not supported.
     */
    default Literal createLiteral(String lexicalForm)
            throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "createLiteral(String) not supported");
    }

    /**
     * Create a literal with the specified data type.
     *
     * The provided lexical form should not be escaped in any sense, e.g. should
     * not include "quotes" unless those are part of the literal value.
     *
     * It is RECOMMENDED that the provided dataType is one of the <a
     * href="http://www.w3.org/TR/rdf11-concepts/#xsd-datatypes">RDF-compatible
     * XSD types</a>.
     *
     * The provided lexical form SHOULD be in the <a
     * href="http://www.w3.org/TR/rdf11-concepts/#dfn-lexical-space">lexical
     * space</a> of the provided dataType.
     *
     * The returned Literal SHOULD have a {@link Literal#getLexicalForm()} that
     * is equal to the provided lexicalForm, MUST NOT have a
     * {@link Literal#getLanguageTag()} present, and MUST return a
     * {@link Literal#getDatatype()} that is equivalent to the provided dataType
     * IRI.
     *
     * @param lexicalForm The literal value
     * @param dataType    The data type IRI for the literal value, e.g.
     *                    <code>http://www.w3.org/2001/XMLSchema#integer</code>
     * @return The created Literal
     * @throws IllegalArgumentException      If any of the provided arguments are not acceptable, e.g.
     *                                       because the provided dataType is not permitted.
     * @throws UnsupportedOperationException If the operation is not supported.
     */
    default Literal createLiteral(String lexicalForm, IRI dataType)
            throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "createLiteral(String) not supported");
    }

    /**
     * Create a language-tagged literal.
     *
     * The provided lexical form should not be escaped in any sense, e.g. should
     * not include "quotes" unless those are part of the literal value.
     *
     * The provided language tag MUST be valid according to <a
     * href="http://tools.ietf.org/html/bcp47">BCP47</a>, e.g. <code>en</code>.
     *
     * The provided language tag <a
     * href="http://www.w3.org/TR/rdf11-concepts/#dfn-language-tagged-string"
     * >MAY be converted to lower case</a>.
     *
     * The returned Literal SHOULD have a {@link Literal#getLexicalForm()} which
     * is equal to the provided lexicalForm, MUST return a
     * {@link Literal#getDatatype()} that is equal to the IRI
     * <code>http://www.w3.org/1999/02/22-rdf-syntax-ns#langString</code>, and
     * MUST have a {@link Literal#getLanguageTag()} present which SHOULD be
     * equal to the provided language tag (compared as
     * {@link String#toLowerCase(Locale)} using {@link Locale#ENGLISH}).
     *
     * @param lexicalForm The literal value
     * @param languageTag The non-empty language tag as defined by <a
     *                    href="http://tools.ietf.org/html/bcp47">BCP47</a>
     * @return The created Literal
     * @throws IllegalArgumentException      If the provided values are not acceptable, e.g. because the
     *                                       languageTag was syntactically invalid.
     * @throws UnsupportedOperationException If the operation is not supported.
     */
    default Literal createLiteral(String lexicalForm, String languageTag)
            throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "createLiteral(String,String) not supported");
    }

    /**
     * Create a triple.
     *
     * The returned Triple SHOULD have a {@link Triple#getSubject()} that is
     * equal to the provided subject, a {@link Triple#getPredicate()} that is
     * equal to the provided predicate, and a {@link Triple#getObject()} that is
     * equal to the provided object.
     *
     * @param subject   The IRI or BlankNode that is the subject of the triple
     * @param predicate The IRI that is the predicate of the triple
     * @param object    The IRI, BlankNode or Literal that is the object of the triple
     * @return The created Triple
     * @throws IllegalArgumentException      If any of the provided arguments are not acceptable, e.g.
     *                                       because a Literal has a lexicalForm that is too large for an
     *                                       underlying storage.
     * @throws UnsupportedOperationException If the operation is not supported.
     */
    default Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
                                RDFTerm object) throws IllegalArgumentException,
            UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "createTriple(BlankNodeOrIRI,IRI,RDFTerm) not supported");
    }

    // -- below here,  default methods actually do something

    default Literal createLiteral(long value) {
        return createLiteral(Long.toString(value),createIRI("http://www.w3.org/2001/XMLSchema#integer"));
    }

    //
    // this opens the possibility of having
    //
    // (i) a smaller internal representation,  and/or
    // (ii) being more specific about narrower data types
    //
    //

    default Literal createLiteral(byte value) {
        return createLiteral((long) value);
    }

    default Literal createLiteral(short value) {
        return createLiteral((long) value);
    }

    default Literal createLiteral(int value) {
        return createLiteral((long) value);
    }

    default Literal createLiteral(BigInteger value) {
        return createLiteral(value.toString(),createIRI("http://www.w3.org/2001/XMLSchema#integer"));
    }

    default Literal createLiteral(BigDecimal value) {
        return createLiteral(value.toString(),createIRI("http://www.w3.org/2001/XMLSchema#decimal"));
    }

    default Literal createLiteral(float value) {
        return createLiteral(Float.toString(value),createIRI("http://www.w3.org/2001/XMLSchema#float"));
    }

    default Literal createLiteral(double value) {
        return createLiteral(Double.toString(value),createIRI("http://www.w3.org/2001/XMLSchema#double"));
    }

    default Literal createLiteral(boolean value) {
        return createLiteral(Boolean.toString(value),createIRI("http://www.w3.org/2001/XMLSchema#boolean"));
    }

    //
    // This may collapse the hh:mm:ss part if mm or ss are zero,  so we may want to amend the form.  We
    // do want permissive parsing on the way back
    //

    default Literal createLiteral(OffsetDateTime value) {
        return createLiteral(value.toString(),createIRI("http://www.w3.org/2001/XMLSchema#datetime"));
    }

    default Literal createLiteral(LocalDateTime value) {
        return createLiteral(value.toString(),createIRI("http://www.w3.org/2001/XMLSchema#datetime"));
    }

    default Literal createLiteral(LocalDate value) {
        return createLiteral(value.toString(),createIRI("http://www.w3.org/2001/XMLSchema#date"));
    }

    default Literal createLiteral(LocalTime value) {
        return createLiteral(value.toString(),createIRI("http://www.w3.org/2001/XMLSchema#time"));
    }

    //
    //
    // many kinds of literal types could be worth adding,  such as
    //
    // byte[] -> hexBytes
    // xsd:gYearMonth
    // xsd:gYear	xsd:gMonthDay	xsd:gDay	xsd:gMonth
    // unsigned ints of various sorts (often you use some ordinary integer and just treat it differently.
    // Guava has libraries for doing this AND unsigned int types.
    //

    //
    // right now this is a stub to illustrate the concept.  The idea is that we need the above polymorphic
    // functions for speed when the type is known at compile time but sometimes you just want to turn an object
    // to a literal and don't care what it is
    //
    // a smarter implementation might be configured with a hashmap and certainly specialist RDFContext(s)
    // should be able to handle special types
    //

    default Literal createLiteralDynamic(Object o) {
        if(o instanceof String) {
            return createLiteral((String) o);
        } else if(o instanceof Float) {
            return createLiteral((float) o);
        } else if(o instanceof Double) {
            return createLiteral((double) o);
        }

        throw new IllegalArgumentException("Cannot interpret object of type ["+o.getClass()+"] as an RDF literal");
    };

    //
    // now under ideal circumstances you should be able to create a triple without having to
    // create a Literal()
    //

    default Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
                                String text,String language) throws IllegalArgumentException,
            UnsupportedOperationException {
        return createTriple(subject,predicate,createLiteral(text,language));
    }

    default Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
                                String rawString) throws IllegalArgumentException,
            UnsupportedOperationException {
        return createTriple(subject,predicate,createLiteral(rawString));
    }

    default Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
                                String lexicalForm,IRI datatype) throws IllegalArgumentException,
            UnsupportedOperationException {
        return createTriple(subject,predicate,createLiteral(lexicalForm, datatype));
    }

    default Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
                                long value) throws IllegalArgumentException,
            UnsupportedOperationException {
        return createTriple(subject,predicate,createLiteral(value));
    }

    default Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
                                int value) throws IllegalArgumentException,
            UnsupportedOperationException {
        return createTriple(subject,predicate,createLiteral(value));
    }

    default Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
                                short value) throws IllegalArgumentException,
            UnsupportedOperationException {
        return createTriple(subject,predicate,createLiteral(value));
    }

    default Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
                                     byte value) throws IllegalArgumentException,
            UnsupportedOperationException {
        return createTriple(subject,predicate,createLiteral(value));
    }

    default Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
                                     BigInteger value) throws IllegalArgumentException,
            UnsupportedOperationException {
        return createTriple(subject,predicate,createLiteral(value));
    }

    default Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
                                BigDecimal value) throws IllegalArgumentException,
            UnsupportedOperationException {
        return createTriple(subject,predicate,createLiteral(value));
    }

    default Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
                                float value) throws IllegalArgumentException,
            UnsupportedOperationException {
        return createTriple(subject,predicate,createLiteral(value));
    }

    default Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
                                double value) throws IllegalArgumentException,
            UnsupportedOperationException {
        return createTriple(subject,predicate,createLiteral(value));
    }

    default Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
                                OffsetDateTime value) throws IllegalArgumentException,
            UnsupportedOperationException {
        return createTriple(subject,predicate,createLiteral(value));
    }

    default Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
                                     LocalDateTime value) throws IllegalArgumentException,
            UnsupportedOperationException {
        return createTriple(subject,predicate,createLiteral(value));
    }

    default Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
                                LocalDate value) throws IllegalArgumentException,
            UnsupportedOperationException {
        return createTriple(subject,predicate,createLiteral(value));
    }

    default Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
                                LocalTime value) throws IllegalArgumentException,
            UnsupportedOperationException {
        return createTriple(subject,predicate,createLiteral(value));
    }

    default Triple createTripleDynamic(BlankNodeOrIRI subject, IRI predicate,
                                Object value) throws IllegalArgumentException,
            UnsupportedOperationException {
        return createTriple(subject,predicate,createLiteralDynamic(value));
    }
}
