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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.TimeZone;

/**
 * Test RDFTermFactory implementation (and thus its RDFTerm implementations)
 * <p>
 * To add to your implementation's tests, create a subclass with a name ending
 * in <code>Test</code> and provide {@link #createFactory()} which minimally
 * supports one of the operations, but ideally supports all operations.
 *
 * @see RDFContext
 */
public abstract class AbstractRDFTermFactoryTest {

    private RDFContext factory;

    /**
     * testCreate a new, distinct {@link RDFContext} object using the
     * implementation being tested here.
     *
     * @return a new, distinct {@link RDFContext} object using the
     * implementation being tested here
     */
    public abstract RDFContext createFactory();

    @Before
    public void setUp() {
        factory = createFactory();
    }

    @Test
    public void testCreateBlankNode() throws Exception {
        BlankNode bnode;
        try {
            bnode = factory.createBlankNode();
            assertEquals(factory,bnode.getContext());
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }

        BlankNode bnode2 = factory.createBlankNode();
        assertNotEquals(
                "Second blank node has not got a unique internal identifier",
                bnode.uniqueReference(), bnode2.uniqueReference());
    }

    @Test
    public void testCreateBlankNodeIdentifierEmpty() throws Exception {
        try {
            factory.createBlankNode("");
        } catch (UnsupportedOperationException e) {
            Assume.assumeNoException(e);
        } catch (IllegalArgumentException e) {
            // Expected exception
        }
    }

    @Test
    public void testCreateBlankNodeIdentifier() throws Exception {
        try {
            factory.createBlankNode("example1");
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }
    }

    @Test
    public void testCreateBlankNodeIdentifierTwice() throws Exception {
        BlankNode bnode1, bnode2, bnode3;
        try {
            bnode1 = factory.createBlankNode("example1");
            bnode2 = factory.createBlankNode("example1");
            bnode3 = factory.createBlankNode("differ");
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }
        // We don't know what the identifier is, but it MUST be the same
        assertEquals(bnode1.uniqueReference(), bnode2.uniqueReference());
        // We don't know what the ntriplesString is, but it MUST be the same
        assertEquals(bnode1.ntriplesString(), bnode2.ntriplesString());
        // and here it MUST differ
        assertNotEquals(bnode1.uniqueReference(),
                bnode3.uniqueReference());
        assertNotEquals(bnode1.ntriplesString(), bnode3.ntriplesString());
    }

    @Test
    public void testCreateGraph() {
        Graph graph;
        try {
            graph = factory.createGraph();
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }

        assertEquals(factory,graph.getContext());
        assertEquals("Graph was not empty", 0, graph.size());
        graph.add(factory.createBlankNode(),
                factory.createIRI("http://example.com/"),
                factory.createBlankNode());

        Graph graph2 = factory.createGraph();
        assertNotSame(graph, graph2);
        assertEquals("Graph was empty after adding", 1, graph.size());
        assertEquals("New graph was not empty", 0, graph2.size());
    }

    @Test
    public void testCreateIRI() throws Exception {
        IRI example;
        try {
            example = factory.createIRI("http://example.com/");
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException("createIRI not supported", ex);
            return;
        }

        assertEquals(factory,example.getContext());
        assertEquals("http://example.com/", example.getIRIString());
        assertEquals("<http://example.com/>", example.ntriplesString());

        IRI term = factory.createIRI("http://example.com/vocab#term");
        assertEquals("http://example.com/vocab#term", term.getIRIString());
        assertEquals("<http://example.com/vocab#term>", term.ntriplesString());

        // and now for the international fun!

        IRI latin1 = factory.createIRI("http://accént.example.com/première");
        assertEquals("http://accént.example.com/première",
                latin1.getIRIString());
        assertEquals("<http://accént.example.com/première>",
                latin1.ntriplesString());

        IRI cyrillic = factory.createIRI("http://example.испытание/Кириллица");
        assertEquals("http://example.испытание/Кириллица",
                cyrillic.getIRIString());
        assertEquals("<http://example.испытание/Кириллица>",
                cyrillic.ntriplesString());

        IRI deseret = factory.createIRI("http://𐐀.example.com/𐐀");
        assertEquals("http://𐐀.example.com/𐐀", deseret.getIRIString());
        assertEquals("<http://𐐀.example.com/𐐀>", deseret.ntriplesString());
    }

