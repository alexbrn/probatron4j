/*
 * This file is part of the source of
 * 
 * Probatron4J - a Schematron validator for Java(tm)
 * 
 * Copyright (C) 2009 Griffin Brown Digitial Publishing Ltd
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.probatron;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.UUID;

import net.sf.saxon.om.SequenceIterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class Session
{
    static Logger logger = LogManager.getLogger( Session.class.getName());

    private boolean physicalLocators = true;
    private String phase;
    private String schemaSysId;
    private SchematronSchema theSchema;
    private UUID uuid;
    private String candidateSysId;
    private int reportFormat;
    private String fsContextDir;

    private ValidationContext validationContext;


    public Session()
    {
        logger.debug( "Session created" );
        uuid = UUID.randomUUID();
        Runtime.registerSession( this );
    }


    public ValidationReport doValidation( String candidate ) throws MalformedURLException,
            SAXException, IOException
    {
        logger.debug( "Session validating ..." );
        ValidationReport vr = null;
        this.candidateSysId = candidate;

        theSchema = new SchematronSchema( this );

        synchronized( Session.class )
        {
            // gets some metadata about the instance to set a context
            // object used by some XPath extension functions
            URL candidateUrl = new URL( candidate );
            ValidationContext vc = analyzeCandidate( candidateUrl );
            vc.setVerbatimName( candidate );

            this.setValidationContext( vc );

            vr = theSchema.validateCandidate( candidateUrl );

            if( physicalLocators )
            {
                vr.annotateWithLocators( this, candidateUrl );
            }

            if( getReportFormat() == ValidationReport.REPORT_SVRL_MERGED )
            {
                vr.mergeSvrlIntoCandidate( this, candidateUrl );
            }

            return vr;
        }

    }


    public String getFsContextDir()
    {
        return fsContextDir;
    }


    public void setFsContextDir( String fsContextDir )
    {
        this.fsContextDir = fsContextDir;
    }


    private ValidationContext analyzeCandidate( URL url ) throws SAXException, IOException
    {
        ValidationContext vc = new ValidationContext();
        CandidateAnalyzer ca = new CandidateAnalyzer( vc );

        XMLReader parser = XMLReaderFactory.createXMLReader();
        parser.setContentHandler( ca );
        parser.parse( url.toString() );

        return vc;
    }


    public String getPhase()
    {
        return phase;
    }


    public void setPhase( String phase )
    {
        this.phase = phase;
        logger.debug( "Using phase: " + phase );
    }


    public String getSchemaDoc()
    {
        return schemaSysId;
    }


    public void setSchemaSysId( String schemaDoc )
    {
        this.schemaSysId = schemaDoc;
        logger.debug( "Schema document is:" + schemaDoc );
    }


    public boolean usesPhysicalLocators()
    {
        return physicalLocators;
    }


    public void setUsePhysicalLocators( boolean physicalLocators )
    {
        this.physicalLocators = physicalLocators;
        logger.debug( "Setting option (use physical locators): " + physicalLocators );
    }


    public UUID getUuid()
    {
        return uuid;
    }


    public int getReportFormat()
    {
        return reportFormat;
    }


    public void setReportFormat( int reportFormat )
    {
        this.reportFormat = reportFormat;
        logger.debug( "Setting option (report format): " + reportFormat );
    }


    public ValidationContext getValidationContext()
    {
        return validationContext;
    }


    public void setValidationContext( ValidationContext validationContext )
    {
        this.validationContext = validationContext;
    }


    public String getSchemaSysId()
    {
        return schemaSysId;
    }


    public String getCandidateSysId()
    {
        return candidateSysId;
    }


    public URI getCandidateAsUri()
    {
        String cand = candidateSysId;
        cand = cand.replaceAll( "file:", "" );
        File candidateFile = new File( getFsContextDir(), cand );
        logger.debug( "Candidate file is " + candidateFile.getAbsolutePath() );
        return candidateFile.toURI();
    }

}
