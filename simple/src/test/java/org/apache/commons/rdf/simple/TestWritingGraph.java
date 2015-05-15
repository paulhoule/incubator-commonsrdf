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

import static org.junit.Assert.assertEquals;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFContext;
import org.apache.commons.rdf.api.Triple;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test writing graph
 */
public class TestWritingGraph {

    /*
     * 200k triples should do - about 7 MB on disk. Override with
     * -Dtriples=20000000 to exercise your memory banks.
     */
    private static final long TRIPLES = Long.getLong("triples", 200000L);

    /**
     * Run tests with -Dkeepfiles=true to inspect /tmp files *
     */
    private static boolean KEEP_FILES = Boolean.getBoolean("keepfiles");

    private static Graph graph;

    private static RDFContext factory;

    @BeforeClass
    public static void createGraph() throws Exception {
        factory = new SimpleRDFTermFactory();
        graph = factory.createGraph();
        IRI subject = factory.createIRI("subj");
        IRI predicate = factory.createIRI("pred");
        List<IRI> types = new ArrayList<>(Types.values());
        // Ensure we don't try to create a literal with rdf:langString but
        // without a language tag
        types.remove(Types.RDF_LANGSTRING);
        Collections.shuffle(types);
        for (int i = 0; i < TRIPLES; i++) {
            if (i % 11 == 0) {
                graph.add(subject, predicate,
                        factory.createBlankNode("Example " + i));
            } else if (i % 5 == 0) {
                graph.add(subject, predicate,
                        factory.createLiteral("Example " + i, "en"));
            } else if (i % 3 == 0) {
                graph.add(
                        subject,
                        predicate,
                        factory.createLiteral("Example " + i,
                                types.get(i % types.size())));
            } else {
                graph.add(subject, predicate,
                        factory.createLiteral("Example " + i));
            }
        }
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        graph.clear();
        graph = null;
    }

    @Test
    public void createGraphTiming() throws Exception {
        createGraph();
    }

    @Test
    public void countQuery() {
        IRI subject = factory.createIRI("subj");
        IRI predicate = factory.createIRI("pred");
        long count = graph.getTriples(subject, predicate, null).unordered()
                .parallel().count();
        //System.out.println("Counted - " + count);
        assertEquals(count, TRIPLES);
    }

    public static String tripleAsString(Triple t) {
        return t.getSubject().ntriplesString() + " "
                + t.getPredicate().ntriplesString() + " " +
                t.getObject().ntriplesString() + " .";
    }

    @Test
    public void writeGraphFromStream() throws Exception {
        Path graphFile = Files.createTempFile("graph", ".nt");
        if (KEEP_FILES) {
            System.out.println("From stream: " + graphFile);
        } else {
            graphFile.toFile().deleteOnExit();
        }

        Stream<CharSequence> stream = graph.getTriples().map(TestWritingGraph::tripleAsString);
        Files.write(graphFile, stream::iterator, Charset.forName("UTF-8"));
    }

    @Test
    public void writeGraphFromStreamFiltered() throws Exception {
        Path graphFile = Files.createTempFile("graph", ".nt");
        if (KEEP_FILES) {
            System.out.println("Filtered stream: " + graphFile);
        } else {
            graphFile.toFile().deleteOnExit();
        }

        IRI subject = factory.createIRI("subj");
        IRI predicate = factory.createIRI("pred");
        Stream<CharSequence> stream = graph
                .getTriples(subject, predicate, null).map(TestWritingGraph::tripleAsString);
        Files.write(graphFile, stream::iterator, Charset.forName("UTF-8"));

    }

    @Test
    public void writeGraphFromStreamFilteredNoMatches() throws Exception {
        Path graphFile = Files.createTempFile("graph-empty-", ".nt");
        if (KEEP_FILES) {
            System.out.println("Filtered stream: " + graphFile);
        } else {
            graphFile.toFile().deleteOnExit();
        }

        IRI subject = factory.createIRI("nonexistent");
        IRI predicate = factory.createIRI("pred");
        Stream<CharSequence> stream = graph
                .getTriples(subject, predicate, null).map(Object::toString);
        Files.write(graphFile, stream::iterator, Charset.forName("UTF-8"));

    }

}