    @Test
    public void testCreateIRIRelative() throws Exception {
        // Although relative IRIs are defined in
        // http://www.w3.org/TR/rdf11-concepts/#section-IRIs
        // it is not a requirement for an implementation to support
        // it (all instances of an relative IRI should eventually
        // be possible to resolve to an absolute IRI)
        try {
            factory.createIRI("../relative");
        } catch (UnsupportedOperationException | IllegalArgumentException ex) {
            Assume.assumeNoException(ex);
            return;
        }

        IRI relative = factory.createIRI("../relative");
        assertEquals("../relative", relative.getIRIString());
        assertEquals("<../relative>", relative.ntriplesString());

        assertEquals(factory, relative.getContext());

        IRI relativeTerm = factory.createIRI("../relative#term");
        assertEquals("../relative#term", relativeTerm.getIRIString());
        assertEquals("<../relative#term>", relativeTerm.ntriplesString());

        IRI emptyRelative = factory.createIRI(""); // <> equals the base URI
        assertEquals("", emptyRelative.getIRIString());
        assertEquals("<>", emptyRelative.ntriplesString());
    }

    @Test
    public void testCreateLiteral() throws Exception {
        Literal example;
        try {
            example = factory.createLiteral("Example");
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }

        assertEquals(factory, example.getContext());
        assertEquals("Example", example.getLexicalForm());
        assertFalse(example.getLanguageTag().isPresent());
        assertEquals("http://www.w3.org/2001/XMLSchema#string", example
                .getDatatype().getIRIString());
        // http://lists.w3.org/Archives/Public/public-rdf-comments/2014Dec/0004.html
        assertEquals("\"Example\"", example.ntriplesString());
    }

    @Test
    public void testCreateLiteralDateTime() throws Exception {
        Literal dateTime;
        try {
            dateTime = factory
                    .createLiteral(
                            "2014-12-27T00:50:00T-0600",
                            factory.createIRI("http://www.w3.org/2001/XMLSchema#dateTime"));
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }
        assertEquals(factory, dateTime.getContext());
        assertEquals("2014-12-27T00:50:00T-0600", dateTime.getLexicalForm());
        assertFalse(dateTime.getLanguageTag().isPresent());
        assertEquals("http://www.w3.org/2001/XMLSchema#dateTime", dateTime
                .getDatatype().getIRIString());
        assertEquals(
                "\"2014-12-27T00:50:00T-0600\"^^<http://www.w3.org/2001/XMLSchema#dateTime>",
                dateTime.ntriplesString());
    }

