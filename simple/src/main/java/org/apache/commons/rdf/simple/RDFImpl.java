package org.apache.commons.rdf.simple;

import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFContext;

public class RDFImpl implements RDF {
    private final RDFContext context;

    public RDFImpl(RDFContext context) {
        this.context=context;
    }

    public RDFContext getContext() {
        return context;
    }
}
