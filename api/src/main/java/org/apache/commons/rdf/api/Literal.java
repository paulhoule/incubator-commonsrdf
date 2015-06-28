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
import java.time.*;
import java.time.temporal.Temporal;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * A RDF-1.1 Literal, as defined by <a href=
 * "http://www.w3.org/TR/rdf11-concepts/#section-Graph-Literal" >RDF-1.1
 * Concepts and Abstract Syntax</a>, a W3C Recommendation published on 25
 * February 2014
 */
public interface Literal extends RDFTerm {

    /**
     * The lexical form of this literal, represented by a <a
     * href="http://www.unicode.org/versions/latest/">Unicode string</a>.
     *
     * @return The lexical form of this literal.
     * @see <a
     * href="http://www.w3.org/TR/rdf11-concepts/#dfn-lexical-form">RDF-1.1
     * Literal lexical form</a>
     */
    String getLexicalForm();

    /**
     * The IRI identifying the datatype that determines how the lexical form
     * maps to a literal value.
     *
     * If the datatype IRI is <a
     * href="http://www.w3.org/1999/02/22-rdf-syntax-ns#langString"
     * >http://www.w3.org/1999/02/22-rdf-syntax-ns#langString</a>,
     * {@link #getLanguageTag()} must not return {@link Optional#empty()}, and
     * it must return a valid <a
     * href="http://tools.ietf.org/html/bcp47">BCP47</a> language tag.
     *
     * @return The datatype IRI for this literal.
     * @see <a
     * href="http://www.w3.org/TR/rdf11-concepts/#dfn-datatype-iri">RDF-1.1
     * Literal datatype IRI</a>
     */
    IRI getDatatype();

    /**
     * If and only if the datatype IRI is <a
     * href="http://www.w3.org/1999/02/22-rdf-syntax-ns#langString"
     * >http://www.w3.org/1999/02/22-rdf-syntax-ns#langString</a>, the language
     * tag for this Literal is a non-empty language tag as defined by <a
     * href="http://tools.ietf.org/html/bcp47">BCP47</a>.<br>
     * If the datatype IRI is not <a
     * href="http://www.w3.org/1999/02/22-rdf-syntax-ns#langString"
     * >http://www.w3.org/1999/02/22-rdf-syntax-ns#langString</a>, this method
     * must return {@link Optional#empty()}.
     *
     * Implementation note: If your application requires {@link Serializable}
     * objects, it is best not to store an {@link Optional} in a field. It is
     * recommended to use {@link Optional#ofNullable(Object)} to create the
     * return value for this method.
     *
     * @return The {@link Optional} language tag for this literal. If
     * {@link Optional#isPresent()} returns true, the value returned by
     * {@link Optional#get()} must be a non-empty string conforming to
     * BCP47.
     * @see <a
     * href="http://www.w3.org/TR/rdf11-concepts/#dfn-language-tag">RDF-1.1
     * Literal language tag</a>
     */
    Optional<String> getLanguageTag();

    /**
     * Check it this Literal is equal to another Literal. <blockquote> <a
     * href="http://www.w3.org/TR/rdf11-concepts/#dfn-literal-term">Literal term
     * equality</a>: Two literals are term-equal (the same RDF literal) if and
     * only if the two lexical forms, the two datatype IRIs, and the two
     * language tags (if any) compare equal, character by character. Thus, two
     * literals can have the same value without being the same RDF term.
     * </blockquote>
     *
     * Implementations MAY check the local scope for Literal comparison.
     *
     * Implementations MUST also override {@link #hashCode()} so that two equal
     * Literals produce the same hash code.
     *
     * @param other Another object
     * @return true if other is a Literal and is equal to this
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object other);

    /**
     * Calculate a hash code for this Literal.
     *
     * This method MUST be implemented when implementing {@link #equals(Object)}
     * so that two equal Literals produce the same hash code.
     *
     * @return a hash code value for this Literal.
     * @see Object#hashCode()
     */
    @Override
    public int hashCode();

    //
    // These "straw man" implementations don't check the underlying data type but maybe they
    // don't need to...
    //

    public default byte asByte() {
        return Byte.parseByte(getLexicalForm());
    }

    public default short asShort() {
        return Short.parseShort(getLexicalForm());
    }

    public default int asInteger() {
        return Integer.parseInt(getLexicalForm());
    }

    public default long asLong() {
        return Long.parseLong(getLexicalForm());
    }

    public default float asFloat() {
        return Float.parseFloat(getLexicalForm());
    }

    public default double asDouble() {
        return Float.parseFloat(getLexicalForm());
    }

    public default BigInteger asBigInteger() {
        return new BigInteger(getLexicalForm());
    }

    public default BigDecimal asBigDecimal() {
        return new BigDecimal(getLexicalForm());
    }

    public default boolean asBoolean() {
        return Boolean.parseBoolean(getLexicalForm());
    }

    //
    // lame as hell;  it is deliberate that we're not checking the type because we want to
    // be permissive,  particularly to parse rawstrings.
    //

    public default Temporal asTemporal() {
        String lf=getLexicalForm();
        if (lf.charAt(2)==':') {
            if(lf.contains("+") || lf.contains("-") || lf.contains("Z"))
                return OffsetTime.parse(lf);

            return LocalTime.parse(lf);
        }

        if (lf.length()>19) {
            return OffsetDateTime.parse(lf);
        } else if(lf.length()>10) {
            return LocalDateTime.parse(lf);
        } else {
            return LocalDate.parse(lf);
        }
    }

    //
    // the list of integer comes from part 17.1 of
    // http://www.w3.org/TR/sparql11-query/
    //
    public default Object asDynamic() {
        switch(getDatatype().getIRIString()) {
            case "http://www.w3.org/2001/XMLSchema#integer":
            case "http://www.w3.org/2001/XMLSchema#unsignedLong":
            case "http://www.w3.org/2001/XMLSchema#nonNegativeInteger":
            case "http://www.w3.org/2001/XMLSchema#positiveInteger":
                return asBigInteger();

            case "http://www.w3.org/2001/XMLSchema#byte":
            case "http://www.w3.org/2001/XMLSchema#short":
            case "http://www.w3.org/2001/XMLSchema#long":
            case "http://www.w3.org/2001/XMLSchema#int":
            case "http://www.w3.org/2001/XMLSchema#unsignedInt":
            case "http://www.w3.org/2001/XMLSchema#unsignedShort":
            case "http://www.w3.org/2001/XMLSchema#unsignedByte":
                return asLong();

            case "http://www.w3.org/2001/XMLSchema#decimal":
                return asBigDecimal();

            case "http://www.w3.org/2001/XMLSchema#float":
                return asFloat();

            case "http://www.w3.org/2001/XMLSchema#double":
                return asDouble();

            case "http://www.w3.org/2001/XMLSchema#date":
            case "http://www.w3.org/2001/XMLSchema#time":
            case "http://www.w3.org/2001/XMLSchema#dateTime":
                return asDouble();

        }

        return asRawString();
    }

    //
    // we don't really need this...
    //

    public default String asRawString() {
        return getLexicalForm();
    }

    //
    // this function is not fully filled in at this point in time
    //

    public default Object asObject() {
        switch(getDatatype().getIRIString()) {
            case "http://www.w3.org/2001/XMLSchema#string":
                return asRawString();
            case "http://www.w3.org/2001/XMLSchema#float":
                return asFloat();
            case "http://www.w3.org/2001/XMLSchema#double":
                return asDouble();
        }

        return ntriplesString();
    };

}