    @Test
    public void testCreateLiteralLang() throws Exception {
        Literal example;
        try {
            example = factory.createLiteral("Example", "en");
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }

        assertEquals(factory, example.getContext());
        assertEquals("Example", example.getLexicalForm());
        assertEquals("en", example.getLanguageTag().get());
        assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString",
                example.getDatatype().getIRIString());
        assertEquals("\"Example\"@en", example.ntriplesString());
    }

    @Test
    public void testCreateLiteralLangISO693_3() throws Exception {
        // see https://issues.apache.org/jira/browse/JENA-827
        Literal vls;
        try {
            vls = factory.createLiteral("Herbert Van de Sompel", "vls"); // JENA-827
            // reference
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }

        assertEquals(factory, vls.getContext());
        assertEquals("vls", vls.getLanguageTag().get());
        assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString",
                vls.getDatatype().getIRIString());
        assertEquals("\"Herbert Van de Sompel\"@vls", vls.ntriplesString());
    }

    @Test
    public void testCreateLiteralString() throws Exception {
        Literal example;
        try {
            example = factory.createLiteral("Example", factory
                    .createIRI("http://www.w3.org/2001/XMLSchema#string"));
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }
        assertEquals(factory,example.getContext());
        assertEquals("Example", example.getLexicalForm());
        assertFalse(example.getLanguageTag().isPresent());
        assertEquals("http://www.w3.org/2001/XMLSchema#string", example
                .getDatatype().getIRIString());
        // http://lists.w3.org/Archives/Public/public-rdf-comments/2014Dec/0004.html
        assertEquals("\"Example\"", example.ntriplesString());
    }

    @Test
    public void testCreateTripleBnodeBnode() {
        BlankNode subject;
        IRI predicate;
        BlankNode object;
        Triple triple;
        try {
            subject = factory.createBlankNode("b1");
            predicate = factory.createIRI("http://example.com/pred");
            object = factory.createBlankNode("b2");
            triple = factory.createTriple(subject, predicate, object);
            assertEquals(factory,triple.getContext());
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }

        // bnode equivalence should be OK as we used the same
        // factory and have not yet inserted Triple into a Graph
        assertEquals(subject, triple.getSubject());
        assertEquals(predicate, triple.getPredicate());
        assertEquals(object, triple.getObject());
    }

    @Test
    public void testCreateTripleBnodeIRI() {
        BlankNode subject;
        IRI predicate;
        IRI object;
        Triple triple;
        try {
            subject = factory.createBlankNode("b1");
            predicate = factory.createIRI("http://example.com/pred");
            object = factory.createIRI("http://example.com/obj");
            triple = factory.createTriple(subject, predicate, object);
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }

        // bnode equivalence should be OK as we used the same
        // factory and have not yet inserted Triple into a Graph
        assertEquals(factory, triple.getContext());
        assertEquals(subject, triple.getSubject());
        assertEquals(predicate, triple.getPredicate());
        assertEquals(object, triple.getObject());
    }

    @Test
    public void testCreateTripleBnodeTriple() {
        BlankNode subject;
        IRI predicate;
        Literal object;
        Triple triple;
        try {
            subject = factory.createBlankNode();
            predicate = factory.createIRI("http://example.com/pred");
            object = factory.createLiteral("Example", "en");
            triple = factory.createTriple(subject, predicate, object);
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }

        // bnode equivalence should be OK as we used the same
        // factory and have not yet inserted Triple into a Graph
        assertEquals(factory,triple.getContext());
        assertEquals(subject, triple.getSubject());
        assertEquals(predicate, triple.getPredicate());
        assertEquals(object, triple.getObject());
    }

    @Test
    public void testPossiblyInvalidBlankNode() throws Exception {
        BlankNode withColon;
        try {
            withColon = factory.createBlankNode("with:colon");
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException("createBlankNode(String) not supported",
                    ex);
            return;
        } catch (IllegalArgumentException ex) {
            // Good!
            return;
        }
        // Factory allows :colon, which is OK as long as it's not causing an
        // invalid ntriplesString
        assertFalse(withColon.ntriplesString().contains("with:colon"));

        // and creating it twice gets the same ntriplesString
        assertEquals(withColon.ntriplesString(),
                factory.createBlankNode("with:colon").ntriplesString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIRI() throws Exception {
        try {
            factory.createIRI("<no_brackets>");
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException("createIRI not supported", ex);
            return;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidLiteralLang() throws Exception {
        try {
            factory.createLiteral("Example", "with space");
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(
                    "createLiteral(String,String) not supported", ex);
            return;
        }
    }

    @Test(expected = Exception.class)
    public void testInvalidTriplePredicate() {
        BlankNode subject = factory.createBlankNode("b1");
        BlankNode predicate = factory.createBlankNode("b2");
        BlankNode object = factory.createBlankNode("b3");
        factory.createTriple(subject, (IRI) predicate, object);
    }

    @Test
    public void testLongRoundtrip() {
        try {
            Literal seventyFive = factory.createLiteral(75L);
            assertEquals(75L, seventyFive.asLong());
            assertEquals("http://www.w3.org/2001/XMLSchema#integer", seventyFive.getDatatype().getIRIString());
        } catch (UnsupportedOperationException ex) {
                Assume.assumeNoException(ex);
                return;
        }
    }

    @Test
    public void testByteRoundtrip() {
        try {
            Literal eight = factory.createLiteral((byte) 8);
            assertEquals((byte) 8, eight.asByte());
            assertEquals("http://www.w3.org/2001/XMLSchema#integer",eight.getDatatype().getIRIString());
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }
    }

    @Test
    public void testShortRoundtrip() {
        try {
        Literal fourThousand = factory.createLiteral((short) 4000);
        assertEquals((short) 4000, fourThousand.asShort());
        assertEquals("http://www.w3.org/2001/XMLSchema#integer",fourThousand.getDatatype().getIRIString());
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }
    }

    @Test
    public void testIntRoundtrip() {
        try {
        Literal twoMillion = factory.createLiteral((int) 2000000);
        assertEquals(2000000, twoMillion.asInteger());
        assertEquals("http://www.w3.org/2001/XMLSchema#integer",twoMillion.getDatatype().getIRIString());
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }
    }

    @Test
    public void testBigIntegerRoundtrip() {
        try {
            Literal threeNines= factory.createLiteral(new BigInteger("999"));
            assertEquals(new BigInteger("999"), threeNines.asBigInteger());
            assertEquals("http://www.w3.org/2001/XMLSchema#integer",threeNines.getDatatype().getIRIString());
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }
    }

    @Test
    public void testBigDecimalRoundtrip() {
        try {
            Literal littlePi = factory.createLiteral(new BigDecimal("3.14"));
            assertEquals(new BigDecimal("3.14"), littlePi.asBigDecimal());
            assertEquals("http://www.w3.org/2001/XMLSchema#decimal", littlePi.getDatatype().getIRIString());
        } catch (UnsupportedOperationException ex) {
        Assume.assumeNoException(ex);
        return;
    }
    }

    @Test
    public void testFloatRoundtrip() {
        try {
            Literal littleE= factory.createLiteral(2.718f);
            assertEquals(2.718f,littleE.asFloat(),0.00001f);
            assertEquals("http://www.w3.org/2001/XMLSchema#float",littleE.getDatatype().getIRIString());
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }
    }

    @Test
    public void testDoubleRoundtrip() {
        try {
            Literal littleDelta= factory.createLiteral(4.669201);
            assertEquals(4.669201,littleDelta.asDouble(),0.000001);
            assertEquals("http://www.w3.org/2001/XMLSchema#double", littleDelta.getDatatype().getIRIString());
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }
    }

    @Test
    public void testBooleanRoundtrip() {
        try {
            Literal yes = factory.createLiteral(true);
            assertEquals(true, yes.asBoolean());
            assertEquals("http://www.w3.org/2001/XMLSchema#boolean", yes.getDatatype().getIRIString());
            Literal no = factory.createLiteral(false);
            assertEquals(false, no.asBoolean());
            assertEquals("http://www.w3.org/2001/XMLSchema#boolean", no.getDatatype().getIRIString());
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }
    }

    @Test
    public void testOffsetDateTime() {
        try {
            OffsetDateTime thenNative=OffsetDateTime.of(
                    LocalDateTime.of(
                            LocalDate.of(2011, Month.MARCH, 24),
                            LocalTime.of(15, 16, 17)) ,
                            ZoneOffset.ofHours(-5)
                    );
            Literal thenRDF= factory.createLiteral(thenNative);
            assertEquals(thenNative,thenRDF.asTemporal());
            assertEquals("http://www.w3.org/2001/XMLSchema#dateTime",thenRDF.getDatatype().getIRIString());
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }
    }

    @Test
    public void testLocalDateTime() {
        try {
            LocalDateTime thenNative=
                    LocalDateTime.of(
                            LocalDate.of(2011, Month.MARCH, 24),
                            LocalTime.of(15, 16, 17)
            );


            Literal thenRDF= factory.createLiteral(thenNative);
            assertEquals(thenNative,thenRDF.asTemporal());
            assertEquals("http://www.w3.org/2001/XMLSchema#dateTime",thenRDF.getDatatype().getIRIString());
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }
    }

    @Test
    public void testLocalDate() {
        try {
            LocalDate thenNative=LocalDate.of(1986, Month.JANUARY, 24);

            Literal thenRDF= factory.createLiteral(thenNative);
            assertEquals(thenNative,thenRDF.asTemporal());
            assertEquals("http://www.w3.org/2001/XMLSchema#date",thenRDF.getDatatype().getIRIString());
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }
    }

    @Test
    public void testLocalTime() {
        try {
            LocalTime thenNative=LocalTime.of(8, 37, 52);
            Literal thenRDF= factory.createLiteral(thenNative);
            assertEquals(thenNative,thenRDF.asTemporal());
            assertEquals("http://www.w3.org/2001/XMLSchema#time",thenRDF.getDatatype().getIRIString());
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }
    }

    @Test
    public void testOffsetTime() {
        try {
            OffsetTime thenNative=OffsetTime.of(8, 37, 52, 371*1000*1000, ZoneOffset.ofHours(5));
            Literal thenRDF= factory.createLiteral(thenNative);
            assertEquals(thenNative,thenRDF.asTemporal());
            assertEquals("http://www.w3.org/2001/XMLSchema#time",thenRDF.getDatatype().getIRIString());
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
            return;
        }
    }

}
